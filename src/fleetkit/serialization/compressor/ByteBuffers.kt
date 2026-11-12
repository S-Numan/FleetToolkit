package fleetkit.serialization.compressor

// Byte buffers (no java.io, so the script sandbox is happy)
object ByteBuffers {
    class In(private val buf: ByteArray, private var pos: Int = 0) {
        fun remaining(): Int = buf.size - pos

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

        fun readLong(): Long {
            var x = 0L
            var shift = 0
            while (true) {
                val b = readByte()
                x = x or ((b and 0x7F).toLong() shl shift)
                if (b and 0x80 == 0) break
                shift += 7
                check(shift < 70) { "Bad varlong" }
            }
            return (x ushr 1) xor -(x and 1)
        }
        fun readFloat(): Float =
            Float.fromBits((readByte() shl 24) or (readByte() shl 16) or (readByte() shl 8) or readByte())
    }

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

        fun writeLong(v: Long) {
            var x = (v shl 1) xor (v shr 63)
            while (x and 0x7FL.inv() != 0L) {
                writeByte(((x and 0x7F) or 0x80).toInt())
                x = x ushr 7
            }
            writeByte(x.toInt())
        }
        fun writeFloat(v: Float) {
            val bits = v.toRawBits()
            writeByte(bits ushr 24); writeByte(bits ushr 16); writeByte(bits ushr 8); writeByte(bits)
        }

        fun toByteArray(): ByteArray = buf.copyOf(len)
    }
}