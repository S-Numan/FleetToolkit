package fleetkit.serialization.core

import com.fs.starfarer.api.Global

/**
 * RULE: if you change the layout of a format, bump its `version` AND the `version` of every format that
 * embeds it, directly or indirectly. Otherwise old codes pass the version check and decode into garbage.
 */
interface Format<T : Any> {
    val typeId: Char
    val version: Int
    fun sync(c: Codec, d: T?): T

    fun encode(data: T): String = encodeWith(data) { }

    fun decode(code: String): T? = decodeWith(code) { }?.first

    /**
     * Not intended for use outside sub-interfaces.
     *
     * Building block for [encode]. [header] writes extra data between the version and the body (nothing in a plain Format).
     * Subinterfaces use it to add their own header; don't call it from outside.
     */
    fun encodeWith(data: T, header: (Codec) -> Unit): String {
        try {
            val out = ByteBuffers.Out()
            val c = Codec(out)
            c.int(version)
            header(c)
            sync(c, data)
            return "${CodeRegistry.header(typeId)}${BinaryPack.pack(out.toByteArray())}"
        } catch (e: Exception) {
            Global.getLogger(this.javaClass).error("Failed to encode", e)
        }
        return ""
    }

    /**
     * Not intended for use outside sub-interfaces.
     *
     * Building block for [decode]. [header] reads whatever [encodeWith]'s header wrote and returns it.
     */
    fun <H> decodeWith(code: String, header: (Codec) -> H): Pair<T, H>? {
        try {
            val (input, c) = openCodec(code)
            val h = header(c)
            val result = sync(c, null)
            // Raw deflate has no checksum; leftover bytes mean a mistyped or mismatched code
            check(input.remaining() == 0) { "Corrupt data: trailing bytes" }
            return result to h
        } catch (e: Exception) {
            logDecodeFailure("Failed to decode", e)
        }
        return null
    }

    /**
     * Not intended for use outside sub-interfaces.
     *
     * Reads only the header (null on failure), without decoding the body.
     */
    fun <H> readHeaderWith(code: String, header: (Codec) -> H): H? {
        try {
            return header(openCodec(code).second)
        } catch (e: Exception) {
            logDecodeFailure("Failed to decode", e)
        }
        return null
    }

    private fun openCodec(code: String): Pair<ByteBuffers.In, Codec> {
        val clean = CodeRegistry.clean(code)
        check(clean.startsWith(CodeRegistry.header(typeId))) { "Wrong data type: expected '$typeId'" }
        val input = ByteBuffers.In(BinaryPack.unpack(clean.substring(CodeRegistry.HEADER_LENGTH)))
        val c = Codec(input)
        val ver = c.int(null)
        check(ver == version) { "Unsupported version $ver" }
        return input to c
    }

    @Suppress("SameParameterValue")
    private fun logDecodeFailure(what: String, e: Exception) {
        val log = Global.getLogger(this.javaClass)
        log.warn("$what: ${e.message}")
        log.debug("$what (stack trace)", e)
    }
}