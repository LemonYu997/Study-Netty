package example.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

/**
 * netty设计了一套基于内存池的缓冲区重用机制
 * 测试基于内存池循环利用的ByteBuff和普通ByteBuf的性能差异
 *
 * 测试结果：
 * 内存池分配缓冲区耗时：867ms.
 * 非内存池分配缓冲区耗时：544ms.
 */
public class PoolBufferDemo {
    public static void main(String[] args) {
        final byte[] CONTENT = new byte[1024];
        int loop = 3000000;
        long startTime = System.currentTimeMillis();

        //使用内存池分配缓冲区
        ByteBuf poolBuffer = null;
        for (int i = 0; i < loop; i++) {
            poolBuffer = PooledByteBufAllocator.DEFAULT.directBuffer();
            poolBuffer.writeBytes(CONTENT);
            poolBuffer.release();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("内存池分配缓冲区耗时：" + (endTime - startTime) + "ms.");


        //非内存池缓冲区
        long startTime2 = System.currentTimeMillis();
        ByteBuf buffer = null;
        for (int i = 0; i < loop; i++) {
            buffer = Unpooled.directBuffer(1024);
            buffer.writeBytes(CONTENT);
            buffer.release();
        }
        endTime = System.currentTimeMillis();
        System.out.println("非内存池分配缓冲区耗时：" + (endTime - startTime2) + "ms.");
    }


}
