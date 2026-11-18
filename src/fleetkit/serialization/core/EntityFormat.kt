package fleetkit.serialization.core

/** A top-level format whose codes carry an optional [ModHeader] (game version + required mods). */
interface EntityFormat<T : ConvertibleToEntity<*>> : Format<T> {

    // The plain versions must use the same layout, so they go through the mod header too.
    override fun encode(data: T): String = encode(data, true, null)
    override fun decode(code: String): T? = decodeWithMods(code)?.data

    /**
     * @param withPrepend Add a description line for the user, a line break, then the code. Safe to paste back into decode().
     * @param mods to embed the game version and required mods.
     */
    fun encode(data: T, withPrepend: Boolean = true, mods: ModHeader? = null): String {
        val output = encodeWith(data) { it.optModHeader(mods) }
        return if (output.isNotEmpty() && withPrepend && mods != null)
            data.javaClass.simpleName + " " + mods.describe() + "\n" + output
        else
            output
    }

    fun decodeWithMods(code: String): Decoded<T>? =
        decodeWith(code) { it.optModHeader(null) }?.let { (data, mods) -> Decoded(data, mods) }

    /** Reads only the mod header (null if the code has none). Use it to warn about missing mods before importing. */
    fun readMods(code: String): ModHeader? =
        readHeaderWith(code) { it.optModHeader(null) }
}
 