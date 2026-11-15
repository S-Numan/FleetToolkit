package fleetkit.serialization.entities.fleet

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetDataAPI
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.Format
import fleetkit.serialization.core.ModHeader
import fleetkit.serialization.entities.person.PersonCode
import fleetkit.serialization.content.GameModInfo
import fleetkit.serialization.core.EntityFormat
import fleetkit.serialization.entities.fleet.sic.SecondInCommandCode
import fleetkit.serialization.entities.member.MemberCode
import fleetkit.util.api.FleetUtils

// Composite: embeds PersonFormat and MemberFormat. Bump this version whenever either changes.
@Suppress("EnumValuesSoftDeprecate")
object FleetCode : EntityFormat<DataFleet.ParsedFleetData> {
    override val typeId = 'f'
    override val version = 1
    override fun sync(c: Codec, d: DataFleet.ParsedFleetData?) = DataFleet.ParsedFleetData(
        fleetName = c.optStr(d?.fleetName),
        fleetAggression = c.opt(d?.fleetAggression) { c.enum(it, DataFleet.FleetAggressionV1.values()) },
        factionID = c.optStr(d?.factionID),
        commanderIfNoFlagship = c.opt(d?.commanderIfNoFlagship, PersonCode),
        members = c.list(d?.members, MemberCode),
        idleOfficers = c.list(d?.idleOfficers, PersonCode),
        memKeys = c.map(d?.memKeys) { c.any(it) },
        secondInCommandData = c.opt(d?.secondInCommandData, SecondInCommandCode)
    )
    /** If [mods] is null, it will automatically be generated */
    override fun encode(data: DataFleet.ParsedFleetData, withPrepend: Boolean, mods: ModHeader?): String {
        return super.encode(data, withPrepend = withPrepend, mods ?: ModHeader(FleetUtils.getAllSourceModsFromFleet(data).map { GameModInfo(it.id, it.name, it.version) }))
    }
    fun encode(data: FleetDataAPI, withPrepend: Boolean = true) =
         encode(DataFleet.getFleetDataFromFleet(data), withPrepend)
    fun encode(data: CampaignFleetAPI, withPrepend: Boolean = true) =
        encode(data.fleetData, withPrepend)
}
