package cn.langya;

/**
 * @author LangYa466
 * @date 2025/5/19
 */
public class SlicedByteBuf extends UnpooledByteBuf {
    private final UnpooledByteBuf parent;
    private final int offset;
    private final int length;

    public SlicedByteBuf(UnpooledByteBuf parent, int index, int length) {
        super(0); // 不实际分配内存
        this.parent = parent;
        this.offset = index;
        this.length = length;
        this.writerIndex = length;
    }

    @Override
    protected void ensureWritable(int minWritableBytes) {
        if (writerIndex + minWritableBytes > length) {
            throw new IndexOutOfBoundsException("Required capacity " + (writerIndex + minWritableBytes) +
                    " is greater than slice length " + length);
        }
        parent.ensureWritable(minWritableBytes);
    }

    @Override
    public byte getByte(int index) {
        checkIndex(index);
        return parent.getByte(offset + index);
    }

    @Override
    public ByteBuf setByte(int index, byte value) {
        checkIndex(index);
        parent.setByte(offset + index, value);
        return this;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public ByteBuf slice(int index, int length) {
        checkIndex(index);
        if (index + length > this.length) {
            throw new IndexOutOfBoundsException();
        }
        return new SlicedByteBuf(parent, offset + index, length);
    }

    @Override
    public ByteBuf duplicate() {
        return new SlicedByteBuf(parent, offset, length);
    }
}