package fleetkit.serialization.compressor// Bidirectional codec

class Codec private constructor(private val out: ByteBuffers.Out?, private val input: ByteBuffers.In?) {
    constructor(out: ByteBuffers.Out) : this(out, null)
    constructor(input: ByteBuffers.In) : this(null, input)

    val reading: Boolean get() = input != null

    // ---- primitives: write `v` (or a default if null) / read and ignore `v` ----
    fun i(v: Int?): Int = input?.readInt() ?: (v ?: 0).also { out!!.writeInt(it) }
    fun l(v: Long?): Long = input?.readLong() ?: (v ?: 0L).also { out!!.writeLong(it) }
    fun f(v: Float?): Float = input?.readFloat() ?: (v ?: 0f).also { out!!.writeFloat(it) }
    fun b(v: Boolean?): Boolean = input?.readBoolean() ?: (v ?: false).also { out!!.writeBoolean(it) }
    fun s(v: String?): String = input?.readUTF() ?: (v ?: "").also { out!!.writeUTF(it) }

    // Enums are stored by ordinal (compact). Reordering/inserting enum entries changes the format.
    fun <E : Enum<E>> enum(v: E?, all: Array<E>): E =
        all.getOrElse(i(v?.ordinal ?: 0)) { all[0] }

    // ---- nullability: a 1-byte presence flag, then the value if present ----
    fun <T : Any> opt(v: T?, item: (T?) -> T): T? =
        if (b(v != null)) item(v) else null

    fun <T : Any> opt(v: T?, format: Format<T>): T? = opt(v) { format.sync(this, it) }
    fun optS(v: String?): String? = opt(v) { s(it) }
    fun optI(v: Int?): Int? = opt(v) { i(it) }
    fun optF(v: Float?): Float? = opt(v) { f(it) }

    // ---- collections ----
    fun <T> list(v: Collection<T>?, item: (T?) -> T): List<T> {
        val src = v?.toList() ?: emptyList()
        val n = i(src.size)
        return List(n) { k -> item(src.getOrNull(k)) }
    }
    fun <T : Any> list(v: Collection<T>?, format: Format<T>): List<T> =
        list(v) { format.sync(this, it) }

    fun strList(v: Collection<String>?): List<String> = list(v) { s(it) }
    fun strSet(v: Collection<String>?): Set<String> = LinkedHashSet(strList(v))

    fun <V> map(v: Map<String, V>?, value: (V?) -> V): Map<String, V> {
        val src = v?.entries?.toList() ?: emptyList()
        val n = i(src.size)
        val result = LinkedHashMap<String, V>()
        for (k in 0 until n) {
            val e = src.getOrNull(k)
            val key = s(e?.key)
            result[key] = value(e?.value)
        }
        return result
    }

    // ---- loosely typed values (for memKeys). Unsupported types are written as null. ----
    fun any(v: Any?): Any? {
        val tag = i(
            if (reading) 0 else when (v) {
                null -> 0
                is Boolean -> 1
                is Int -> 2
                is Long -> 3
                is Float -> 4
                is Double -> 5
                is String -> 6
                else -> 0
            }
        )
        return when (tag) {
            1 -> b(v as? Boolean)
            2 -> i(v as? Int)
            3 -> l(v as? Long)
            4 -> f(v as? Float)
            5 -> java.lang.Double.longBitsToDouble(l((v as? Double)?.toRawBits()))
            6 -> s(v as? String)
            else -> null
        }
    }
}