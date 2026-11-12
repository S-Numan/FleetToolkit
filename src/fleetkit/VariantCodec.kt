package fleetkit

import com.fs.starfarer.api.loading.WeaponGroupType
import fleetkit.serialization.variant.DataVariant
import java.util.*
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater

// ---------- Byte buffers (no java.io, so the script sandbox is happy) ----------

class Out {
    private var buf = ByteArray(256)
    private var len = 0

    private fun ensure(extra: Int) {
        if (len + extra > buf.size) buf = buf.copyOf(maxOf(buf.size * 2, len + extra))
    }

    fun writeByte(b: Int) { ensure(1); buf[len++] = b.toByte() }
    fun writeBoolean(b: Boolean) = writeByte(if (b) 1 else 0)
    fun writeInt(v: Int) {
        writeByte(v ushr 24); writeByte(v ushr 16); writeByte(v ushr 8); writeByte(v)
    }
    fun writeUTF(s: String) {
        val b = s.toByteArray(Charsets.UTF_8)
        writeInt(b.size)
        ensure(b.size)
        System.arraycopy(b, 0, buf, len, b.size)
        len += b.size
    }
    fun toByteArray(): ByteArray = buf.copyOf(len)
}

class In(private val buf: ByteArray, private var pos: Int = 0) {
    fun readByte(): Int {
        check(pos < buf.size) { "Unexpected end of data" }
        return buf[pos++].toInt() and 0xFF
    }
    fun readBoolean(): Boolean = readByte() != 0
    fun readInt(): Int =
        (readByte() shl 24) or (readByte() shl 16) or (readByte() shl 8) or readByte()
    fun readUTF(): String {
        val n = readInt()
        check(n >= 0 && pos + n <= buf.size) { "Bad string length" }
        val s = String(buf, pos, n, Charsets.UTF_8)
        pos += n
        return s
    }
}

// ---------- Bidirectional codec ----------

class Codec private constructor(private val out: Out?, private val input: In?) {
    constructor(out: Out) : this(out, null)
    constructor(input: In) : this(null, input)

    val reading: Boolean get() = input != null

    fun i(v: Int?): Int = input?.readInt() ?: (v ?: 0).also { out!!.writeInt(it) }
    fun b(v: Boolean?): Boolean = input?.readBoolean() ?: (v ?: false).also { out!!.writeBoolean(it) }
    fun s(v: String?): String = input?.readUTF() ?: (v ?: "").also { out!!.writeUTF(it) }

    // Generic list: `item` reads/writes one element (it receives null when reading)
    fun <T> list(v: Collection<T>?, item: (T?) -> T): List<T> {
        val src = v?.toList() ?: emptyList()
        val n = i(src.size)
        return List(n) { k -> item(src.getOrNull(k)) }
    }

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
}

// ---------- Format definition (written once, used for both directions) ----------

object VariantCodec {
    private const val VERSION = 1

    private fun syncGroup(c: Codec, g: DataVariant.ParsedWeaponGroup?) = DataVariant.ParsedWeaponGroup(
        autofire = c.b(g?.autofire),
        mode = WeaponGroupType.entries[c.i(g?.mode?.ordinal)],
        weapons = c.map(g?.weapons) { c.s(it) }
    )

    private fun sync(c: Codec, d: DataVariant.ParsedVariantData?): DataVariant.ParsedVariantData = DataVariant.ParsedVariantData(
        hullId = c.s(d?.hullId),
        variantId = c.s(d?.variantId),
        displayName = c.s(d?.displayName),
        fluxCapacitors = c.i(d?.fluxCapacitors),
        fluxVents = c.i(d?.fluxVents),
        tags = c.strList(d?.tags),
        hullMods = c.strSet(d?.hullMods),
        permaMods = c.strSet(d?.permaMods),
        sMods = c.strSet(d?.sMods),
        sModdedBuiltIns = c.strSet(d?.sModdedBuiltIns),
        suppressedMods = c.strSet(d?.suppressedMods),
        wings = c.strList(d?.wings),
        weaponGroups = c.list(d?.weaponGroups) { syncGroup(c, it) },
        moduleVariants = c.map(d?.moduleVariants) { sync(c, it) }
    )

    @JvmStatic
    fun encode(data: DataVariant.ParsedVariantData): String {
        val out = Out()
        val c = Codec(out)
        c.i(VERSION)
        sync(c, data)
        val raw = out.toByteArray()

        val d = Deflater(Deflater.BEST_COMPRESSION)
        d.setInput(raw)
        d.finish()
        var tmp = ByteArray(raw.size + 64)
        var n = 0
        while (!d.finished()) {
            if (n == tmp.size) tmp = tmp.copyOf(tmp.size * 2)
            n += d.deflate(tmp, n, tmp.size - n)
        }
        d.end()

        // prefix with raw length so decode knows how much to inflate
        val header = Out().apply { writeInt(raw.size) }.toByteArray()
        val result = header + tmp.copyOf(n)
        return Base64.getEncoder().encodeToString(result)
    }

    @JvmStatic
    fun decode(base64: String): DataVariant.ParsedVariantData {
        val data = Base64.getDecoder().decode(base64)
        val rawLen = In(data).readInt()

        val inf = Inflater()
        inf.setInput(data, 4, data.size - 4)
        val raw = ByteArray(rawLen)
        try {
            var off = 0
            while (off < rawLen && !inf.finished()) {
                off += inf.inflate(raw, off, rawLen - off)
            }
        } catch (e: DataFormatException) {
            throw IllegalStateException("Corrupt variant code", e)
        } finally {
            inf.end()
        }

        val c = Codec(In(raw))
        val version = c.i(null)
        check(version == VERSION) { "Unsupported version $version" }
        return sync(c, null)
    }
}