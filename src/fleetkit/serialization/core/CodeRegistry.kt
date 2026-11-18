package fleetkit.serialization.core

import fleetkit.serialization.entities.fleet.FleetCode
import fleetkit.serialization.entities.fleet.sic.SecondInCommandCode
import fleetkit.serialization.entities.fleet.sic.SecondInCommandOfficerCode
import fleetkit.serialization.entities.member.MemberCode
import fleetkit.serialization.entities.person.PersonCode
import fleetkit.serialization.entities.variant.VariantCode
import fleetkit.serialization.entities.variant.WeaponGroupCode

object CodeRegistry {
    /** Every code starts with this, so ordinary text that happens to begin with a type letter isn't as easily mistaken for one. */
    const val MARKER = '$'
    const val HEADER_LENGTH = 2 // MARKER + typeId

    fun header(typeId: Char): String = "$MARKER$typeId"

    private val all: List<EntityFormat<*>> = listOf(VariantCode, MemberCode, PersonCode, FleetCode)

    init {
        // Catch duplicate type ids early
        check(all.map { it.typeId }.toSet().size == all.size) { "Duplicate Format typeId" }
    }

    /** Returns the last non-blank line, trimmed, intended to keep only the trailing Base64 code. */
    private val WS = Regex("\\s+")
    fun clean(code: String): String =
        code.lines().lastOrNull { it.isNotBlank() }
            ?.trim()?.split(WS)?.last()
            ?.trim('`', '"', '\'', '<', '>') ?: ""

    /** Returns the matching format, or null unless the text starts with MARKER followed by a known type id. */
    fun identify(code: String): EntityFormat<*>? {
        val c = clean(code)
        if (c.length <= HEADER_LENGTH || c[0] != MARKER) return null
        return all.firstOrNull { it.typeId == c[1] }
    }

    /** Mod header of any recognised code without decoding its body; null if unrecognised or it has none. */
    fun parseModHeader(code: String): ModHeader? =
        identify(code)?.readMods(code)

    fun parseData(code: String): ConvertibleToEntity<*>? =
        identify(code)?.decode(code)

    /**
     * Example usage:
     *
     * when (CodeRegistry.identify(pasted)) {
     *     FleetCode   -> importFleet(FleetCode.decode(pasted))
     *     MemberCode  -> importMember(MemberCode.decode(pasted))
     *     VariantCode -> importVariant(VariantCode.decode(pasted))
     *     PersonCode  -> importPerson(PersonCode.decode(pasted))
     *     else          -> showError("Unrecognized code")
     * }
     */
}
