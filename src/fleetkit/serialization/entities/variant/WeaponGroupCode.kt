package fleetkit.serialization.entities.variant

import com.fs.starfarer.api.loading.WeaponGroupType
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.Format

@Suppress("EnumValuesSoftDeprecate")
object WeaponGroupCode : Format<DataVariant.ParsedWeaponGroup> {
    override val typeId = 'g'
    override val version = 1
    override fun sync(c: Codec, d: DataVariant.ParsedWeaponGroup?) = DataVariant.ParsedWeaponGroup(
        autofire = c.bool(d?.autofire),
        mode = c.enum(d?.mode, WeaponGroupType.values()),
        weapons = c.map(d?.weapons) { c.str(it) }
    )
}