package example.nio.buffer;

import java.nio.ByteBuffer;

/**
 * 缓冲区分片 可以根据现有缓冲区分出一个子缓冲区
 */
public class BufferSlice {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        //缓冲区的数据 0~9
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i );
        }

        //创建子缓冲区 3~7
        buffer.position(3);
        buffer.limit(7);
        ByteBuffer slice = buffer.slice();

        //改变子缓冲区的内容
        for (int i = 0; i < slice.capacity(); i++) {
            byte b = slice.get(i);
            b *= 10;
            slice.put(i, b);
        }

        //相当于buffer.clear();
        buffer.position(0);
        buffer.limit(buffer.capacity());

        //输出0 1 2 30 40 50 60 7 8 9
        while (buffer.hasRemaining()) {
            System.out.print(buffer.get() + " ");
        }
    }
}
