# LByteBuf

# 轻量 高性能 Java ByteBuf 实现 支持零拷贝+自动扩容+读写指针分离+多数据类型序列化+对象池复用

## 导入maven/gradle使用
https://jitpack.io/#LangYa466/LByteBuf/-SNAPSHOT

## 示例
```java
ByteBuf buf = new UnpooledByteBuf(32);

buf.writeString("Hello")
   .writeInt(123)
   .writeLong(4567890123L)
   .writeFloat(3.14f)
   .writeDouble(6.28)
   .writeChar('A')
   .writeObject("World")
   .writeBytes(new byte[]{10, 20, 30});

System.out.println(buf.readString()); 
System.out.println(buf.readInt());     
System.out.println(buf.readLong()); 
System.out.println(buf.readFloat()); 
System.out.println(buf.readDouble()); 
System.out.println(buf.readChar());   
System.out.println(buf.readObject()); 
