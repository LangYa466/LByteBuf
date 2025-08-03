package cn.langya;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author LangYa466
 * @date 2025/5/19
 */
public class UnpooledByteBuf implements ByteBuf {
    public byte[] buffer;
    public int readerIndex;
    public int writerIndex;

    public UnpooledByteBuf(int initialCapacity) {
        this.buffer = new byte[initialCapacity];
        this.readerIndex = this.writerIndex = 0;
    }

    protected void ensureWritable(int minWritableBytes) {
        int required = writerIndex + minWritableBytes;
        if (required > buffer.length) {
            int newCapacity = Math.max(buffer.length << 1, required);
            byte[] newBuf = new byte[newCapacity];
            System.arraycopy(buffer, 0, newBuf, 0, writerIndex);
            buffer = newBuf;
        }
    }

    @Override
    public int readerIndex() {
        return readerIndex;
    }

    @Override
    public int writerIndex() {
        return writerIndex;
    }

    @Override
    public ByteBuf readerIndex(int readerIndex) {
        this.readerIndex = readerIndex;
        return this;
    }

    @Override
    public ByteBuf writerIndex(int writerIndex) {
        this.writerIndex = writerIndex;
        return this;
    }

    @Override
    public int readableBytes() {
        return writerIndex - readerIndex;
    }

    @Override
    public int writableBytes() {
        return buffer.length - writerIndex;
    }

    @Override
    public byte readByte() {
        if (readerIndex >= writerIndex) throw new IndexOutOfBoundsException();
        return buffer[readerIndex++];
    }

    @Override
    public ByteBuf writeByte(byte b) {
        ensureWritable(1);
        buffer[writerIndex++] = b;
        return this;
    }

    @Override
    public byte getByte(int index) {
        return buffer[index];
    }

    @Override
    public ByteBuf setByte(int index, byte b) {
        buffer[index] = b;
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        ensureWritable(src.length);
        System.arraycopy(src, 0, buffer, writerIndex, src.length);
        writerIndex += src.length;
        return this;
    }

    @Override
    public byte[] readBytes(int length) {
        if (readerIndex + length > writerIndex) {
            throw new IndexOutOfBoundsException("readBytes: Not enough readable bytes. Required: " + length + ", Available: " + (writerIndex - readerIndex));
        }
        byte[] dst = new byte[length];
        System.arraycopy(buffer, readerIndex, dst, 0, length);
        readerIndex += length;
        return dst;
    }

    @Override
    public ByteBuf writeInt(int value) {
        ensureWritable(4);
        buffer[writerIndex++] = (byte) (value >> 24);
        buffer[writerIndex++] = (byte) (value >> 16);
        buffer[writerIndex++] = (byte) (value >> 8);
        buffer[writerIndex++] = (byte) (value);
        return this;
    }

    @Override
    public int readInt() {
        return ((buffer[readerIndex++] & 0xFF) << 24) |
                ((buffer[readerIndex++] & 0xFF) << 16) |
                ((buffer[readerIndex++] & 0xFF) << 8) |
                (buffer[readerIndex++] & 0xFF);
    }

    @Override
    public ByteBuf writeLong(long value) {
        ensureWritable(8);
        for (int i = 7; i >= 0; i--) {
            buffer[writerIndex++] = (byte) (value >> (i * 8));
        }
        return this;
    }

    @Override
    public long readLong() {
        long v = 0;
        for (int i = 7; i >= 0; i--) {
            v |= ((long) (buffer[readerIndex++] & 0xFF)) << (i * 8);
        }
        return v;
    }

    @Override
    public ByteBuf writeFloat(float value) {
        return writeInt(Float.floatToIntBits(value));
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public ByteBuf writeDouble(double value) {
        return writeLong(Double.doubleToLongBits(value));
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public ByteBuf writeChar(char value) {
        ensureWritable(2);
        buffer[writerIndex++] = (byte) (value >> 8);
        buffer[writerIndex++] = (byte) (value);
        return this;
    }

    @Override
    public char readChar() {
        return (char) (((buffer[readerIndex++] & 0xFF) << 8) | (buffer[readerIndex++] & 0xFF));
    }
    
    @Override
    public boolean readBoolean() {
        return readByte() != 0;
    }
    
    @Override
    public ByteBuf writeBoolean(boolean value) {
        writeByte(value ? (byte) 1 : (byte) 0);
        return this;
    }

    @Override
    public ByteBuf writeString(String s) {
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        writeInt(data.length);
        writeBytes(data);
        return this;
    }

    @Override
    public String readString() {
        int len = readInt();
        byte[] data = readBytes(len);
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public ByteBuf writeObject(Serializable obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        byte[] data = baos.toByteArray();
        writeInt(data.length);
        writeBytes(data);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject() throws IOException, ClassNotFoundException {
        int len = readInt();
        byte[] data = readBytes(len);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        return (T) ois.readObject();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        if (index < 0 || length < 0 || index + length > buffer.length) {
            throw new IndexOutOfBoundsException();
        }
        return new SlicedByteBuf(this, index, length);
    }

    @Override
    public ByteBuf duplicate() {
        UnpooledByteBuf copy = new UnpooledByteBuf(buffer.length);
        System.arraycopy(buffer, 0, copy.buffer, 0, buffer.length);
        copy.readerIndex = readerIndex;
        copy.writerIndex = writerIndex;
        return copy;
    }

    public void clear() {
        readerIndex = 0;
        writerIndex = 0;
    }
}