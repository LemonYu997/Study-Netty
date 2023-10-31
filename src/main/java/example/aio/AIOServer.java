package example.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AIO服务端
 */
public class AIOServer {
    private final int port;

    public static void main(String[] args) {
        int port = 8000;
        new AIOServer(port);
    }

    //开启监听端口，并在CompletionHandler中处理接收到消息后的逻辑，将接收到的信息输出给客户端
    public AIOServer(int port) {
        this.port = port;
        listen();
    }

    private void listen() {
        try {
            // 创建线程池
            ExecutorService executorService = Executors.newCachedThreadPool();
            // 创建线程池组
            AsynchronousChannelGroup threadPool = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
            // 打开异步服务器套接字通道
            final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(threadPool);

            // 绑定监听地址和端口
            server.bind(new InetSocketAddress(port));
            System.out.println("服务已启动，监听端口：" + port);

            // 监听连接成功事件
            server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
                // 创建缓冲区
                final ByteBuffer buffer = ByteBuffer.allocate(1024);

                @Override
                public void completed(AsynchronousSocketChannel result, Object attachment) {
                    System.out.println("I/O操作成功，开始获取数据");
                    try {
                        buffer.clear();
                        //读取数据
                        result.read(buffer).get();
                        //重置buffer字节的可读状态
                        buffer.flip();
                        //写回数据
                        result.write(buffer);
                        buffer.flip();
                    } catch (Exception e) {
                        System.out.println(e);
                    } finally {
                        try {
                            result.close();
                            // 继续监听连接事件
                            server.accept(null, this);
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    System.out.println("操作完成");
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("I/O操作失败：" + exc);
                }
            });


            try {
                // 线程休眠直至被中断
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                System.out.println(e);
            }

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
