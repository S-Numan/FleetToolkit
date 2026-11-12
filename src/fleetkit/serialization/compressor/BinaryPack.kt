package fleetkit.serialization.compressor

import java.util.Base64
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater

object BinaryPack {
    private const val MAX_RAW_SIZE = 1_048_576 // 1 MB cap on decompressed data

    // Strategies to try. The decoder doesn't need to know which one won.
    private val STRATEGIES = intArrayOf(
        Deflater.DEFAULT_STRATEGY,
        //Deflater.FILTERED,
        //Deflater.HUFFMAN_ONLY
    )

    /** Compresses with one specific strategy. Raw deflate (no zlib header/checksum). */
    private fun deflate(raw: ByteArray, dict: ByteArray?, strategy: Int): ByteArray {
        val d = Deflater(Deflater.BEST_COMPRESSION, true)
        try {
            d.setStrategy(strategy)
            if (dict != null) d.setDictionary(dict)
            d.setInput(raw)
            d.finish()
            var tmp = ByteArray(raw.size + 64)
            var n = 0
            while (!d.finished()) {
                if (n == tmp.size) tmp = tmp.copyOf(tmp.size * 2)
                n += d.deflate(tmp, n, tmp.size - n)
            }
            return tmp.copyOf(n)
        } finally {
            d.end()
        }
    }

    fun pack(raw: ByteArray, dictVersion: Char = PackDictionaries.LATEST): String {
        val dict = PackDictionaries.byVersion[dictVersion]
        val usedVersion = if (dict != null) dictVersion else '0'

        // Try every strategy and keep the smallest result
        var best: ByteArray? = null
        for (strategy in STRATEGIES) {
            val candidate = deflate(raw, dict, strategy)
            if (best == null || candidate.size < best.size) best = candidate
        }

        return usedVersion + Base64.getEncoder().withoutPadding().encodeToString(best!!)
    }

    fun unpack(code: String): ByteArray {
        require(code.isNotEmpty()) { "Empty data code" }
        val dictVersion = code[0]
        val dict = if (dictVersion == '0') null
        else PackDictionaries.byVersion[dictVersion]
            ?: error("Unknown dictionary version '$dictVersion' (code from a newer version?)")

        val data = Base64.getDecoder().decode(code.substring(1))

        val inf = Inflater(true)
        try {
            if (dict != null) inf.setDictionary(dict)
            inf.setInput(data + 0.toByte()) // dummy byte, required by some zlib versions in nowrap mode
            var raw = ByteArray(1024)
            var off = 0
            while (!inf.finished()) {
                if (off == raw.size) {
                    check(raw.size < MAX_RAW_SIZE) { "Data too large" }
                    raw = raw.copyOf(raw.size * 2)
                }
                val k = inf.inflate(raw, off, raw.size - off)
                if (k == 0 && (inf.needsInput() || inf.needsDictionary())) break
                off += k
            }
            return raw.copyOf(off)
        } catch (e: DataFormatException) {
            throw IllegalStateException("Corrupt data code", e)
        } finally {
            inf.end()
        }
    }
}