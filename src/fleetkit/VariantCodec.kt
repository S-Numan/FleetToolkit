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

    // zigzag + varint: small magnitudes (incl. -1) take 1 byte
    fun writeInt(v: Int) {
        var x = (v shl 1) xor (v shr 31)
        while (x and 0x7F.inv() != 0) {
            writeByte((x and 0x7F) or 0x80)
            x = x ushr 7
        }
        writeByte(x)
    }

    // null-terminated; \u0000 is stripped since it would break decoding
    fun writeUTF(s: String) {
        val b = s.replace("\u0000", "").toByteArray(Charsets.UTF_8)
        ensure(b.size + 1)
        System.arraycopy(b, 0, buf, len, b.size)
        len += b.size
        buf[len++] = 0
    }

    fun toByteArray(): ByteArray = buf.copyOf(len)
}

class In(private val buf: ByteArray, private var pos: Int = 0) {
    fun readByte(): Int {
        check(pos < buf.size) { "Unexpected end of data" }
        return buf[pos++].toInt() and 0xFF
    }
    fun readBoolean(): Boolean = readByte() != 0

    fun readInt(): Int {
        var x = 0
        var shift = 0
        while (true) {
            val b = readByte()
            x = x or ((b and 0x7F) shl shift)
            if (b and 0x80 == 0) break
            shift += 7
            check(shift < 35) { "Bad varint" }
        }
        return (x ushr 1) xor -(x and 1)
    }

    fun readUTF(): String {
        val start = pos
        while (true) {
            check(pos < buf.size) { "Unterminated string" }
            if (buf[pos] == 0.toByte()) break
            pos++
        }
        val s = String(buf, start, pos - start, Charsets.UTF_8)
        pos++ // skip terminator
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

        val d = Deflater(Deflater.BEST_COMPRESSION, true) // true = no zlib header/checksum
        d.setInput(raw)
        d.finish()
        var tmp = ByteArray(raw.size + 64)
        var n = 0
        while (!d.finished()) {
            if (n == tmp.size) tmp = tmp.copyOf(tmp.size * 2)
            n += d.deflate(tmp, n, tmp.size - n)
        }
        d.end()

        return Base64.getEncoder().withoutPadding().encodeToString(tmp.copyOf(n))
    }

    @JvmStatic
    fun decode(base64: String): DataVariant.ParsedVariantData {
        val data = Base64.getDecoder().decode(base64) // accepts unpadded input

        val inf = Inflater(true)
        inf.setInput(data + 0.toByte()) // extra dummy byte, required by some zlib versions in nowrap mode
        var raw = ByteArray(1024)
        var off = 0
        try {
            while (!inf.finished()) {
                if (off == raw.size) raw = raw.copyOf(raw.size * 2)
                val k = inf.inflate(raw, off, raw.size - off)
                if (k == 0 && (inf.needsInput() || inf.needsDictionary())) break
                off += k
            }
        } catch (e: DataFormatException) {
            throw IllegalStateException("Corrupt variant code", e)
        } finally {
            inf.end()
        }

        val c = Codec(In(raw.copyOf(off)))
        val version = c.i(null)
        check(version == VERSION) { "Unsupported version $version" }
        return sync(c, null)
    }
}