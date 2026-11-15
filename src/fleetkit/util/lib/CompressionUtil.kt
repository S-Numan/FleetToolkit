package fleetkit.util.lib

import java.util.*
import java.util.zip.Deflater
import java.util.zip.Inflater

object CompressionUtil {
    const val BLOCK_SIZE = 1024

    /** Minimal growable byte buffer. java.io I miss you. */
    private class ByteSink {
        private var data = ByteArray(BLOCK_SIZE)
        private var size = 0

        fun write(src: ByteArray, offset: Int, length: Int) {
            val needed = size + length
            if (needed > data.size) {
                var newCapacity = data.size * 2
                while (newCapacity < needed) newCapacity *= 2
                val grown = ByteArray(newCapacity)
                System.arraycopy(data, 0, grown, 0, size)
                data = grown
            }
            System.arraycopy(src, offset, data, size, length)
            size += length
        }

        fun toByteArray(): ByteArray {
            val result = ByteArray(size)
            System.arraycopy(data, 0, result, 0, size)
            return result
        }
    }

    /** Compresses a string with DEFLATE and encodes it in Base64, optionally using a preset dictionary. */
    fun base64Deflate(input: String, dictionary: String? = null): String {
        val data = input.toByteArray(Charsets.UTF_8)
        val compresser = Deflater()
        try {
            if (!dictionary.isNullOrEmpty()) {
                compresser.setDictionary(dictionary.toByteArray(Charsets.UTF_8))
            }
            compresser.setInput(data)
            compresser.finish()

            val sink = ByteSink()
            val buffer = ByteArray(BLOCK_SIZE)
            while (!compresser.finished()) {
                val count = compresser.deflate(buffer)
                sink.write(buffer, 0, count)
            }
            return Base64.getEncoder().encodeToString(sink.toByteArray())
        } finally {
            compresser.end()
        }
    }

    /** Decodes Base64 and inflates it; returns null on invalid input or a missing/mismatched dictionary. */
    fun base64Inflate(input: String, dictionary: String? = null): String? {
        val decompresser = Inflater()
        try {
            val compressed = Base64.getDecoder().decode(input)
            decompresser.setInput(compressed)

            val dictBytes = dictionary?.takeIf { it.isNotEmpty() }?.toByteArray(Charsets.UTF_8)
            val sink = ByteSink()
            val buffer = ByteArray(BLOCK_SIZE)

            while (!decompresser.finished()) {
                val count = decompresser.inflate(buffer)
                if (count > 0) {
                    sink.write(buffer, 0, count)
                } else if (decompresser.needsDictionary()) {
                    // Throws IllegalArgumentException if the dictionary doesn't match.
                    if (dictBytes == null) return null
                    decompresser.setDictionary(dictBytes)
                } else if (decompresser.needsInput()) {
                    return null // Truncated data.
                }
            }

            // Decode once at the end so multibyte characters aren't split across blocks.
            return String(sink.toByteArray(), Charsets.UTF_8)
        } catch (_: Exception) {
            return null
        } finally {
            decompresser.end()
        }
    }
}