package example.nio.channel;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用NIO写入数据：
 *  1、从FileOutputStream获取Channel
 *  2、创建Buffer
 *  3、将数据从Buffer中写入到Channel
 */
public class FileOutputDemo {
    static private final byte message[] = {83, 111, 109, 101, 32, 98, 121, 116, 101, 115, 46};

    public static void main(String[] args) throws Exception {
        //1、从FileInputStream获取Channel
        FileOutputStream fout = new FileOutputStream("F:\\Java\\Workspace\\Study-Netty\\src\\main\\java\\example\\nio\\channel\\test.txt");
        FileChannel channel = fout.getChannel();
        //2、创建Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //3、将数据从Buffer中写入到Channel
        for (int i = 0; i < message.length; i++) {
            buffer.put(message[i]);
        }

        buffer.flip();
        //写入Some bytes.
        channel.write(buffer);
        fout.close();
    }
}
