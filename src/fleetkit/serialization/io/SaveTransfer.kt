package fleetkit.serialization.io

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.impl.campaign.ids.*
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator.addNonSalvageEntity
import com.fs.starfarer.api.impl.campaign.submarkets.LocalResourcesSubmarketPlugin
import com.fs.starfarer.api.util.IntervalUtil
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CampaignUIPersistentData
import fleetkit.core.config.FTVariables
import fleetkit.core.util.DisplayMessage
import fleetkit.core.util.FTTxt
import fleetkit.otherMods.starficz.ReflectionUtilsSafe.safeInvoke
import fleetkit.serialization.content.MissingContentExtended
import fleetkit.serialization.dictionaries.PackDictionaryV1
import fleetkit.serialization.entities.cargo.JSONCargo
import fleetkit.serialization.entities.cargo.LegacyCargoCode
import fleetkit.serialization.entities.fleet.DataFleet
import fleetkit.serialization.entities.fleet.DataFleet.toParsedData
import fleetkit.serialization.entities.fleet.FleetSettings
import fleetkit.serialization.entities.fleet.FleetJson
import fleetkit.serialization.entities.person.PersonJson
import fleetkit.serialization.entities.person.PersonSettings
import fleetkit.util.api.FleetUtils
import fleetkit.util.api.kotlin.optJSONArrayToStringList
import fleetkit.util.lib.CompressionUtil
import org.json.JSONArray
import org.json.JSONObject
import org.lazywizard.lazylib.MathUtils
import org.lazywizard.lazylib.ext.json.optFloat
import org.lwjgl.util.vector.Vector2f
import org.magiclib.kotlin.getStorageCargo
import org.magiclib.kotlin.toStringList
import org.magiclib.util.MagicCampaign
import org.magiclib.util.api.getMarkets
import kotlin.math.cos
import kotlin.math.sin


object SaveTransfer {

    private val hullDictionary = PackDictionaryV1.hulls.joinToString("")
    private val wingDictionary = PackDictionaryV1.wings.joinToString("")
    private val weaponDictionary = PackDictionaryV1.weapons.joinToString("")

    class RemoveEmptyStation(private val station: SectorEntityToken) : EveryFrameScript {
        var isEmpty = false
        override fun isDone() = isEmpty
        override fun runWhilePaused() = false
        private val interval = IntervalUtil(0.1f, 0.1f)
        override fun advance(amount: Float) {
            val days = Global.getSector().clock.convertToDays(amount)
            interval.advance(days)
            if (!interval.intervalElapsed()) return

            if (!station.isVisibleToPlayerFleet) return

            val storage = station.market?.getStorageCargo() ?: return
            if (storage.isEmpty && (storage.mothballedShips == null || storage.mothballedShips.numMembers == 0)) {
                isEmpty = true
                station.containingLocation.removeEntity(station)

                @Suppress("UNCHECKED_CAST")
                val saveTransferStations = Global.getSector().persistentData["${FTVariables.PREFIX}_SaveTransferStations"] as? MutableSet<String>
                    ?: run { Global.getLogger(this.javaClass).warn("Null data that should not be null"); mutableSetOf() }

                saveTransferStations.remove(station.id)
                if (saveTransferStations.isEmpty())
                    Global.getSector().persistentData.remove("${FTVariables.PREFIX}_SaveTransferStations")
                else
                    Global.getSector().persistentData["${FTVariables.PREFIX}_SaveTransferStations"] = saveTransferStations
            }
        }
    }

    internal fun beforeGameSave() {
        @Suppress("UNCHECKED_CAST")
        val saveTransferStations = Global.getSector().persistentData["${FTVariables.PREFIX}_SaveTransferStations"] as? MutableSet<String>
        saveTransferStations?.forEach { entityID ->
            val entity = Global.getSector().getEntityById(entityID) ?: return@forEach
            if (entity.hasScriptOfClass(RemoveEmptyStation::class.java))
                entity.removeScriptsOfClass(RemoveEmptyStation::class.java)
        }
    }

