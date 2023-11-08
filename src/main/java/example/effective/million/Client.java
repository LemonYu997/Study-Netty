package example.effective.million;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端
 */
public class Client {
    private static final String SERVER_HOST = "127.0.0.1";

    public static void main(String[] args) {
        new Client().start(Server.BEGIN_PORT, Server.END_PORT);
    }

    public void start(final int beginPort, int endPort) {
        System.out.println("客户端已启动");
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final Bootstrap b = new Bootstrap();

        b.group(eventLoopGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_REUSEADDR, true);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {

            }
        });

        //不断向服务器发送链接请求，测试极限
        int index = 0;
        int port;
        while (!Thread.interrupted()) {
            port = beginPort + index;
            try {
                ChannelFuture f = b.connect(SERVER_HOST, port);
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (!channelFuture.isSuccess()) {
                            System.out.println("链接失败，程序关闭!");
                            System.exit(0);
                        }
                    }
                });
                f.get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (port == endPort) {
                index = 0;
            } else {
                index++;
            }
        }
    }
}
