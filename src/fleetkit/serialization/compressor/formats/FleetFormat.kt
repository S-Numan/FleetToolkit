package fleetkit.serialization.compressor.formats

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetDataAPI
import fleetkit.serialization.GameModInfo
import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.compressor.ModHeader
import fleetkit.serialization.compressor.formats.sic.SecondInCommandFormat
import fleetkit.serialization.fleet.DataFleet
import fleetkit.util.api.FleetUtils

// Composite: embeds PersonFormat and MemberFormat. Bump this version whenever either changes.
@Suppress("EnumValuesSoftDeprecate")
object FleetFormat : Format<DataFleet.ParsedFleetData> {
    override val typeId = 'f'
    override val version = 1
    override fun sync(c: Codec, d: DataFleet.ParsedFleetData?) = DataFleet.ParsedFleetData(
        fleetName = c.optStr(d?.fleetName),
        fleetAggression = c.opt(d?.fleetAggression) { c.enum(it, DataFleet.FleetAggressionV1.values()) },
        factionID = c.optStr(d?.factionID),
        commanderIfNoFlagship = c.opt(d?.commanderIfNoFlagship, PersonFormat),
        members = c.list(d?.members, MemberFormat),
        idleOfficers = c.list(d?.idleOfficers, PersonFormat),
        memKeys = c.map(d?.memKeys) { c.any(it) },
        secondInCommandData = c.opt(d?.secondInCommandData, SecondInCommandFormat)
    )
    fun encode(data: FleetDataAPI, withPrepend: Boolean = true): String {
        val data = DataFleet.getFleetDataFromFleet(data)
        val header = ModHeader(FleetUtils.getAllSourceModsFromFleet(data).map { GameModInfo(it.id, it.name, it.version) })
        return encode(data, header, withPrepend)
    }
    fun encode(data: CampaignFleetAPI, withPrepend: Boolean = true) =
        encode(data.fleetData, withPrepend)
}
