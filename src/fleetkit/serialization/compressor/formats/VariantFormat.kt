package fleetkit.serialization.compressor.formats

import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import fleetkit.serialization.GameModInfo
import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.compressor.ModHeader
import fleetkit.serialization.member.DataMember
import fleetkit.serialization.variant.DataVariant
import fleetkit.util.api.MemberUtils
import fleetkit.util.api.VariantUtils

// Composite: embeds WeaponGroupFormat. Bump this version whenever WeaponGroupFormat's layout changes.
object VariantFormat : Format<DataVariant.ParsedVariantData> {
    override val typeId = 'v'
    override val version = 1
    override fun sync(c: Codec, d: DataVariant.ParsedVariantData?): DataVariant.ParsedVariantData =
        DataVariant.ParsedVariantData(
            hullId = c.str(d?.hullId),
            variantId = c.str(d?.variantId),
            displayName = c.str(d?.displayName),
            fluxCapacitors = c.int(d?.fluxCapacitors),
            fluxVents = c.int(d?.fluxVents),
            tags = c.strList(d?.tags),
            hullMods = c.strSet(d?.hullMods),
            permaMods = c.strSet(d?.permaMods),
            sMods = c.strSet(d?.sMods),
            sModdedBuiltIns = c.strSet(d?.sModdedBuiltIns),
            suppressedMods = c.strSet(d?.suppressedMods),
            wings = c.strList(d?.wings),
            weaponGroups = c.list(d?.weaponGroups, WeaponGroupFormat),
            moduleVariants = c.map(d?.moduleVariants) { c.nested { sync(c, it) } }
        )

    fun encode(variant: ShipVariantAPI, withPrepend: Boolean = true): String {
        val data = DataVariant.getVariantDataFromVariant(variant)
        val header = ModHeader(VariantUtils.getAllSourceModsFromVariant(data).map { GameModInfo(it.id, it.name, it.version) })
        return encode(data, header, withPrepend)
    }
}