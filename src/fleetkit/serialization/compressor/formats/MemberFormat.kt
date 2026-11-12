package fleetkit.serialization.compressor.formats

import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.member.DataMember

object MemberFormat : Format<DataMember.ParsedMemberData> {
    override val typeId = 'm'
    override val version = 1
    override fun sync(c: Codec, d: DataMember.ParsedMemberData?) = DataMember.ParsedMemberData(
        variantData = c.opt(d?.variantData, VariantFormat),
        personData = c.opt(d?.personData, PersonFormat),
        shipName = c.str(d?.shipName),
        cr = c.optFloat2(d?.cr),
        hullFraction = c.optFloat2(d?.hullFraction),
        isMothballed = c.bool(d?.isMothballed),
        isFlagship = c.bool(d?.isFlagship),
        id = c.optStr(d?.id)
    )
}