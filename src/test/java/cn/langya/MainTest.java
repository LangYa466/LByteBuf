package cn.langya;

/**
 * @author LangYa466
 * @date 2025/5/19
 */
public class MainTest {
    public static void main(String[] args) throws Exception {
        ByteBuf buf = new UnpooledByteBuf(32);
        buf.writeString("Hello")
           .writeInt(123)
           .writeLong(4567890123L)
           .writeFloat(3.14f)
           .writeDouble(6.28)
           .writeChar('A')
           .writeObject("World")
           .writeBytes(new byte[]{10,20,30});

        System.out.println(buf.readString());
        System.out.println(buf.readInt());
        System.out.println(buf.readLong());
        System.out.println(buf.readFloat());
        System.out.println(buf.readDouble());
        System.out.println(buf.readChar());
        System.out.println(buf.readObject().toString());
        byte[] bytes = buf.readBytes(3);
        for (byte b : bytes) System.out.println(b);
    }
}
