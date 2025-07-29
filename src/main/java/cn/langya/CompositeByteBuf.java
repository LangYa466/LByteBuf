package cn.langya;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author LangYa466
 * @date 2025/5/19
 */
public class CompositeByteBuf implements ByteBuf {
    public final List<ByteBuf> components;
    public int readerIndex;
    public int writerIndex;
    public int totalCapacity;

    public CompositeByteBuf(ByteBuf... buffers) {
        this.components = new ArrayList<>();
        this.readerIndex = 0;
        this.writerIndex = 0;
        for (ByteBuf buf : buffers) {
            addComponent(buf);
        }
    }

    private void addComponent(ByteBuf buf) {
        components.add(buf);
        totalCapacity += buf.writableBytes() + buf.readableBytes();
        writerIndex += buf.readableBytes();
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
        return totalCapacity - writerIndex;
    }

    private ComponentEntry getComponent(int index) {
        int readableBytes = 0;
        for (int i = 0; i < components.size(); i++) {
            ByteBuf component = components.get(i);
            int componentBytes = component.readableBytes();
            if (index < readableBytes + componentBytes) {
                return new ComponentEntry(i, component, index - readableBytes);
            }
            readableBytes += componentBytes;
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public byte readByte() {
        ComponentEntry entry = getComponent(readerIndex);
        readerIndex++;
        return entry.component.getByte(entry.componentIndex);
    }

    @Override
    public ByteBuf writeByte(byte b) {
        ComponentEntry entry = getComponent(writerIndex);
        entry.component.setByte(entry.componentIndex, b);
        writerIndex++;
        return this;
    }

    @Override
    public byte getByte(int index) {
        ComponentEntry entry = getComponent(index);
        return entry.component.getByte(entry.componentIndex);
    }

    @Override
    public ByteBuf setByte(int index, byte b) {
        ComponentEntry entry = getComponent(index);
        entry.component.setByte(entry.componentIndex, b);
        return this;
    }

    @Override
    public ByteBuf writeBytes(byte[] src) {
        for (byte b : src) {
            writeByte(b);
        }
        return this;
    }

    @Override
    public byte[] readBytes(int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = readByte();
        }
        return result;
    }

    // 为了简化实现，其他方法都委托给第一个组件
    @Override
    public ByteBuf writeInt(int value) {
        components.get(0).writeInt(value);
        return this;
    }

    @Override
    public int readInt() {
        return components.get(0).readInt();
    }

    @Override
    public ByteBuf writeLong(long value) {
        components.get(0).writeLong(value);
        return this;
    }

    @Override
    public long readLong() {
        return components.get(0).readLong();
    }

    @Override
    public ByteBuf writeFloat(float value) {
        components.get(0).writeFloat(value);
        return this;
    }

    @Override
    public float readFloat() {
        return components.get(0).readFloat();
    }

    @Override
    public ByteBuf writeDouble(double value) {
        components.get(0).writeDouble(value);
        return this;
    }

    @Override
    public double readDouble() {
        return components.get(0).readDouble();
    }

    @Override
    public ByteBuf writeChar(char value) {
        components.get(0).writeChar(value);
        return this;
    }

    @Override
    public char readChar() {
        return components.get(0).readChar();
    }

    @Override
    public ByteBuf writeString(String s) {
        components.get(0).writeString(s);
        return this;
    }

    @Override
    public String readString() {
        return components.get(0).readString();
    }

    @Override
    public ByteBuf writeObject(Serializable obj) throws IOException {
        components.get(0).writeObject(obj);
        return this;
    }

    @Override
    public <T> T readObject() throws IOException, ClassNotFoundException {
        return components.get(0).readObject();
    }

    @Override
    public ByteBuf slice(int index, int length) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ByteBuf duplicate() {
        throw new UnsupportedOperationException();
    }

    private static class ComponentEntry {
        final int componentId;
        final ByteBuf component;
        final int componentIndex;

        ComponentEntry(int componentId, ByteBuf component, int componentIndex) {
            this.componentId = componentId;
            this.component = component;
            this.componentIndex = componentIndex;
        }
    }
}