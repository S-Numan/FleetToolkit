package fleetkit.serialization.core

import fleetkit.serialization.dictionaries.PackDictionaries
import java.util.*
import java.util.zip.CRC32
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater


object BinaryPack {
    private const val MAX_RAW_SIZE = 1_048_576 // 1 MB cap on decompressed data

    private const val CRC_BYTES = 4

    // Covers the version char too, so a mistyped dictionary version is caught as well
    private fun checksum(version: Char, data: ByteArray, len: Int = data.size): Int {
        val crc = CRC32()
        crc.update(version.code)
        crc.update(data, 0, len)
        return crc.value.toInt()
    }

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
        require(raw.size < MAX_RAW_SIZE) { "Data too large to pack: ${raw.size} bytes (limit ${MAX_RAW_SIZE - 1})" }

        val dict: ByteArray? =
            if (dictVersion == '0') null // '0' means no dictionary
            else PackDictionaries.byVersion[dictVersion] // Get dictionary if not '0'
                ?: throw IllegalArgumentException("Unknown dictionary version '$dictVersion'") // Unknown dictionary versions throw

        // Try every strategy, keep the smallest result
        val best = STRATEGIES
            .map { deflate(raw, dict, it) }
            .minBy { it.size }

        val framed = best.copyOf(best.size + CRC_BYTES)
        val crc = checksum(dictVersion, best)
        for (i in 0 until CRC_BYTES) framed[best.size + i] = (crc ushr (24 - 8 * i)).toByte()

        return dictVersion + Base64.getEncoder().withoutPadding().encodeToString(framed)
    }

    fun unpack(code: String): ByteArray {
        require(code.isNotEmpty()) { "Empty data code" }
        val dictVersion = code[0]
        val dict = if (dictVersion == '0') null
        else PackDictionaries.byVersion[dictVersion]
            ?: error("Unknown dictionary version '$dictVersion' (code from a newer version?)")

        val framed = try {
            Base64.getDecoder().decode(code.substring(1))
        } catch (e: IllegalArgumentException) {
            throw IllegalStateException("Corrupt data code", e)
        }
        check(framed.size > CRC_BYTES) { "Truncated data code" }

        val bodyLen = framed.size - CRC_BYTES
        var stored = 0
        for (i in 0 until CRC_BYTES) stored = (stored shl 8) or (framed[bodyLen + i].toInt() and 0xFF)
        check(stored == checksum(dictVersion, framed, bodyLen)) {
            "Checksum mismatch (typo or truncated code?)"
        }

        val data = framed.copyOf(bodyLen)

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
            // The loop also exits when input runs out, so confirm the stream really ended
            check(inf.finished()) { "Truncated data code" }
            return raw.copyOf(off)
        } catch (e: DataFormatException) {
            throw IllegalStateException("Corrupt data code", e)
        } finally {
            inf.end()
        }
    }
}