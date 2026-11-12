package fleetkit.serialization.compressor

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
        val c = Codec(ByteBuffers.In(BinaryPack.unpack(clean.substring(1))))
        val ver = c.int(null)
        check(ver == version) { "Unsupported version $ver" }
        return sync(c, null)
    }
}