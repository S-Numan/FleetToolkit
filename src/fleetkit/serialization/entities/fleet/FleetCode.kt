package fleetkit.serialization.entities.fleet

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetDataAPI
import fleetkit.core.util.FTTxt
import fleetkit.serialization.content.ModVersionData
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.EntityFormat
import fleetkit.serialization.core.ModHeader
import fleetkit.serialization.entities.fleet.sic.SecondInCommandCode
import fleetkit.serialization.entities.member.MemberCode
import fleetkit.serialization.entities.person.PersonCode
import fleetkit.util.api.FleetUtils

// Composite: embeds PersonFormat and MemberFormat. Bump this version whenever either changes.
object FleetCode : EntityFormat<DataFleet.ParsedFleetData> {
    override val typeId = 'f'
    override val version = 1
    override fun sync(c: Codec, d: DataFleet.ParsedFleetData?) = DataFleet.ParsedFleetData(
        fleetName = c.optStr(d?.fleetName),
        aggression = c.int(d?.aggression),
        factionID = c.optStr(d?.factionID),
        commanderIfNoFlagship = c.opt(d?.commanderIfNoFlagship, PersonCode),
        members = c.list(d?.members, MemberCode),
        idleOfficers = c.list(d?.idleOfficers, PersonCode),
        memKeys = c.map(d?.memKeys) { c.any(it) },
        secondInCommandData = c.opt(d?.secondInCommandData, SecondInCommandCode)
    )
    /** If [mods] is null, it will automatically be generated */
    override fun encode(data: DataFleet.ParsedFleetData, withPrepend: Boolean, mods: ModHeader?): String {
        val mods = mods ?: ModHeader(FleetUtils.getAllSourceModsFromFleet(data).map { ModVersionData(it.id, it.name, it.version) })
        val output = super.encode(data, withPrepend = false, mods)
        if (!withPrepend) {
            return output
        } else {
            val resolvedFleetData = DataFleet.createCampaignFleetFromData(data).fleetData

            val dp = resolvedFleetData.membersListCopy.sumOf { it.deploymentPointsCost.toDouble() }

            val fleetName = data.fleetName ?: "Fleet"
            val readable = FTTxt.txt("fleet_summary", fleetName, dp.toInt(), data.members.size)

            return "$readable ${mods.describe()}\n$output"
        }
    }
    fun encode(data: FleetDataAPI, withPrepend: Boolean = true) =
         encode(DataFleet.getFleetDataFromFleet(data), withPrepend)
    fun encode(data: CampaignFleetAPI, withPrepend: Boolean = true) =
        encode(data.fleetData, withPrepend)
}
