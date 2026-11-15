package fleetkit.serialization.core

import kotlin.math.roundToInt

// Bidirectional codec

class Codec private constructor(private val out: ByteBuffers.Out?, private val input: ByteBuffers.In?) {
    constructor(out: ByteBuffers.Out) : this(out, null)
    constructor(input: ByteBuffers.In) : this(null, input)

    val reading: Boolean get() = input != null

    private var depth = 0

    /** Wrap every recursive step in this. Throws on corrupt data that nests too deeply. */
    fun <T> nested(block: () -> T): T {
        check(++depth <= MAX_DEPTH) { "Corrupt data: nesting too deep" }
        try {
            return block()
        } finally {
            depth--
        }
    }

    // ---- primitives: write `v` (or a default if null) / read and ignore `v` ----
    fun int(v: Int?): Int = input?.readInt() ?: (v ?: 0).also { out!!.writeInt(it) }
    fun long(v: Long?): Long = input?.readLong() ?: (v ?: 0L).also { out!!.writeLong(it) }
    fun float(v: Float?): Float = input?.readFloat() ?: (v ?: 0f).also { out!!.writeFloat(it) }
    fun bool(v: Boolean?): Boolean = input?.readBoolean() ?: (v ?: false).also { out!!.writeBoolean(it) }
    fun str(v: String?): String = input?.readUTF() ?: (v ?: "").also { out!!.writeUTF(it) }

    // Enums are stored by ordinal (compact). Reordering/inserting enum entries changes the format.
    fun <E : Enum<E>> enum(v: E?, all: Array<E>): E =
        all.getOrElse(int(v?.ordinal ?: 0)) { all[0] }

    // ---- nullability: a 1-byte presence flag, then the value if present ----
    fun <T : Any> opt(v: T?, item: (T?) -> T): T? =
        if (bool(v != null)) item(v) else null

    fun <T : Any> opt(v: T?, format: Format<T>): T? = opt(v) { format.sync(this, it) }
    fun optStr(v: String?): String? = opt(v) { str(it) }
    fun optInt(v: Int?): Int? = opt(v) { int(it) }
    fun optFloat(v: Float?): Float? = opt(v) { float(it) }

    // Stores the float as round(v * 100) in a varint. Precision: 0.01.
    fun float2(v: Float?): Float = int(((v ?: 0f) * 100f).roundToInt()) / 100f
    fun optFloat2(v: Float?): Float? = opt(v) { float2(it) }

    private fun count(v: Int): Int {
        val n = int(v)
        if (input != null) check(n in 0..input.remaining()) { "Corrupt data: bad count $n" }
        return n
    }

    // ---- collections ----
    fun <T> list(v: Collection<T>?, item: (T?) -> T): List<T> {
        val src = v?.toList() ?: emptyList()
        val n = count(src.size)
        return List(n) { k -> item(src.getOrNull(k)) }
    }
    fun <T : Any> list(v: Collection<T>?, format: Format<T>): List<T> =
        list(v) { format.sync(this, it) }

    fun strList(v: Collection<String>?): List<String> = list(v) { str(it) }
    fun strSet(v: Collection<String>?): Set<String> = LinkedHashSet(strList(v))

    fun <V> map(v: Map<String, V>?, value: (V?) -> V): Map<String, V> {
        val src = v?.entries?.toList() ?: emptyList()
        val n = count(src.size)
        val result = LinkedHashMap<String, V>()
        for (k in 0 until n) {
            val e = src.getOrNull(k)
            val key = str(e?.key)
            result[key] = value(e?.value)
        }
        return result
    }

    // ---- loosely typed values (for memKeys). Unsupported types are written as null. ----
    fun any(v: Any?): Any? {
        val tag = int(
            if (reading) 0 else when (v) {
                null -> 0
                is Boolean -> 1
                is Int -> 2
                is Long -> 3
                is Float -> 4
                is Double -> 5
                is Short -> 6
                is Byte -> 7
                is Char -> 8
                is String -> 9
                else -> 0
            }
        )
        return when (tag) {
            1 -> bool(v as? Boolean)
            2 -> int(v as? Int)
            3 -> long(v as? Long)
            4 -> float(v as? Float)
            5 -> java.lang.Double.longBitsToDouble(long((v as? Double)?.toRawBits()))
            6 -> int((v as? Short)?.toInt()).toShort()
            7 -> int((v as? Byte)?.toInt()).toByte()
            8 -> int((v as? Char)?.code).toChar()
            9 -> str(v as? String)
            else -> null // tag 0: null or unsupported type, nothing follows
        }
    }

    private companion object {
        const val MAX_DEPTH = 32
    }
}