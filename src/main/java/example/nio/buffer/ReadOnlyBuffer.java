package example.nio.buffer;

import java.nio.ByteBuffer;

/**
 * 只读缓冲区，如果不想缓冲区中的内容改变可以用这个
 * 只能读，不能写，只读缓冲区改变时会抛异常
 */
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        //缓冲区中的数据是0~9
        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i);
        }

        //创建只读缓冲区
        //数据与原缓冲区一致，如果原缓冲区改变，这个也改变
        ByteBuffer readOnly = buffer.asReadOnlyBuffer();

        //改变原缓冲区
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.get(i);
            b *= 10;
            buffer.put(i, b);
        }

        readOnly.position(0);
        readOnly.limit(buffer.capacity());

        //只读缓冲区内容也跟着改变
        while (readOnly.hasRemaining()) {
            //输出 0 10 20 30 40 50 60 70 80 90
            System.out.print(readOnly.get() + " ");
        }
    }
}
