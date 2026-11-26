package fleetkit.features.recentBattles

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.CampaignUIAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin
import com.fs.starfarer.api.combat.BattleCreationContext
import com.fs.starfarer.api.combat.CombatEngineAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.combat.CombatEngine
import fleetkit.core.config.FTVariables
import fleetkit.core.util.DisplayMessage
import fleetkit.otherMods.starficz.ReflectionUtils
import fleetkit.otherMods.starficz.ReflectionUtils.get
import fleetkit.otherMods.starficz.ReflectionUtils.getConstructorsMatching
import fleetkit.otherMods.starficz.ReflectionUtils.getFieldsMatching
import fleetkit.otherMods.starficz.ReflectionUtils.set
import fleetkit.otherMods.starficz.ReflectionUtilsSafe.safeSet
import fleetkit.serialization.entities.fleet.DataFleet.toParsedData
import fleetkit.serialization.entities.fleet.FleetSettings
import org.magiclib.util.taskscheduler.SystemTaskScheduler

// Initial code taken from Ship Mastery System by float

/**
 * Runs a campaign battle as a simulation.
 *
 * Like the vanilla refit-tab simulator, the player's real ships never take part, the combat is done with throwaway copies.
 *
 * Callers do not need an interaction dialog, a dummy dialog or a particular campaign screen. [simulateBattle] works from:
 *  1. a real interaction dialog,
 *  2. a core UI screen (fleet, cargo, intel...),
 *  3. the bare campaign map.
 */
object RecentBattleSimulator {

    const val IS_SIMULATOR_KEY = "${FTVariables.PREFIX}_isSimulator"

    private const val ENCOUNTER_DIALOG_FIELD = "encounterDialog"
    private const val ENEMY_FLEET_FIELD = "enemyFleetForBattle"
    private const val SCREEN_PANEL_FIELD = "screenPanel"

    /**
     * True from the moment a simulation starts until it has been cleaned up.
     *
     * Campaign listeners should ignore engagements while this is set.
     */
    @Volatile
    var isSimulating = false
        private set

    private var encounterDialogConstructor: ReflectionUtils.ReflectedConstructor? = null

    private fun findEncounterDialogConstructor(campUI: CampaignUIAPI): ReflectionUtils.ReflectedConstructor {
        encounterDialogConstructor?.let { return it }

        val dialogClass = campUI.getFieldsMatching(ENCOUNTER_DIALOG_FIELD).singleOrNull()?.type
            ?: error("CampaignState.$ENCOUNTER_DIALOG_FIELD not found")

        return dialogClass.getConstructorsMatching(numOfParams = 4).singleOrNull()
            ?.also { encounterDialogConstructor = it }
            ?: error("Encounter dialog constructor with 4 parameters not found")
    }

    /** Builds a dialog object that is never shown, it only has to sit in CampaignState.encounterDialog. */
    private fun createHiddenEncounterDialog(campUI: CampaignUIAPI, plugin: InteractionDialogPlugin): Any {
        val screenPanel = campUI.get(SCREEN_PANEL_FIELD)
        return findEncounterDialogConstructor(campUI).newInstance(null, plugin, screenPanel, campUI)
    }

    // ---------------------------------------------------------------------------------------------------------------

    fun createSimulationFleet(realFleet: CampaignFleetAPI): CampaignFleetAPI {
        val simFleet = realFleet.toParsedData(filterParsed = false).toCampaignFleet(FleetSettings())
        simFleet.containingLocation = realFleet.containingLocation // Required
        simFleet.setLocation(realFleet.location.x, realFleet.location.y) // For environmental effects

        /* if (CommanderShuttle.playerShuttleExists(realFleet)) {
            val shuttleMember = CommanderShuttle.createCommanderShuttle()
            simFleet.fleetData.addFleetMember(shuttleMember)
            simFleet.fleetData.setFlagship(shuttleMember)
            shuttleMember.repairTracker.isMothballed = true
            shuttleMember.repairTracker.cr = shuttleMember.repairTracker.maxCR
        } */

        return simFleet
    }

