package fleetkit.serialization.entities.member

import com.fs.starfarer.api.fleet.FleetMemberAPI
import fleetkit.core.util.FTMisc
import fleetkit.serialization.content.MissingContent
import fleetkit.serialization.entities.member.DataMember.buildMemberFull
import fleetkit.serialization.entities.person.PersonJson.extractPersonDataFromJson
import fleetkit.serialization.entities.person.PersonJson.savePersonToJson
import fleetkit.serialization.entities.variant.VariantJson.addVariantSourceModsToJson
import fleetkit.serialization.entities.variant.VariantJson.extractVariantDataFromJson
import fleetkit.serialization.entities.variant.VariantJson.saveVariantToJson
import fleetkit.util.api.kotlin.roundToDecimals
import org.json.JSONObject
import org.lazywizard.lazylib.ext.json.optFloat
import java.util.*

object MemberJson {
    @JvmOverloads
    fun extractMemberDataFromJson(
        json: JSONObject,
        missing: MissingContent = MissingContent()
    ): DataMember.ParsedMemberData {
        val variantJson = json.optJSONObject("variant")

        val variantData = if (variantJson != null)
            extractVariantDataFromJson(variantJson, missing)
        else
            null

        val officerJson = json.optJSONObject("officer")
        val personData = if (officerJson != null)
            extractPersonDataFromJson(officerJson, missing)
        else
            null

        val isFlagship = json.optBoolean("isFlagship", false)

        missing.gameMods.addAll(FTMisc.getModInfosFromJson(json))

        return DataMember.ParsedMemberData(
            variantData = variantData,
            personData = personData,
            shipName = json.optString("name", ""),
            cr = if (json.has("cr")) json.optFloat("cr") else null,
            hullFraction = if (json.has("hullFraction")) json.optFloat("hullFraction") else null,
            isMothballed = json.optBoolean("ismothballed", false),
            isFlagship = isFlagship,
            id = if (json.has("id")) json.optString("id") else null
        )
    }

    fun setMemberValuesFromJson(json: JSONObject, member: FleetMemberAPI) {
        val cr = json.optFloat("cr", 0.7f)
        val hullFraction = json.optFloat("hullFraction", 1.0f)
        val shipName = json.optString("name", "")
        member.repairTracker.cr = cr.coerceIn(0f, 1f) // Ensure CR is within [0, 1]
        member.status.hullFraction = hullFraction.coerceIn(0f, 1f)
        if (shipName.isNotEmpty()) {
            member.shipName = shipName
        }
        if (json.optBoolean("ismothballed"))
            member.repairTracker.isMothballed = true
    }

    @JvmOverloads
    fun getMemberFromJson(
        json: JSONObject,
        settings: MemberSettings = MemberSettings(),
        missing: MissingContent = MissingContent(),
        random: Random = Random()
    ): FleetMemberAPI {
        val parsed = extractMemberDataFromJson(json, missing)

        return buildMemberFull(parsed, settings, missing, random)
    }

    @JvmOverloads
    fun saveMemberToJson(
        member: FleetMemberAPI,
        settings: MemberSettings = MemberSettings(),
        includeModInfo: Boolean = true,
    ): JSONObject {
        return saveMemberToJson(
            DataMember.getMemberDataFromMember(member, settings),
            includeModInfo
        )
    }

    @JvmOverloads
    fun saveMemberToJson(
        data: DataMember.ParsedMemberData,
        includeModInfo: Boolean = true,
    ): JSONObject {
        val memberJson = JSONObject()

        if (data.cr != null)
            memberJson.put("cr", data.cr.roundToDecimals(2))

        if (data.hullFraction != null && data.hullFraction != 1f)
            memberJson.put("hullFraction", data.hullFraction.roundToDecimals(2))

        memberJson.put("name", data.shipName)

        if (data.isMothballed)
            memberJson.put("ismothballed", true)

        if (data.personData != null) {
            val officerJson = savePersonToJson(data.personData)
            memberJson.put("officer", officerJson)
        }

        if (data.variantData != null) {
            memberJson.put("variant", saveVariantToJson(data.variantData, includeModInfo = false))

            if (includeModInfo)
                addVariantSourceModsToJson(data.variantData, memberJson)
        }

        if (!data.id.isNullOrEmpty())
            memberJson.put("id", data.id)

        return memberJson
    }
}