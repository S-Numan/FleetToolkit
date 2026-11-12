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
        shipName = c.s(d?.shipName),
        cr = c.optF(d?.cr),
        hullFraction = c.optF(d?.hullFraction),
        isMothballed = c.b(d?.isMothballed),
        isFlagship = c.b(d?.isFlagship),
        id = c.optS(d?.id)
    )
}