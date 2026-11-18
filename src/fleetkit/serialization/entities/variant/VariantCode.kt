package fleetkit.serialization.entities.variant

import com.fs.starfarer.api.combat.ShipVariantAPI
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.ModHeader
import fleetkit.serialization.content.ParsedModData
import fleetkit.serialization.core.EntityFormat
import fleetkit.util.api.VariantUtils
import org.magiclib.util.MagicLookup

// Composite: embeds WeaponGroupFormat. Bump this version whenever WeaponGroupFormat's layout changes.
object VariantCode : EntityFormat<DataVariant.ParsedVariantData> {
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
            weaponGroups = c.list(d?.weaponGroups, WeaponGroupCode),
            moduleVariants = c.map(d?.moduleVariants) { c.nested { sync(c, it) } }
        )

    /** If [mods] is null, it will automatically be generated */
    override fun encode(data: DataVariant.ParsedVariantData, withPrepend: Boolean, mods: ModHeader?): String {
        val mods = mods ?: ModHeader(VariantUtils.getAllSourceModsFromVariant(data).map { ParsedModData(it.id, it.name, it.version) })
        val output = super.encode(data, withPrepend = false, mods)
        if (!withPrepend) {
            return output
        } else {
            val readable = "${data.displayName} ${MagicLookup.getHullSpec(data.hullId)?.hullName}"
            return "$readable ${mods.describe()}\n$output"
        }
    }

    fun encode(variant: ShipVariantAPI, withPrepend: Boolean = true) =
        encode(DataVariant.getVariantDataFromVariant(variant), withPrepend)
}