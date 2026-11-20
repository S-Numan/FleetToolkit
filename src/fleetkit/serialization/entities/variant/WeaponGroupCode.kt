package fleetkit.serialization.entities.variant

import com.fs.starfarer.api.loading.WeaponGroupType
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.Format

@Suppress("EnumValuesSoftDeprecate")
object WeaponGroupCode : Format<DataVariant.ParsedWeaponGroup> {
    private val SLOT = Regex("WS (\\d{3})")

    /** "WS 007" is stored as the int 7. Anything else is stored as 0 followed by the literal string. */
    fun Codec.slotId(v: String?): String {
        val n = int(v?.let { SLOT.matchEntire(it)?.groupValues?.get(1)?.toInt() } ?: 0)
        return if (n > 0) "WS %03d".format(n) else str(v)
    }

    override val typeId = 'g'
    override val version = 1
    override fun sync(c: Codec, d: DataVariant.ParsedWeaponGroup?) = DataVariant.ParsedWeaponGroup(
        autofire = c.bool(d?.autofire),
        mode = c.enum(d?.mode, WeaponGroupType.values()),
        weapons = c.map(d?.weapons, { c.slotId(it) }, { c.str(it) })
    )
}