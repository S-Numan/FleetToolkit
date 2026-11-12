package fleetkit.serialization.compressor

interface Format<T : Any> {
    val typeId: Char   // 'v' variant, 'm' member, 'p' person, 'f' fleet...
    val version: Int
    fun sync(c: Codec, d: T?): T

    fun encode(data: T): String {
        val out = ByteBuffers.Out()
        val c = Codec(out)
        c.i(typeId.code)
        c.i(version)
        sync(c, data)
        return BinaryPack.pack(out.toByteArray())
    }

    fun decode(code: String): T {
        val c = Codec(ByteBuffers.In(BinaryPack.unpack(code)))
        val type = c.i(null)
        check(type == typeId.code) { "Wrong data type: expected '$typeId'" }
        val ver = c.i(null)
        check(ver == version) { "Unsupported version $ver" }
        return sync(c, null)
    }
}