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

    /** Returns the last non-blank line, trimmed, keeping only the trailing sequence of characters after the last space. */
    fun clean(code: String): String =
        code.lines()
            .lastOrNull { it.isNotBlank() }
            ?.trim()
            ?.substringAfterLast(' ')
            ?: ""

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
     * when (Formats.identify(pasted)) {
     *     FleetFormat   -> importFleet(FleetFormat.decode(pasted))
     *     MemberFormat  -> importMember(MemberFormat.decode(pasted))
     *     VariantFormat -> importVariant(VariantFormat.decode(pasted))
     *     PersonFormat  -> importPerson(PersonFormat.decode(pasted))
     *     else          -> showError("Unrecognized code")
     * }
     */
}
