package fleetkit.serialization.compressor

import fleetkit.serialization.compressor.formats.FleetFormat
import fleetkit.serialization.compressor.formats.MemberFormat
import fleetkit.serialization.compressor.formats.PersonFormat
import fleetkit.serialization.compressor.formats.VariantFormat

object Formats {
    private val all: List<Format<*>> = listOf(VariantFormat, MemberFormat, PersonFormat, FleetFormat)

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

    fun identify(code: String): Format<*>? {
        val t = clean(code).firstOrNull() ?: return null
        return all.firstOrNull { it.typeId == t }
    }

    fun identifyData(code: String): Any? =
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