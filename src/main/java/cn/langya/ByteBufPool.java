package cn.langya;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author LangYa466
 * @date 2025/5/19
 */
public class ByteBufPool {
    private static final int DEFAULT_INITIAL_CAPACITY = 256;
    private static final ConcurrentLinkedQueue<ByteBuf> pool = new ConcurrentLinkedQueue<>();

    public static ByteBuf acquire() {
        ByteBuf buf = pool.poll();
        if (buf == null) {
            return new UnpooledByteBuf(DEFAULT_INITIAL_CAPACITY);
        }
        return buf;
    }

    public static void release(ByteBuf buf) {
        if (buf instanceof UnpooledByteBuf) {
            ((UnpooledByteBuf) buf).clear();
            pool.offer(buf);
        }
    }

    public static void clear() {
        pool.clear();
    }
}