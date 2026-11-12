package fleetkit.serialization.compressor.formats

import com.fs.starfarer.api.loading.WeaponGroupType
import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.variant.DataVariant

@Suppress("EnumValuesSoftDeprecate")
object WeaponGroupFormat : Format<DataVariant.ParsedWeaponGroup> {
    override val typeId = 'g'
    override val version = 1
    override fun sync(c: Codec, d: DataVariant.ParsedWeaponGroup?) = DataVariant.ParsedWeaponGroup(
        autofire = c.bool(d?.autofire),
        mode = c.enum(d?.mode, WeaponGroupType.values()),
        weapons = c.map(d?.weapons) { c.str(it) }
    )
}