    internal fun afterGameSave() {
        @Suppress("UNCHECKED_CAST")
        val saveTransferStations = Global.getSector().persistentData["${FTVariables.PREFIX}_SaveTransferStations"] as? MutableSet<String>
        saveTransferStations?.forEach { entityID ->
            val entity = Global.getSector().getEntityById(entityID) ?: return@forEach
            if (!entity.hasScriptOfClass(RemoveEmptyStation::class.java))
                entity.addScript(RemoveEmptyStation(entity))
        }
    }

    data class AbilityPair(
        var abilityId: String? = null,
        var inHyperAbilityId: String? = null
    )

    fun createSaveJson(
        handleCargo: Boolean = true,
        handleSubmarketCargo: Boolean = true,
        handleRelations: Boolean = true,
        handleKnownBlueprints: Boolean = true,
        handlePlayer: Boolean = true,
        handleFleet: Boolean = true,
        handleCredits: Boolean = true,
        handleKnownHullmods: Boolean = true,
        handleOfficers: Boolean = true,
        handleAbilityBar: Boolean = true,
        showErrorMessages: Boolean = true,
    ): String {
        val json = JSONObject()
        val sector = Global.getSector() ?: return ""
        val playerFleet = sector.playerFleet ?: return ""

        if (handleCargo) {
            //val cargoJson = CargoSerialization.saveCargoToJson(playerFleet.cargo.stacksCopy)
            //json.put("cargo", cargoJson)
            val cargoComp = LegacyCargoCode.saveCargoToCompString(playerFleet.cargo.stacksCopy, showErrorMessages = showErrorMessages)
            json.put("cargo", cargoComp)
        }

        if (handleSubmarketCargo) {
            try {
                val markets = sector.getMarkets()
                val checkedStorages = mutableSetOf<CargoAPI>()

                @Suppress("UNCHECKED_CAST")
                val saveTransferStations = sector.persistentData["${FTVariables.PREFIX}_SaveTransferStations"] as? MutableSet<String>

                val marketsArray = JSONArray()
                for (market in markets) {
                    //if (market.isHidden)
                    //    continue
                    //if (market.surveyLevel != MarketAPI.SurveyLevel.SEEN && market.surveyLevel != MarketAPI.SurveyLevel.FULL)
                    //    continue

                    if (saveTransferStations?.contains(market.primaryEntity.id) == true)
                        continue
                    if (market.memoryWithoutUpdate.isEmpty)
                        continue
                    if (market.memoryWithoutUpdate.contains("\$isPlayerOwned") && !market.memoryWithoutUpdate.getBoolean("\$isPlayerOwned") && !market.memoryWithoutUpdate.getBoolean("\$isSurveyed"))
                        continue

                    val submarketArray = JSONArray()
                    for (submarket in market.submarketsCopy) {
                        val plugin = submarket.plugin

                        if (!plugin.isFreeTransfer)
                            continue
                        if (plugin is LocalResourcesSubmarketPlugin)
                            continue

                        val cargo = plugin.cargoNullOk ?: continue
                        if (cargo.isEmpty && (cargo.mothballedShips == null || cargo.mothballedShips.numMembers == 0))
                            continue

                        if (cargo in checkedStorages)
                            continue

                        checkedStorages.add(cargo)

                        val submarketContentsObject = JSONObject()
                        if (!cargo.isEmpty)
                            submarketContentsObject.put("c", LegacyCargoCode.saveCargoToCompString(cargo.stacksCopy, showErrorMessages = showErrorMessages))
                        if (cargo.mothballedShips != null && cargo.mothballedShips.numMembers > 0)
                            submarketContentsObject.put("f", cargo.mothballedShips.toParsedData().toCode(withPrepend = false))
                        submarketArray.put(submarket.nameOneLine)
                        submarketArray.put(submarketContentsObject)
                    }
                    if (submarketArray.length() > 0) {
                        marketsArray.put(market.name)
                        marketsArray.put(submarketArray)
                    }
                }
                if (marketsArray.length() > 0)
                    json.put("submarket_cargos", marketsArray)

            } catch (e: Exception) {
                DisplayMessage.showError("Failed to save submarket cargos", e)
            }
        }

        if (handleRelations) {
            val relationsJson = JSONObject()
            for (faction in sector.allFactions) {
                relationsJson.put(faction.id, faction.relToPlayer.rel)
            }
            json.put("relations", relationsJson)
        }

        if (handleKnownBlueprints) {
            val playerFaction = sector.playerFaction

            if (playerFaction.knownShips.isNotEmpty())
                json.put("shipBlueprints", CompressionUtil.base64Deflate(playerFaction.knownShips.joinToString(separator = " "), dictionary = hullDictionary))
            if (playerFaction.knownFighters.isNotEmpty())
                json.put("fighterBlueprints", CompressionUtil.base64Deflate(playerFaction.knownFighters.joinToString(separator = " "), dictionary = wingDictionary))
            if (playerFaction.knownWeapons.isNotEmpty())
                json.put("weaponBlueprints", CompressionUtil.base64Deflate(playerFaction.knownWeapons.joinToString(separator = " "), dictionary = weaponDictionary))
        }

        if (handlePlayer) {
            val playerJson = PersonJson.savePersonToJson(sector.playerPerson, settings = PersonSettings().apply {
                includeMemKeys = false
            })
            playerJson.put("storyPoints", sector.playerStats.storyPoints)
            json.put("player", playerJson)
        }

        if (handleFleet) {
            val compFleet = playerFleet.toParsedData(FleetSettings().apply {
                    includeCommanderSetFlagship = false
                    includeCommanderAsOfficer = false
                    memberSettings.includeOfficer = handleOfficers
                    includeIdleOfficers = handleOfficers
                }).toCode(withPrepend = false)

            json.put("fleet", compFleet)
        }

        if (handleCredits) {
            val credits = playerFleet.cargo.credits
            json.put("credits", credits.get())
        }

        if (handleKnownHullmods) {
            val hullMods = sector.characterData?.hullMods
            json.put("knownHullMods", JSONArray(hullMods))
        }

        if (handleAbilityBar) {
            val bars: Array<Array<CampaignUIPersistentData.AbilitySlot>> = try {
                @Suppress("UNCHECKED_CAST")
                sector.uiData.abilitySlotsAPI.safeInvoke("getSlots") as Array<Array<CampaignUIPersistentData.AbilitySlot>>
            } catch (e: Exception) {
                DisplayMessage.showError("Failed to save Ability Bar")
                emptyArray()
            }

            val savedBars = List(bars.size) { List(10) { AbilityPair() } }

            bars.forEachIndexed { barIndex, bar ->
                bar.forEach { slot ->
                    if (slot.slotId >= 10) return@forEach
                    savedBars[barIndex][slot.slotId].apply {
                        if (!slot.abilityId.isNullOrEmpty()) abilityId = slot.abilityId
                        if (!slot.inHyperAbilityId.isNullOrEmpty()) inHyperAbilityId = slot.inHyperAbilityId
                    }
                }
            }


            fun convertSavedBarsToJson(savedBars: List<List<AbilityPair>>): JSONArray {
                val barsJson = JSONArray()

                savedBars.forEachIndexed { barIndex, bar ->
                    val barObject = JSONObject()
                    barObject.put("barIndex", barIndex)

                    val slotsJson = JSONArray()
                    bar.forEachIndexed { slotId, pair ->
                        if (!pair.abilityId.isNullOrEmpty() || !pair.inHyperAbilityId.isNullOrEmpty()) {
                            val slotArray = JSONArray().apply {
                                put(pair.abilityId ?: "")
                                put(pair.inHyperAbilityId ?: "")
                            }

                            val slotObject = JSONObject().apply {
                                put("slotId", slotId)
                                put("values", slotArray)
                            }

                            slotsJson.put(slotObject)
                        }
                    }

                    // Only add the bar if it has non-empty slots
                    if (slotsJson.length() > 0) {
                        barObject.put("slots", slotsJson)
                        barsJson.put(barObject)
                    }
                }

                return barsJson
            }

            json.put("abilityBars", convertSavedBarsToJson(savedBars))

            json.put("abilities", JSONArray(sector.characterData.abilities))
        }

        return json.toString(4)
    }

