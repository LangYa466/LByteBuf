package cn.langya;

import java.io.IOException;

/**
 * @author LangYa466
 * @date 2025/5/19
 */
public class ByteBufTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // 测试基本读写功能
        testBasicReadWrite();
        
        // 测试自动扩容
        testAutoExpansion();
        
        // 测试对象池
        testByteBufPool();
        
        // 测试零拷贝 - slice
        testSlice();
        
        // 测试零拷贝 - duplicate
        testDuplicate();
        
        // 测试零拷贝 - composite
        testComposite();

        // 测试清除缓冲区
        testClear();
        
        System.out.println("所有测试通过");
    }
    
    private static void testBasicReadWrite() throws IOException, ClassNotFoundException {
        ByteBuf buf = new UnpooledByteBuf(32);
        
        // 测试读写指针操作
        assert buf.readerIndex() == 0;
        assert buf.writerIndex() == 0;
        
        // 测试链式调用
        buf.writerIndex(5) // 设置写指针
           .readerIndex(2); // 设置读指针
        assert buf.writerIndex() == 5;
        assert buf.readerIndex() == 2;
        
        // 重置指针进行正常测试
        buf.writeString("Hello")
           .writeInt(123)
           .writeLong(4567890123L)
           .writeFloat(3.14f)
           .writeDouble(6.28)
           .writeChar('A')
           .writeObject("World")
           .writeBytes(new byte[]{10, 20, 30});

        assert "Hello".equals(buf.readString());
        assert 123 == buf.readInt();
        assert 4567890123L == buf.readLong();
        assert 3.14f == buf.readFloat();
        assert 6.28 == buf.readDouble();
        assert 'A' == buf.readChar();
        assert "World".equals(buf.readObject());
        byte[] bytes = buf.readBytes(3);
        assert bytes[0] == 10 && bytes[1] == 20 && bytes[2] == 30;
        
        System.out.println("基本读写测试通过");
    }
    
    private static void testAutoExpansion() {
        ByteBuf buf = new UnpooledByteBuf(4);
        byte[] data = new byte[16];
        buf.writeBytes(data) // 应该自动扩容
           .readerIndex(4) // 设置读指针位置
           .writeBytes(new byte[]{1, 2, 3, 4}); // 继续写入数据
        assert buf.writableBytes() >= 0;
        assert buf.readerIndex() == 4;
        
        System.out.println("自动扩容测试通过");
    }
    
    private static void testByteBufPool() {
        ByteBuf buf1 = ByteBufPool.acquire();
        buf1.writeInt(123);
        ByteBufPool.release(buf1);
        
        ByteBuf buf2 = ByteBufPool.acquire(); // 应该获取到之前释放的buf1
        assert buf2.readableBytes() == 0; // 确保已被清理
        
        System.out.println("对象池测试通过");
    }
    
    private static void testSlice() {
        ByteBuf original = new UnpooledByteBuf(16);
        original.writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7, 8});
        
        ByteBuf slice = original.slice(2, 4); // 获取中间4个字节的切片
        assert slice.readableBytes() == 4;
        assert slice.getByte(0) == 3; // 原buf的索引2处的值
        
        slice.setByte(1, (byte)99); // 修改切片应该影响原buf
        assert original.getByte(3) == (byte)99;
        
        // 写入时需要考虑剩余可写空间
        int writableBytes = slice.writableBytes();
        if (writableBytes > 0) {
            slice.writeByte((byte)88);
            assert original.getByte(2 + slice.readerIndex()) == (byte)88; // 2 is the slice offset
        }
        
        System.out.println("切片测试通过");
    }
    
    private static void testDuplicate() {
        ByteBuf original = new UnpooledByteBuf(16);
        original.writeBytes(new byte[]{1, 2, 3, 4});
        
        ByteBuf duplicate = original.duplicate();
        assert duplicate.readableBytes() == original.readableBytes();
        
        duplicate.setByte(0, (byte)99)
                  .writeByte((byte)88)
                  .writeBytes(new byte[]{77, 66});
        assert duplicate.getByte(0) == (byte)99;
        assert original.getByte(0) == 1; // 副本修改不应影响原buf
        
        System.out.println("副本测试通过");
    }
    
    private static void testComposite() {
        ByteBuf buf1 = new UnpooledByteBuf(8);
        ByteBuf buf2 = new UnpooledByteBuf(8);
        
        buf1.writeBytes(new byte[]{1, 2, 3, 4});
        buf2.writeBytes(new byte[]{5, 6, 7, 8});
        
        ByteBuf composite = ByteBuf.compositeBuffer(buf1, buf2);
        assert composite.readableBytes() == 8;
        
        byte[] allBytes = composite.readBytes(8);
        for (int i = 0; i < 8; i++) {
            assert allBytes[i] == i + 1;
        }
        
        System.out.println("组合ByteBuf测试通过");
    }

    private static void testClear() {
        UnpooledByteBuf buf = new UnpooledByteBuf(16);
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        assert buf.readerIndex() == 0;
        assert buf.writerIndex() == 4;

        buf.readerIndex(2); // 移动读指针
        assert buf.readerIndex() == 2;

        buf.clear(); // 测试clear方法
        assert buf.readerIndex() == 0 : "clear后读指针应该为0";
        assert buf.writerIndex() == 0 : "clear后写指针应该为0";

        System.out.println("清除缓冲区测试通过");
    }
}