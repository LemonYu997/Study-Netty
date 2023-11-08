package example.effective.million;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 模拟单机百万链接——服务端
 */
public final class Server {
    public static final int BEGIN_PORT = 8000;
    public static final int END_PORT = 8100;

    public static void main(String[] args) {
        new Server().start(Server.BEGIN_PORT, Server.END_PORT);
    }

    public void start(int beginPort, int endPort) {
        System.out.println("服务端启动中");

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        //地址复用，默认为false
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childHandler(new ConnectionCountHandler());

        for (int i = 0; i <= (endPort - beginPort); i++) {
            final int port = beginPort + i;

            bootstrap.bind(port).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("成功绑定监听端口：" + port);
                }
            });
        }

        System.out.println("服务端已启动");
    }

}
