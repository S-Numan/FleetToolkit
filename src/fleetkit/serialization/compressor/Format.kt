package fleetkit.serialization.compressor

/**
 * RULE: if you change the layout of a format, bump its `version` AND the `version` of every format that
 * embeds it, directly or indirectly. Otherwise old codes pass the version check and decode into garbage.
 */
interface Format<T : Any> {
    val typeId: Char
    val version: Int
    fun sync(c: Codec, d: T?): T

    fun encode(data: T): String {
        val out = ByteBuffers.Out()
        val c = Codec(out)
        c.int(version)
        sync(c, data)
        return typeId + BinaryPack.pack(out.toByteArray())
    }

    fun decode(code: String): T {
        val clean = Formats.clean(code)
        check(clean.firstOrNull() == typeId) { "Wrong data type: expected '$typeId'" }
        val input = ByteBuffers.In(BinaryPack.unpack(clean.substring(1)))
        val c = Codec(input)
        val ver = c.int(null)
        check(ver == version) { "Unsupported version $ver" }
        val result = sync(c, null)
        // Raw deflate has no checksum; leftover bytes mean a mistyped or mismatched code
        check(input.remaining() == 0) { "Corrupt data: trailing bytes" }
        return result
    }

    fun decodeOrNull(code: String): T? = try { decode(code) } catch (_: RuntimeException) { null }
}