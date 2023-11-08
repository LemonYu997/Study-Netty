package example.effective.optimize;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * Netty应用性能优化——服务端
 */
public class Server {
    private static final int port = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        final NioEventLoopGroup businessGroup = new NioEventLoopGroup(1000);

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_REUSEADDR, true);

        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
                //自定义长度的解码，每次发送一个long类型的长度数据
                //每次传递一个系统的时间戳
                sc.pipeline().addLast(new FixedLengthFrameDecoder(Long.BYTES));
//                sc.pipeline().addLast(businessGroup, ServerHandler.INSTANCE);
                //性能优化 使用线程池来处理任务
                sc.pipeline().addLast(ServerThreadPoolHandler.INSTANCE);

            }
        });

        ChannelFuture channelFuture = b.bind(port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                System.out.println("服务端已启动，绑定端口为：" + port);
            }
        });
    }
}
