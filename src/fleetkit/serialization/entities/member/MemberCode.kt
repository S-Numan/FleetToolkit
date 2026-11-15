package fleetkit.serialization.entities.member

import com.fs.starfarer.api.fleet.FleetMemberAPI
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.Format
import fleetkit.serialization.core.ModHeader
import fleetkit.serialization.entities.person.PersonCode
import fleetkit.serialization.entities.variant.VariantCode
import fleetkit.serialization.content.GameModInfo
import fleetkit.serialization.core.EntityFormat
import fleetkit.util.api.MemberUtils

// Composite: embeds VariantFormat and PersonFormat. Bump this version whenever either changes.
object MemberCode : EntityFormat<DataMember.ParsedMemberData> {
    override val typeId = 'm'
    override val version = 1
    override fun sync(c: Codec, d: DataMember.ParsedMemberData?) = DataMember.ParsedMemberData(
        variantData = c.opt(d?.variantData, VariantCode),
        personData = c.opt(d?.personData, PersonCode),
        shipName = c.str(d?.shipName),
        cr = c.optFloat2(d?.cr),
        hullFraction = c.optFloat2(d?.hullFraction),
        isMothballed = c.bool(d?.isMothballed),
        isFlagship = c.bool(d?.isFlagship),
        id = c.optStr(d?.id)
    )
    /** If [mods] is null, it will automatically be generated */
    override fun encode(data: DataMember.ParsedMemberData, withPrepend: Boolean, mods: ModHeader?): String {
        return super.encode(data, withPrepend, mods ?: ModHeader(MemberUtils.getAllSourceModsFromMember(data).map { GameModInfo(it.id, it.name, it.version) }))
    }
    fun encode(member: FleetMemberAPI, withPrepend: Boolean = true) =
        encode(DataMember.getMemberDataFromMember(member), withPrepend)
}
