package example.nio.selector;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO中非阻塞IO编写服务器处理程序，可以分为以下三个步骤：
 *  1、向Selector对象注册感兴趣的事件
 *  2、从Selector中获取感兴趣的事件
 *  3、根据不同的事件进行相应处理
 */
public class SelectorDemo {
    static final int port = 8000;
    Selector selector = getSelector();
    ByteBuffer buffer = ByteBuffer.allocate(1024);


    public SelectorDemo() throws Exception {
    }

    /**
     * 1、注册事件
     */
    private Selector getSelector() throws Exception {
        //创建Selector对象
        Selector sel = Selector.open();

        //创建可选择通道，并配置为非阻塞模式
        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);

        //绑定通道到指定端口
        ServerSocket socket = server.socket();
        InetSocketAddress adress = new InetSocketAddress(port);
        socket.bind(adress);

        // 向Selector注册感兴趣的事件
        // 这里是想要监听accept事件（即连接发生时的事件）
        server.register(sel, SelectionKey.OP_ACCEPT);
        return sel;
    }

    /**
     * 2、开始监听
     */
    public void listen() {
        System.out.println("listen on " + port);

        try {
            while (true) {
                //该调用会阻塞，直到至少有一个事件发生
                selector.select();
                //获取发生事件的SelectionKey
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                //处理不同事件
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    process(key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 3、事件处理
     */
    private void process(SelectionKey key) throws Exception {
        //接受请求
        if (key.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        }

        //读事数据
        else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            int len = channel.read(buffer);
            if (len > 0) {
                buffer.flip();
                String content = new String(buffer.array(), 0, len);
                SelectionKey sKey = channel.register(selector, SelectionKey.OP_WRITE);
                sKey.attach(content);
            } else {
                channel.close();
            }
            buffer.clear();
        }

        //写事件
        else if (key.isWritable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            String content = (String) key.attachment();
            ByteBuffer block = ByteBuffer.wrap(("输出内容：" + content).getBytes());
            if (block != null) {
                channel.write(block);
            } else {
                channel.close();;
            }
        }

    }
}
