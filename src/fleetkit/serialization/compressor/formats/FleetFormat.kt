package fleetkit.serialization.compressor.formats

import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.fleet.DataFleet

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
        memKeys = c.map(d?.memKeys) { c.any(it) }
    )
}