    /**
     * Owns everything about getting the campaign back to how it was.
     */
    private class SimulationDialogPlugin(
        private val campUI: CampaignUIAPI,
        private val onBackFromEngagement: () -> Unit
    ) : InteractionDialogPlugin {

        private val sector = Global.getSector()

        // What the campaign looked like before. Null when no interaction dialog was open (core UI screen or bare map).
        private val previousDialog: InteractionDialogAPI? = campUI.currentInteractionDialog

        // The only campaign state the battle return flow touches is the last-battle info, which
        // CampaignState.prepare() sets after every engagement, so it is the only thing saved here.
        private val lastBattleTimestamp = sector.lastPlayerBattleTimestamp
        private val lastBattleWon = sector.isLastPlayerBattleWon
        private val wasPaused = sector.isPaused

        // Set when the battle had to open its own dialog on the bare map, so it can be closed again afterwards.
        private var ownDialog: InteractionDialogAPI? = null

        /**
         * CampaignState needs a non-null encounterDialog and a non-null dialogType to route the battle result here.
         *
         * 1. Interaction dialog already open: swap it for a hidden one, put it back afterwards.
         * 2. Core UI screen open: showInteractionDialog refuses, so the hidden dialog is installed anyway.
         * 3. Bare map: show a real (empty) dialog, which sets the dialog type for us.
         */
        fun install() {
            if (previousDialog == null && campUI.showInteractionDialog(this, null)) {
                ownDialog = campUI.currentInteractionDialog
            } else {
                campUI.set(ENCOUNTER_DIALOG_FIELD, createHiddenEncounterDialog(campUI, this))
            }
        }

        /** Undoes [install] when the simulation never got to run. */
        fun uninstall() {
            val own = ownDialog
            if (own != null) own.dismiss() else campUI.set(ENCOUNTER_DIALOG_FIELD, previousDialog)
        }

        override fun backFromEngagement(battleResult: EngagementResultAPI?) {
            // This runs inside CampaignState.prepare(), an exception here would take the game down with it.
            // Note: battleResult is malformed for a simulation (battleResult.battle is null), do not use it.
            try {
                // Without a real enemy fleet set, CampaignState does not switch the music.
                campUI.set(ENEMY_FLEET_FIELD, null)

                // Put back whatever dialog was there (null for a core UI screen).
                if (ownDialog == null) campUI.set(ENCOUNTER_DIALOG_FIELD, previousDialog)
            } catch (e: Exception) {
                Global.getLogger(RecentBattleSimulator::class.java).error("Failed to restore campaign dialog: ", e)
            }

            // prepare() writes the last-battle info right after this call, so the rest has to wait.
            SystemTaskScheduler.performLater { finish() }
        }

        private fun finish() {
            try {
                sector.lastPlayerBattleTimestamp = lastBattleTimestamp
                sector.isLastPlayerBattleWon = lastBattleWon

                ownDialog?.dismiss()
                sector.isPaused = if (ownDialog != null) wasPaused else true
            } catch (e: Exception) {
                Global.getLogger(RecentBattleSimulator::class.java).error("Failed to clean up after simulation: ", e)
            } finally {
                isSimulating = false
            }

            try {
                onBackFromEngagement()
            } catch (e: Exception) {
                Global.getLogger(RecentBattleSimulator::class.java).error("onBackFromEngagement callback failed: ", e)
            }
        }

        override fun init(dialog: InteractionDialogAPI?) {}
        override fun optionSelected(optionText: String?, optionData: Any?) {}
        override fun optionMousedOver(optionText: String?, optionData: Any?) {}
        override fun advance(amount: Float) {}
        override fun getContext(): Any? = null
        override fun getMemoryMap(): MutableMap<String, MemoryAPI>? = null
    }

    /**
     * @param bcc the battle to simulate. Recommend using [createSimulationFleet] over the fleets before passing them into the bcc to avoid changing their contents.
     * @param onBackFromEngagement called once the campaign is back in its previous state, after the simulation was cleaned up.
     */
    fun simulateBattle(
        bcc: BattleCreationContext,
        onBackFromEngagement: () -> Unit = {}
    ) {
        val sector = Global.getSector() ?: return
        if (isSimulating) return // One simulation at a time.

        val returnPlugin = SimulationDialogPlugin(sector.campaignUI, onBackFromEngagement)

        try {
            returnPlugin.install()
            sector.isPaused = true
            isSimulating = true

            sector.campaignUI.startBattle(bcc)

            // startBattle builds a brand new CombatEngine (the loader destroys the old instance), so the engine has to be fetched after it, not before.
            val engine = CombatEngine.getInstance()
            engine.customData[IS_SIMULATOR_KEY] = true

            engine.playerFleetManager.campaignFleet = null // Like the vanilla simulator, run without a campaign fleet on the player side.

            engine.addPlugin(object : BaseEveryFrameCombatPlugin() {
                private var removedConfirm = false

                override fun advance(amount: Float, events: MutableList<InputEventAPI>?) {
                    if (engine.isEnemyInFullRetreat && !removedConfirm) {
                        removedConfirm = true
                        engine.setCustomExit(
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

        } catch (e: Exception) {
            isSimulating = false
            runCatching { returnPlugin.uninstall() }
            DisplayMessage.showError("Cannot simulate battle: ${e.message}")
            Global.getLogger(this.javaClass).error("Replay battle failed: ", e)
        }
    }
}