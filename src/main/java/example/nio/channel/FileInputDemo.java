package example.nio.channel;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用NIO读取数据
 * 1、从FileInputStream获取Channel
 * 2、创建Buffer
 * 3、将数据从Channel写入到Buffer
 */
public class FileInputDemo {
    public static void main(String[] args) throws Exception {
        FileInputStream fin = new FileInputStream("F:\\Java\\Workspace\\Study-Netty\\src\\main\\java\\example\\nio\\channel\\test.txt");

        //1、从FileInputStream获取Channel
        FileChannel channel = fin.getChannel();

        //2、创建Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //3、将数据从Channel写入到Buffer
        channel.read(buffer);

        buffer.flip();

        //输出Some bytes.
        while (buffer.hasRemaining()) {
            System.out.print((char) buffer.get());
        }

        fin.close();
    }
}
