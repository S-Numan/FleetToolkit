package fleetkit.serialization.compressor

import java.util.Base64
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater

internal object BinaryPack {
    fun pack(raw: ByteArray): String {
        val d = Deflater(Deflater.BEST_COMPRESSION, true) // no zlib header/checksum
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

    fun unpack(code: String): ByteArray {
        val data = Base64.getDecoder().decode(code)
        val inf = Inflater(true)
        inf.setInput(data + 0.toByte()) // dummy byte for nowrap mode
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
            throw IllegalStateException("Corrupt data code", e)
        } finally {
            inf.end()
        }
        return raw.copyOf(off)
    }
}