    data class MarketCargo(
        val marketName: String,
        val submarkets: List<SubmarketCargo>
    )

    data class SubmarketCargo(
        val submarketName: String,
        val fleet: FleetDataAPI?,
        val cargo: CargoAPI?,
    )

    data class CompiledSave(
        var fleet: CampaignFleetAPI? = null,
        var aggressionDoctrine: DataFleet.FleetAggressionV1? = null,
        var player: PersonAPI? = null,
        var cargo: CargoAPI? = null,
        var marketCargos: List<MarketCargo>? = null,
        var relations: Map<String, Float>? = null,
        var shipBlueprints: List<String> = listOf(),
        var fighterBlueprints: List<String> = listOf(),
        var weaponBlueprints: List<String> = listOf(),
        var hullMods: List<String>? = null,
        var abilityBars: List<List<AbilityPair>>? = null,
        var abilities: List<String>? = null,
    ) {
        fun isEmpty(): Boolean {
            return fleet == null &&
                    aggressionDoctrine == null &&
                    player == null &&
                    cargo == null &&
                    relations.isNullOrEmpty() &&
                    shipBlueprints.isEmpty() &&
                    fighterBlueprints.isEmpty() &&
                    weaponBlueprints.isEmpty() &&
                    hullMods.isNullOrEmpty() &&
                    abilityBars.isNullOrEmpty() &&
                    abilities.isNullOrEmpty()
        }
    }

