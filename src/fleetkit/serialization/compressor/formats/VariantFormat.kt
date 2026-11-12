package fleetkit.serialization.compressor.formats

import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.variant.DataVariant

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
}