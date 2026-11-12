package fleetkit;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class Out {
    private byte[] buf = new byte[256];
    private int len = 0;

    private void ensure(int extra) {
        if (len + extra > buf.length) buf = Arrays.copyOf(buf, Math.max(buf.length * 2, len + extra));
    }
    void writeByte(int b) { ensure(1); buf[len++] = (byte) b; }
    void writeBoolean(boolean b) { writeByte(b ? 1 : 0); }
    void writeInt(int v) {
        writeByte(v >>> 24); writeByte(v >>> 16); writeByte(v >>> 8); writeByte(v);
    }
    void writeUTF(String s) {
        byte[] b = s.getBytes(StandardCharsets.UTF_8);
        writeInt(b.length);
        ensure(b.length);
        System.arraycopy(b, 0, buf, len, b.length);
        len += b.length;
    }
    byte[] toByteArray() { return Arrays.copyOf(buf, len); }
}