    @JvmOverloads
    fun compileSaveAny(
        value: Any?,
        missing: MissingContentExtended = MissingContentExtended()
    ): CompiledSave {
        return when (value) {
            is JSONObject -> compileSaveJson(value, missing)
            is String -> compileSaveString(value, missing)
            else -> CompiledSave()
        }
    }

    @JvmOverloads
    fun compileSaveString(
        value: String,
        missing: MissingContentExtended = MissingContentExtended()
    ): CompiledSave {
        val json = JSONInput.getJSONFromStringSafe(value)
        if (json != null) {
            return compileSaveJson(json, missing)
        } else {
            if (value.contains("\$z0$")) {
                val valueSub = value.substringAfter("\$z0$")
                val valueDecomp = CompressionUtil.base64Inflate(valueSub)
                if (valueDecomp != null) {
                    val json = JSONInput.getJSONFromStringSafe(valueDecomp)
                    if (json != null)
                        return compileSaveJson(json, missing)
                }
            }
        }
        return CompiledSave()
    }

    @JvmOverloads
    fun compileSaveJson(
        json: JSONObject,
        missing: MissingContentExtended = MissingContentExtended()
    ): CompiledSave {
        val compiled = CompiledSave()

        val sector = Global.getSector() ?: return compiled

        val cargo = Global.getFactory().createCargo(true)

        if (json.has("cargo")) {
            try {
                val cargoComp = json.optString("cargo", "")
                if (!cargoComp.isNullOrBlank() && LegacyCargoCode.isCompressedCargo(cargoComp)) {
                    missing.add(LegacyCargoCode.getCargoFromCompString(cargoComp, cargo))
                } else {
                    json.optJSONObject("cargo")?.let { // New
                        missing.add(JSONCargo.getCargoFromJson(it, cargo))
                    } ?: json.getJSONArray("cargo")?.let { // Legacy
                        missing.add(JSONCargo.getCargoFromJson(it, cargo))
                    }
                }
            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_cargo"), e)
            }
        }

        if (json.has("submarket_cargos")) {
            try {
                val marketsArray = json.optJSONArray("submarket_cargos")
                val marketCargos = mutableListOf<MarketCargo>()

                for (i in 0 until marketsArray.length() step 2) {
                    val marketName = marketsArray.get(i) as String
                    val submarketsArray = marketsArray.get(i + 1) as JSONArray

                    val submarketCargo = mutableListOf<SubmarketCargo>()
                    for (i in 0 until submarketsArray.length() step 2) {
                        val submarketName = submarketsArray.get(i) as String
                        val submarketContentsObject = submarketsArray.get(i + 1) as JSONObject

                        val cargoComp = submarketContentsObject.optString("c", "")
                        val fleetComp = submarketContentsObject.optString("f", "")
                        var realCargo: CargoAPI? = null
                        var realFleet: FleetDataAPI? = null

                        if (cargoComp.isNotEmpty()) {
                            realCargo = Global.getFactory().createCargo(true)
                            missing.add(LegacyCargoCode.getCargoFromCompString(cargoComp, realCargo))
                        }
                        if (fleetComp.isNotEmpty()) {
                            realFleet = (CodeImporter.extractDataFromString(fleetComp, missing = missing)?.toEntity(missing = missing) as? CampaignFleetAPI)?.fleetData
                        }

                        submarketCargo.add(SubmarketCargo(submarketName, realFleet, realCargo))
                    }
                    if (submarketCargo.isNotEmpty())
                        marketCargos.add(MarketCargo(marketName, submarketCargo))
                }

                if (marketCargos.isNotEmpty())
                    compiled.marketCargos = marketCargos

            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_submarket_cargo"), e)
            }
        }

        if (json.has("credits")) {
            try {
                cargo.credits.add(json.optDouble("credits", 0.0).toFloat())
            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_credits"), e)
            }
        }

        if (cargo.credits.get() > 0 || !cargo.isEmpty)
            compiled.cargo = cargo


        if (json.has("relations")) {
            try {
                val relationsMap: MutableMap<String, Float> = mutableMapOf()

                val relations = json.getJSONObject("relations")
                for (factionItem in sector.allFactions) {
                    if (relations.has(factionItem.id)) {
                        relationsMap[factionItem.id] = relations.optFloat(factionItem.id, 0f)
                    }
                }
                compiled.relations = relationsMap
            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_relations"), e)
            }
        }

        if (json.has("player")) {
            try {
                val playerJson = json.getJSONObject("player")
                val loadedPlayer = PersonJson.getPersonFromJson(playerJson, missing = missing)


                compiled.player = loadedPlayer
            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_player"), e)
            }
        }

        if (json.has("fleet")) {
            try {
                val fleetJson = json.optJSONObject("fleet")
                val parsedData = if (fleetJson == null) {
                    val compFleet = json.getString("fleet")

                    CodeImporter.extractDataFromString(compFleet, missing = missing) as? DataFleet.ParsedFleetData ?: throw Exception()
                } else {
                    FleetJson.extractFleetDataFromJson(fleetJson, missing)
                }

                val fleet = DataFleet.createCampaignFleetFromData(
                    parsedData,
                    FleetSettings().apply {
                        includeCommanderSetFlagship = false
                        includeCommanderAsOfficer = false
                        includeAggression = false
                    },
                    missing
                )
                if (parsedData.fleetAggression != null)
                    compiled.aggressionDoctrine = parsedData.fleetAggression
                //else
                //    compiled.aggressionDoctrine = DataFleet.FleetAggressionV1.STEADY

                compiled.fleet = fleet
            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_fleet"), e)
            }
        }

        if (json.has("knownHullMods")) {
            val hullModsJson = json.optJSONArray("knownHullMods") ?: JSONArray()
            val hullMods = mutableListOf<String>()
            for (i in 0 until hullModsJson.length()) {
                val modId = hullModsJson.optString(i, null) ?: continue
                val spec = runCatching { Global.getSettings().getHullModSpec(modId) }.getOrNull()
                if (spec == null) {
                    missing.hullModIdsKnown.add(modId)
                    continue
                }
                if (!spec.isAlwaysUnlocked && !spec.isHidden && !spec.isHiddenEverywhere) {
                    hullMods.add(modId)
                }
            }

            compiled.hullMods = hullMods
        }

        if (json.has("shipBlueprints") ||
            json.has("fighterBlueprints") ||
            json.has("weaponBlueprints")
        ) {
            val shipBlueprintsJson = json.optJSONArray("shipBlueprints")?.toStringList() ?:
            json.optString("shipBlueprints", null)?.let { CompressionUtil.base64Inflate(it, dictionary = hullDictionary) }?.split(" ") ?:
            emptyList()
            val shipBlueprintsValid = mutableListOf<String>()
            val fighterBlueprintsJson = json.optJSONArray("fighterBlueprints")?.toStringList() ?:
            json.optString("fighterBlueprints", null)?.let { CompressionUtil.base64Inflate(it, dictionary = wingDictionary) }?.split(" ") ?:
            emptyList()
            val fighterBlueprintsValid = mutableListOf<String>()
            val weaponBlueprintsJson = json.optJSONArray("weaponBlueprints")?.toStringList() ?:
            json.optString("weaponBlueprints", null)?.let { CompressionUtil.base64Inflate(it, dictionary = weaponDictionary) }?.split(" ") ?:
            emptyList()
            val weaponBlueprintsValid = mutableListOf<String>()

            for (id in shipBlueprintsJson) {
                val spec = runCatching { Global.getSettings().getHullSpec(id) }.getOrNull()
                if (spec != null) {
                    shipBlueprintsValid.add(id)
                } else {
                    missing.blueprintHullIds.add(id)
                }
            }

            for (id in fighterBlueprintsJson) {
                val spec = runCatching { Global.getSettings().getFighterWingSpec(id) }.getOrNull()
                if (spec != null) {
                    fighterBlueprintsValid.add(id)
                } else {
                    missing.blueprintWingIds.add(id)
                }
            }

            for (id in weaponBlueprintsJson) {
                val spec = runCatching { Global.getSettings().getWeaponSpec(id) }.getOrNull()
                if (spec != null) {
                    weaponBlueprintsValid.add(id)
                } else {
                    missing.blueprintWeaponIds.add(id)
                }
            }

            compiled.shipBlueprints = shipBlueprintsValid
            compiled.fighterBlueprints = fighterBlueprintsValid
            compiled.weaponBlueprints = weaponBlueprintsValid
        }

        if (json.has("abilities")) {
            compiled.abilities = json.optJSONArrayToStringList("abilities")
        }

        if (json.has("abilityBars")) {
            try {
                fun convertJsonToSavedBars(
                    barsJson: JSONArray,
                    barCount: Int = 5,
                    slotCount: Int = 10
                ): List<List<AbilityPair>> {
                    // Initialize a blank savedBars structure
                    val savedBars = MutableList(barCount) { MutableList(slotCount) { AbilityPair() } }

                    for (i in 0 until barsJson.length()) {
                        val barObject = barsJson.getJSONObject(i)
                        val barIndex = barObject.getInt("barIndex")
                        val slotsArray = barObject.optJSONArray("slots") ?: continue

                        for (j in 0 until slotsArray.length()) {
                            val slotObject = slotsArray.getJSONObject(j)
                            val slotId = slotObject.getInt("slotId")
                            val values = slotObject.getJSONArray("values")

                            val abilityId = values.optString(0, null).takeIf { it.isNotEmpty() }
                            val inHyperAbilityId = values.optString(1, null).takeIf { it.isNotEmpty() }

                            // Reconstruct AbilityPair in the same position
                            savedBars[barIndex][slotId] = AbilityPair(abilityId, inHyperAbilityId)
                        }
                    }

                    return savedBars
                }

                val savedBars = convertJsonToSavedBars(json.optJSONArray("abilityBars") ?: JSONArray())

                compiled.abilityBars = savedBars
            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_ability_bars"), e)
            }
        }

        return compiled
    }

