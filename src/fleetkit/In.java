package fleetkit;

import java.nio.charset.StandardCharsets;

class In {
    private final byte[] buf;
    private int pos = 0;

    In(byte[] data) { buf = data; }

    int readByte() {
        if (pos >= buf.length) throw new IllegalStateException("Unexpected end of data");
        return buf[pos++] & 0xFF;
    }
    boolean readBoolean() { return readByte() != 0; }
    int readInt() {
        return (readByte() << 24) | (readByte() << 16) | (readByte() << 8) | readByte();
    }
    String readUTF() {
        int n = readInt();
        if (n < 0 || pos + n > buf.length) throw new IllegalStateException("Bad string length");
        String s = new String(buf, pos, n, StandardCharsets.UTF_8);
        pos += n;
        return s;
    }
}