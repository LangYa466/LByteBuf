package cn.langya;

import java.io.*;

/**
 * @author LangYa466
 * @date 2025/5/19
 */
public interface ByteBuf {
    int readerIndex();

    int writerIndex();

    ByteBuf readerIndex(int readerIndex);

    ByteBuf writerIndex(int writerIndex);

    int readableBytes();

    int writableBytes();

    byte readByte();

    ByteBuf writeByte(byte b);

    byte getByte(int index);

    ByteBuf setByte(int index, byte b);

    ByteBuf writeBytes(byte[] src);

    byte[] readBytes(int length);

    // Primitive and object support
    ByteBuf writeInt(int value);

    int readInt();

    ByteBuf writeLong(long value);

    long readLong();

    ByteBuf writeFloat(float value);

    float readFloat();

    ByteBuf writeDouble(double value);

    double readDouble();

    ByteBuf writeChar(char value);

    char readChar();

    ByteBuf writeString(String s);

    String readString();

    ByteBuf writeObject(Serializable obj) throws IOException;

    <T> T readObject() throws IOException, ClassNotFoundException;

    ByteBuf slice(int index, int length);

    ByteBuf duplicate();

    static CompositeByteBuf compositeBuffer(ByteBuf... buffers) {
        return new CompositeByteBuf(buffers);
    }
}