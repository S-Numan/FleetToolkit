package fleetkit.serialization.compressor.formats

import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.fleet.DataFleet

@Suppress("EnumValuesSoftDeprecate")
object FleetFormat : Format<DataFleet.ParsedFleetData> {
    override val typeId = 'f'
    override val version = 1
    override fun sync(c: Codec, d: DataFleet.ParsedFleetData?) = DataFleet.ParsedFleetData(
        fleetName = c.optS(d?.fleetName),
        aggression = c.opt(d?.aggression) { c.enum(it, DataFleet.FleetAggressionV1.values()) },
        factionID = c.optS(d?.factionID),
        commanderIfNoFlagship = c.opt(d?.commanderIfNoFlagship, PersonFormat),
        members = c.list(d?.members, MemberFormat),
        idleOfficers = c.list(d?.idleOfficers, PersonFormat),
        memKeys = c.map(d?.memKeys) { c.any(it) }
    )
}