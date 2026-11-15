package fleetkit.serialization.io

import com.fs.starfarer.api.Global
import fleetkit.core.util.DisplayMessage
import fleetkit.core.util.FTTxt
import fleetkit.serialization.content.MissingContent
import fleetkit.serialization.core.CodeRegistry
import fleetkit.serialization.core.ConvertibleToEntity
import fleetkit.serialization.entities.fleet.FleetJson.extractFleetDataFromJson
import fleetkit.serialization.entities.fleet.LegacyFleetCode
import fleetkit.serialization.entities.member.LegacyMemberCode
import fleetkit.serialization.entities.member.MemberJson.extractMemberDataFromJson
import fleetkit.serialization.entities.person.LegacyPersonCode
import fleetkit.serialization.entities.person.PersonJson.extractPersonDataFromJson
import fleetkit.serialization.entities.variant.LegacyVariantCode
import fleetkit.serialization.entities.variant.VariantJson.extractVariantDataFromJson
import org.json.JSONObject
import java.awt.Color

object CodeImporter {
    /*@JvmOverloads
    fun extractDataFromAny(value: Any, missing: MissingContent = MissingContent()): ConvertibleToEntity<*, *>? {
        if (value is String)
            return extractDataFromString(value, missing)
        else if (value is JSONObject)
            return extractDataFromJSON(value, missing)

        return null
    }*/

    @JvmOverloads
    fun extractDataFromString(text: String, missing: MissingContent = MissingContent()): ConvertibleToEntity<*>? {
        val text = text.trim()
        if (text.isBlank()) return null
        val json = JSONInput.getJSONFromStringSafe(text)
        if (json != null) {
            return extractDataFromJSON(json, missing)
        }

        return when {
            @Suppress("DEPRECATION")
            LegacyVariantCode.isCompressedVariant(text) -> {
                @Suppress("DEPRECATION")
                val comp = LegacyVariantCode.extractVariantDataFromCompString(text, missing)
                if (comp == null)
                    DisplayMessage.showMessage(FTTxt.txt("failed_extract_variant"), Color.YELLOW)
                comp
            }
            @Suppress("DEPRECATION")
            LegacyPersonCode.isCompressedPerson(text) -> {
                @Suppress("DEPRECATION")
                val comp = LegacyPersonCode.extractPersonDataFromCompString(text, missing)
                if (comp == null)
                    DisplayMessage.showMessage(FTTxt.txt("failed_extract_person"), Color.YELLOW)
                comp
            }
            @Suppress("DEPRECATION")
            LegacyMemberCode.isCompressedMember(text) -> {
                @Suppress("DEPRECATION")
                val comp = LegacyMemberCode.extractMemberDataFromCompString(text, missing)
                if (comp == null)
                    DisplayMessage.showMessage(FTTxt.txt("failed_extract_member"), Color.YELLOW)
                comp
            }
            @Suppress("DEPRECATION")
            LegacyFleetCode.isCompressedFleet(text) -> {
                @Suppress("DEPRECATION")
                val comp = LegacyFleetCode.extractFleetDataFromCompString(text, missing)
                if (comp == null)
                    DisplayMessage.showMessage(FTTxt.txt("failed_extract_fleet"), Color.YELLOW)
                comp
            }
            else -> {
                val format = CodeRegistry.identify(text)
                if (format != null) {
                    try {
                        val decoded = format.decodeWithMods(text)!!
                        decoded.mods?.toGameModInfos()?.let { missing.gameMods.addAll(it) }
                        decoded.data
                    } catch (e: Exception) {
                        DisplayMessage.showMessage(FTTxt.txt("failed_extract", format.javaClass.simpleName), Color.YELLOW)
                        Global.getLogger(this.javaClass).error(e)
                        null
                    }
                } else
                    null
            }
        }
    }

    @JvmOverloads
    fun extractDataFromJSON(json: JSONObject, missing: MissingContent = MissingContent()): ConvertibleToEntity<*>? {
        if (json.length() == 0) return null

        return when {
            json.has("skills") || json.has("first") -> {
                // Officer
                extractPersonDataFromJson(json, missing)
            }

            json.has("variant") || json.has("officer") -> {
                // Fleet member
                extractMemberDataFromJson(json, missing)
            }

            json.has("hullId") -> {
                // Variant
                extractVariantDataFromJson(json, missing)
            }

            json.has("members") -> {
                // Fleet
                extractFleetDataFromJson(json, missing)
            }

            else -> null
        }
    }

    // legacy separator constants, only kept here because I find it to too time-consuming to move them.
    internal const val sep = ","
    internal const val fieldSep = "%"//Only two ascii characters that cannot be in a variant display name
    internal const val metaSep = "$"//Only two ascii characters that cannot be in a variant display name
    internal const val joinSep = ">"
    internal const val memberSep = "$$><$$"
    internal const val personSep = "$$<>$$"
    internal const val fleetSep1 = "$$>>$$"
    internal const val fleetSep2 = "$$<<$$"
    internal const val memKeySep = "$,$"
    internal const val memKeyJoinSep = "$>$"
}