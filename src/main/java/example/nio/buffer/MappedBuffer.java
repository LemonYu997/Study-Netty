package example.nio.buffer;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * IO内存映射缓冲区
 */
public class MappedBuffer {
    static private final int start = 0;
    static private final int size = 1024;

    public static void main(String[] args) throws Exception {
        RandomAccessFile raf = new RandomAccessFile("F:\\Java\\Workspace\\Study-Netty\\src\\main\\java\\example\\nio\\buffer\\test.txt", "rw");
        FileChannel fc = raf.getChannel();

        //把缓冲区和文件系统进行一个映射关联
        //只要操作缓冲区里面的内容改变，文件的内容也会改变
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, start, size);

        mbb.put(0, (byte) 97);
        mbb.put(1023, (byte) 122);

        raf.close();
    }
}
