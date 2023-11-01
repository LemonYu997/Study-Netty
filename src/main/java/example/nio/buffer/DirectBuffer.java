package example.nio.buffer;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 直接缓冲区
 * 以一种特殊方式分配内存的更快的缓冲区
 */
public class DirectBuffer {
    public static void main(String[] args) throws Exception {
        //首先从磁盘上读取文件
        String inFile = "F:\\Java\\Workspace\\Study-Netty\\src\\main\\java\\example\\nio\\buffer\\test.txt";
        FileInputStream fin = new FileInputStream(inFile);
        //创建文件操作管道
        FileChannel fcin = fin.getChannel();

        //把读取的内容写入到一个新文件
        String outFile = "F:\\Java\\Workspace\\Study-Netty\\src\\main\\java\\example\\nio\\buffer\\testcopy.text";
        FileOutputStream fout = new FileOutputStream(outFile);
        FileChannel fcout = fout.getChannel();

        //使用allocateDirect创建直接缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        while (true) {
            buffer.clear();

            int r = fcin.read(buffer);

            if (r == -1) {
                break;
            }

            buffer.flip();
            fcout.write(buffer);
        }
    }
}
