package example.nio.buffer;

import java.nio.ByteBuffer;

/**
 * 手动分配缓冲区
 */
public class BufferWrap {
    public void myMethod() {
        //分配指定大小的缓冲区
        ByteBuffer b1 = ByteBuffer.allocate(10);

        //使用一个现有的数组
        byte[] array = new byte[10];
        ByteBuffer b2 = ByteBuffer.wrap(array);
    }
}