    fun loadCompiledSave(
        compiled: CompiledSave,
        handleCargo: Boolean = true,
        handleSubmarketCargo: Boolean = true,
        handleRelations: Boolean = true,
        handleKnownBlueprints: Boolean = true,
        handlePlayer: Boolean = true,
        handleFleet: Boolean = true,
        handleCredits: Boolean = true,
        handleKnownHullmods: Boolean = true,
        handleOfficers: Boolean = true,
        handleAbilityBar: Boolean = true,
    ) {
        val sector = Global.getSector()
        val playerFleet = sector?.playerFleet ?: return

        val cargo = playerFleet.cargo ?: return

        if (handleRelations && compiled.relations != null) {
            for ((factionId, relation) in compiled.relations) {
                val faction = sector.allFactions.find { it.id == factionId } ?: continue
                faction.relToPlayer.rel = relation
            }
        }

        if (handlePlayer && compiled.player != null) {
            val currentPlayer = sector.playerPerson

            currentPlayer.stats = compiled.player!!.stats
            currentPlayer.name = compiled.player!!.name
            currentPlayer.portraitSprite = compiled.player!!.portraitSprite
        }

        if (handleCargo && compiled.cargo != null) {
            cargo.clear()
            cargo.addAll(compiled.cargo)
        }

        fun findNearbyFreeLocation(
            system: LocationAPI,
            center: Vector2f,
            minDist: Float = 70f,
            step: Float = 120f,
            samplesPerRing: Int = 12,
            maxRadius: Float = 2000f
        ): Vector2f {
            val entities = system.allEntities

            fun isSafe(point: Vector2f): Boolean {
                for (e in entities) {
                    val loc = e.location ?: continue
                    if (e.isExpired) continue
                    if (MathUtils.getDistance(point, loc) < minDist) return false
                }
                return true
            }

            var radius = 0f

            while (radius <= maxRadius) {

                // check multiple angles per ring
                for (i in 0 until samplesPerRing) {
                    val angle = (i.toFloat() / samplesPerRing) * (Math.PI * 2)

                    val x = center.x + (cos(angle) * radius).toFloat()
                    val y = center.y + (sin(angle) * radius).toFloat()

                    val candidate = Vector2f(x, y)

                    if (isSafe(candidate)) {
                        return candidate
                    }
                }

                radius += step
            }

            // fallback (should rarely happen)
            return Vector2f(center.x, center.y)
        }

        try {
            @Suppress("UNCHECKED_CAST")
            val saveTransferStations = sector.persistentData["${FTVariables.PREFIX}_SaveTransferStations"] as? MutableSet<String>
            saveTransferStations?.forEach {
                val entity = Global.getSector().getEntityById(it)
                entity.containingLocation.removeEntity(entity)
            }
            sector.persistentData.remove("${FTVariables.PREFIX}_SaveTransferStations")

            if (handleSubmarketCargo && compiled.marketCargos != null) {
                // Remove all previous save transfer stations.

                for (marketCargo in compiled.marketCargos) {
                    for (submarketCargo in marketCargo.submarkets) {
                        val loc = BaseThemeGenerator.EntityLocation()
                        loc.location = findNearbyFreeLocation(sector.currentLocation, playerFleet.location)
                        val added = addNonSalvageEntity(sector.currentLocation, loc, Entities.CARGO_POD_SPECIAL, Factions.NEUTRAL)
                        val entity = added.entity

                        val name = marketCargo.marketName + " - " + submarketCargo.submarketName
                        val market = MagicCampaign.addSimpleMarket(entity, "save_transfer_${sector.genUID()}", name, 0, Factions.NEUTRAL, false, true, listOf(Conditions.ABANDONED_STATION), emptyList(), true, true, false, false, false, true)
                        entity.name = name
                        MagicCampaign.placeOnStableOrbit(entity, true)

                        @Suppress("UNCHECKED_CAST")
                        val saveTransferStations = sector.persistentData["${FTVariables.PREFIX}_SaveTransferStations"] as? MutableSet<String> ?: mutableSetOf()
                        saveTransferStations.add(entity.id)
                        sector.persistentData["${FTVariables.PREFIX}_SaveTransferStations"] = saveTransferStations
                        entity.addScript(RemoveEmptyStation(entity))

                        val cargo = market.getSubmarket(Submarkets.SUBMARKET_STORAGE).cargo
                        submarketCargo.cargo?.let { cargo.addAll(it) }
                        submarketCargo.fleet?.membersListCopy?.forEach {
                            if (it.captain != null && !it.captain.isDefault && !it.captain.memoryWithoutUpdate.contains(Misc.CAPTAIN_UNREMOVABLE)) {
                                it.captain.memoryWithoutUpdate.set(FTVariables.STORED_OFFICER_TAG, true)
                                it.captain.memoryWithoutUpdate.set(Misc.CAPTAIN_UNREMOVABLE, true)
                            }
                            cargo.mothballedShips.addFleetMember(it)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            DisplayMessage.showError("Error when interacting with save transfer stations", e)
        }

        if (handleCredits && compiled.cargo != null) {
            cargo.credits.set(0f)
            cargo.credits.add(compiled.cargo!!.credits.get())
        }

        if (handleFleet && compiled.fleet != null) {
            FleetUtils.replacePlayerFleetWith(
                compiled.fleet!!, replacePlayer = false, aggression = compiled.aggressionDoctrine,
                settings = FleetSettings().apply {
                    memberSettings.includeOfficer = handleOfficers
                    includeIdleOfficers = handleOfficers
                    includeCommanderSetFlagship = false
                    includeCommanderAsOfficer = false
                    includeAggression = false
                })
        }

        if (handleKnownHullmods && compiled.hullMods != null) {
            sector.characterData.hullMods.toList().forEach {
                val spec = runCatching { Global.getSettings().getHullModSpec(it) }.getOrNull()
                if (spec != null && !spec.isAlwaysUnlocked && !spec.isHidden && !spec.isHiddenEverywhere) {
                    sector.characterData.removeHullMod(spec.id)
                }
            }

            for (modId in compiled.hullMods!!) {
                //sector.playerFaction.addKnownHullMod(modId)
                sector.characterData.addHullMod(modId)
            }
        }

        if (handleKnownBlueprints) {
            sector.playerFaction.knownShips.clear()
            sector.playerFaction.knownFighters.clear()
            sector.playerFaction.knownWeapons.clear()
            for (id in compiled.shipBlueprints) {
                sector.playerFaction.addKnownShip(id, true)
            }
            for (id in compiled.fighterBlueprints) {
                sector.playerFaction.addKnownFighter(id, true)
            }
            for (id in compiled.weaponBlueprints) {
                sector.playerFaction.addKnownWeapon(id, true)
            }
        }

        if (handleAbilityBar) {
            compiled.abilities?.forEach { id ->
                val spec = runCatching { Global.getSettings().getAbilitySpec(id) }.getOrNull() ?: return@forEach

                if (!sector.characterData.abilities.contains(spec.id)
                    && spec.id != "rat_exoship_management") // Will cause a crash if the player controllable exoship is not present
                    sector.characterData.addAbility(spec.id)
            }

            val savedBars = compiled.abilityBars ?: emptyList()

            val bars: Array<Array<CampaignUIPersistentData.AbilitySlot>> = try {
                @Suppress("UNCHECKED_CAST")
                sector.uiData.abilitySlotsAPI.safeInvoke("getSlots") as Array<Array<CampaignUIPersistentData.AbilitySlot>>
            } catch (e: Exception) {
                DisplayMessage.showError(FTTxt.txt("failed_to_load_ability_bars"), e)
                emptyArray<Array<CampaignUIPersistentData.AbilitySlot>>()
            }

            // Vanilla abilities that a modded/renamed ability id can fall back to when its own id is unavailable.
            val fallbacks: List<Pair<String, List<String>>> = listOf(
                Abilities.EMERGENCY_BURN to listOf(Abilities.EMERGENCY_BURN),
                Abilities.SUSTAINED_BURN to listOf(Abilities.SUSTAINED_BURN),
                Abilities.GRAVITIC_SCAN to listOf(Abilities.GRAVITIC_SCAN, "neutrino_detector"),
                Abilities.REMOTE_SURVEY to listOf(Abilities.REMOTE_SURVEY),
                Abilities.SCAVENGE to listOf(Abilities.SCAVENGE),
                Abilities.SENSOR_BURST to listOf(Abilities.SENSOR_BURST),
            )

            fun isUnlocked(id: String): Boolean =
                playerFleet.abilities?.containsKey(id) == true ||
                        sector.characterData.abilities.contains(id) ||
                        sector.playerStats.grantedAbilityIds.contains(id)

            /** Returns an id that is usable by the player right now, or null if the slot should stay empty. */
            fun resolveAbility(savedId: String?): String? {
                if (savedId.isNullOrEmpty()) return null

                // 1. The ability itself, if it exists and the player has it
                val spec = runCatching { Global.getSettings().getAbilitySpec(savedId) }.getOrNull()
                if (spec != null && isUnlocked(spec.id)) return spec.id

                // 2. A vanilla equivalent, matched by id substring.
                return fallbacks
                    .firstOrNull { (_, keys) -> keys.any { savedId.contains(it) } }
                    ?.first
                    ?.takeIf { isUnlocked(it) }
            }

            savedBars.forEachIndexed { barIndex, savedBar ->
                val bar = bars.getOrNull(barIndex) ?: return@forEachIndexed

                savedBar.forEachIndexed { slotIndex, savedSlot ->
                    val slot = bar.getOrNull(slotIndex) ?: return@forEachIndexed

                    slot.abilityId = resolveAbility(savedSlot.abilityId)
                    slot.inHyperAbilityId = resolveAbility(savedSlot.inHyperAbilityId)
                }
            }
        }
    }
}