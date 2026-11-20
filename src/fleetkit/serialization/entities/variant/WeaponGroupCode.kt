package fleetkit.serialization.entities.variant

import com.fs.starfarer.api.loading.WeaponGroupType
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.Format

@Suppress("EnumValuesSoftDeprecate")
object WeaponGroupCode : Format<DataVariant.ParsedWeaponGroup> {
    // "WS" + optional single space + exactly 3 or 4 digits. Space and width are kept so strings round-trip exactly.
    private val SLOT = Regex("WS( ?)(\\d{3,4})")

    /**
     * Stores a slot ID as one varint: 1 + number * 4 + (2 if four digits) + (1 if there was a space).
     * 0 means "literal string follows" and is used for anything that doesn't match exactly
     *
     * Examples: "WS 007", "WS007", "WS0002", "WS 0002".
     */
    fun Codec.slotId(v: String?): String {
        val m = v?.let { SLOT.matchEntire(it) }
        val code = int(
            if (m == null) 0
            else {
                val digits = m.groupValues[2]
                1 + digits.toInt() * 4 +
                        (if (digits.length == 4) 2 else 0) +
                        (if (m.groupValues[1].isNotEmpty()) 1 else 0)
            }
        )
        if (code <= 0) return str(v)
        val c = code - 1
        val width = if (c and 2 != 0) 4 else 3
        return "WS" + (if (c and 1 != 0) " " else "") + (c shr 2).toString().padStart(width, '0')
    }

    override val typeId = 'g'
    override val version = 1
    override fun sync(c: Codec, d: DataVariant.ParsedWeaponGroup?) = DataVariant.ParsedWeaponGroup(
        autofire = c.bool(d?.autofire),
        mode = c.enum(d?.mode, WeaponGroupType.values()),
        weapons = c.map(d?.weapons, { c.slotId(it) }, { c.str(it) })
    )
}