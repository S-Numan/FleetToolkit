package fleetkit.serialization.compressor.formats

import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import fleetkit.serialization.GameModInfo
import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.compressor.ModHeader
import fleetkit.serialization.fleet.DataFleet
import fleetkit.serialization.member.DataMember
import fleetkit.util.api.FleetUtils
import fleetkit.util.api.MemberUtils

// Composite: embeds VariantFormat and PersonFormat. Bump this version whenever either changes.
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
    fun encode(member: FleetMemberAPI, withPrepend: Boolean = true): String {
        val data = DataMember.getMemberDataFromMember(member)
        val header = ModHeader(MemberUtils.getAllSourceModsFromMember(data).map { GameModInfo(it.id, it.name, it.version) })
        return encode(data, header, withPrepend)
    }
}
