package fleetkit.features.recentBattles

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.combat.BattleCreationContext
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.combat.CombatEngine
import fleetkit.core.config.FTVariables
import fleetkit.core.util.DisplayMessage
import fleetkit.features.commanderShuttle.CommanderShuttle
import fleetkit.otherMods.starficz.ReflectionUtils.set
import fleetkit.otherMods.starficz.ReflectionUtilsSafe.safeInvoke
import fleetkit.otherMods.starficz.ReflectionUtilsSafe.safeSet
import fleetkit.serialization.entities.fleet.DataFleet.toParsedData
import fleetkit.util.api.CampaignUtils
import org.magiclib.util.taskscheduler.CombatTaskScheduler
import org.magiclib.util.taskscheduler.SectorTaskScheduler
import org.magiclib.util.taskscheduler.SystemTaskScheduler

// Initial code taken from Ship Mastery System by float

/**
 * Runs a campaign battle as a simulation.
 *
 * Like the vanilla refit-tab simulator, the player's real ships never take part, the combat is done with throwaway copies.
 */
object RecentBattleReplay {

    const val IS_SIMULATOR_KEY = "${FTVariables.PREFIX}_isSimulator"

    /**
     * True from the moment a simulation starts until it has been cleaned up.
     *
     * Campaign listeners should ignore engagements while this is set.
     */
    @Volatile
    var isSimulating = false
        private set

    private fun createSimulationFleet(): CampaignFleetAPI {
        val realFleet = Global.getSector().playerFleet

        val simFleet = realFleet.toParsedData(filterParsed = false).toCampaignFleet()
        simFleet.containingLocation = realFleet.containingLocation
        simFleet.setLocation(realFleet.location.x, realFleet.location.y)

        if (CommanderShuttle.playerShuttleExists(realFleet)) {
            val shuttleMember = CommanderShuttle.createCommanderShuttle()
            simFleet.fleetData.addFleetMember(shuttleMember)
            simFleet.fleetData.setFlagship(shuttleMember)
            shuttleMember.repairTracker.isMothballed = true
            shuttleMember.repairTracker.cr = shuttleMember.repairTracker.maxCR
        }

        return simFleet
    }

    /**
     * @param bcc the battle to simulate. Its player fleet is ignored and replaced by a copy of the real one, so it is fine to build it with `sector.playerFleet`.
     */
    fun simulateBattle(
        bcc: BattleCreationContext,
        onBackFromEngagement: () -> Unit = {}
    ) {
        try {
            val sector = Global.getSector() ?: return
            if (isSimulating) return // One simulation at a time.

            var previousEncounterDialog: InteractionDialogAPI? = null

            // The only campaign state the battle return flow touches is the last-battle info, which
            // CampaignState.prepare() sets after every engagement, so it is the only thing saved here.
            val lastBattleTimestamp = sector.lastPlayerBattleTimestamp
            val lastBattleWon = sector.isLastPlayerBattleWon

            fun onBackFromEngagement(fromDummyDialog: Boolean) {
                val campUI = sector.campaignUI

                CombatEngine.getInstance()?.customData?.remove(IS_SIMULATOR_KEY)

                campUI.set("enemyFleetForBattle", null)
                campUI.safeInvoke("setEncounterDialog", previousEncounterDialog)
                if (!fromDummyDialog) {
                    previousEncounterDialog?.safeInvoke("makeOptionInstant", 0)
                    previousEncounterDialog?.safeInvoke("dismiss", 0)
                    sector.isPaused = true
                }

                SystemTaskScheduler.performLater {
                    sector.lastPlayerBattleTimestamp = lastBattleTimestamp
                    sector.isLastPlayerBattleWon = lastBattleWon
                    isSimulating = false

                    for (member in sector.playerFleet.fleetData.membersListCopy) {
                        if (member.repairTracker.isMothballed && member.variant.hasHullMod(FTVariables.COMMAND_SHUTTLE_ID)) {
                            val maxCR = member.repairTracker.maxCR
                            member.repairTracker.isMothballed = false
                            member.repairTracker.cr = maxCR
                        }
                    }

                    onBackFromEngagement.invoke()
                }
            }

            CampaignUtils.closeCampaignDummyDialog()
            val dummyOpen = CampaignUtils.openCampaignDummyDialog(isInteractionDialog = true) {
                onBackFromEngagement(true)
            }

            val campUI = sector.campaignUI

            if (campUI.currentInteractionDialog == null) {
                DisplayMessage.showError("Cannot simulate battle, no interaction dialog found.")
                CampaignUtils.closeCampaignDummyDialog()
                return
            }

            // Only register this once we know the simulation will actually start, otherwise the callback would linger and fire after the next real battle.
            if (!dummyOpen) {
                SectorTaskScheduler.performAfterPlayerBattle {
                    onBackFromEngagement(false)
                }
            }

            previousEncounterDialog = campUI.currentInteractionDialog
            isSimulating = true

            bcc.safeSet("playerFleet", createSimulationFleet())

            campUI.safeInvoke("setEncounterDialog", campUI.currentInteractionDialog)
            campUI.startBattle(bcc)

            // startBattle builds a brand new CombatEngine (the loader destroys the old instance), so the engine has to be fetched after it, not before.
            val engine = CombatEngine.getInstance()
            engine.customData[IS_SIMULATOR_KEY] = true

            engine.playerFleetManager.campaignFleet = null // Like the vanilla simulator, run without a campaign fleet on the player side.

            CombatTaskScheduler.performOnPlayerBattleStart {
                val combatEngine = CombatEngine.getInstance()

                combatEngine.addPlugin(object : BaseEveryFrameCombatPlugin() {
                    private var removedConfirm = false

                    override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {
                        if (combatEngine.isEnemyInFullRetreat && !removedConfirm) {
                            removedConfirm = true
                            combatEngine.setCustomExit(
                                "End Simulation",
                                null
                            )
                        }
                    }

                    override fun init(engine: CombatEngineAPI) {
                        engine.setCustomExit(
                            "End Simulation",
                            "Exit the simulation?"
                        )
                    }
                })
            }

        } catch (e: Exception) {
            isSimulating = false
            CampaignUtils.closeCampaignDummyDialog()
            Global.getSector()?.campaignUI?.currentInteractionDialog?.dismiss()
            Global.getLogger(this.javaClass).error("Replay battle failed: ", e)
        }
    }
}