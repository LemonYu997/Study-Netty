package example.nio.buffer;

import java.io.FileInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Buffer三大属性：
 * position：当前操作位置
 * limit：从channel读入buffer时还有多少位置，即还剩多少可操作位置
 * capacity：缓冲区最大容量
 */
public class BufferDemo {
    public static void main(String[] args) throws Exception {
        //文件IO
        FileInputStream fin = new FileInputStream("F:\\Java\\Workspace\\Study-Netty\\src\\main\\java\\example\\nio\\buffer\\test.txt");
        //创建文件操作管道
        FileChannel fc = fin.getChannel();

        //分配一个10个大小的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(10);
        output("初始化", buffer);

        //读一下，将position指向读取了4位后的位置
        fc.read(buffer);
        output("调用read()", buffer);

        //锁定操作范围，将limit设置为当前position值（4），再将position设为0
        //此时可读取范围为0~4
        buffer.flip();
        output("调用flip()", buffer);

        //判断有没有可读数据
        //可以读取4位
        while (buffer.remaining() > 0) {
            byte b = buffer.get();
            //System.out.println((char) b);
        }
        output("调用get()", buffer);

        //解锁，复原，position指向0，limit指向结尾10
        buffer.clear();
        output("调用clear()", buffer);

        //关闭管道
        fin.close();
    }

    //打印缓冲区实时状态
    public static void output(String step, Buffer buffer) {
        System.out.println(step + " : ");
        //容量，数组大小
        System.out.print("capacity: " + buffer.capacity() + ", ");
        //当前操作数据所在的位置，游标
        System.out.print("position: " + buffer.position() + ", ");
        //锁定值，flip，数据操作范围索引只能在 position-limit 之间
        System.out.println("limit: " + buffer.limit());
    }
}
