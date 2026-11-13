package fleetkit.serialization.compressor

/**
 * RULE: if you change the layout of a format, bump its `version` AND the `version` of every format that
 * embeds it, directly or indirectly. Otherwise old codes pass the version check and decode into garbage.
 */
interface Format<T : Any> {
    val typeId: Char
    val version: Int
    fun sync(c: Codec, d: T?): T

    /**
     * Pass [mods] to embed the game version and required mods.
     *
     * @param withPrepend Add a description line for the user, a line break, then the code. Safe to paste back into decode().
     */
    fun encode(data: T, mods: ModHeader? = null, withPrepend: Boolean = true): String {
        val out = ByteBuffers.Out()
        val c = Codec(out)
        c.int(version)
        c.optModHeader(mods)
        sync(c, data)
        val output = "${Formats.header(typeId)}${BinaryPack.pack(out.toByteArray())}"
        return if (withPrepend && mods != null)
            mods.describe() + "\n" + output
        else
            output
    }

    fun decode(code: String): T = decodeWithMods(code).data

    fun decodeWithMods(code: String): Decoded<T> {
        val (input, c) = openCodec(code)
        val mods = c.optModHeader(null)
        val result = sync(c, null)
        // Raw deflate has no checksum; leftover bytes mean a mistyped or mismatched code
        check(input.remaining() == 0) { "Corrupt data: trailing bytes" }
        return Decoded(result, mods)
    }

    /** Reads only the mod header (null if the code has none). Use it to warn about missing mods before importing. */
    fun readMods(code: String): ModHeader? = openCodec(code).second.optModHeader(null)

    private fun openCodec(code: String): Pair<ByteBuffers.In, Codec> {
        val clean = Formats.clean(code)
        check(clean.startsWith(Formats.header(typeId))) { "Wrong data type: expected '$typeId'" }
        val input = ByteBuffers.In(BinaryPack.unpack(clean.substring(Formats.HEADER_LENGTH)))
        val c = Codec(input)
        val ver = c.int(null)
        check(ver == version) { "Unsupported version $ver" }
        return input to c
    }
}