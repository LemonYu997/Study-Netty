package example.netty.pipeline;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty服务端
 * Pipeline中Handler的执行顺序：
 * 从InboundHandler开始顺序执行，然后从Outbound逆序执行
 */
public class PipelineServer {
    public static void main(String[] args) throws Exception {
        PipelineServer server = new PipelineServer();
        server.start(8000);
    }

    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
//                            //InboundHandler的执行顺序为注册顺序，即 A->B->C
//                            sc.pipeline().addLast(new InboundHandlerA());
//                            sc.pipeline().addLast(new InboundHandlerB());
//                            sc.pipeline().addLast(new InboundHandlerC());
//
//                            //OutboundHandler的执行顺序应为注册顺序的逆序，即C->B->A
//                            sc.pipeline().addLast(new OutboundHandlerA());
//                            sc.pipeline().addLast(new OutboundHandlerB());
//                            sc.pipeline().addLast(new OutboundHandlerC());

                            //上述顺序的输出结果为：
                            // InboundHandlerA
                            // InboundHandlerB
                            // InboundHandlerC
                            // OutboundHandlerC.write
                            // OutboundHandlerB.write
                            // OutboundHandlerA.write

                            //调整注册顺序进行测试
                            sc.pipeline().addLast(new OutboundHandlerC());
                            sc.pipeline().addLast(new InboundHandlerB());
                            sc.pipeline().addLast(new OutboundHandlerA());
                            sc.pipeline().addLast(new InboundHandlerA());
                            sc.pipeline().addLast(new InboundHandlerC());
                            sc.pipeline().addLast(new OutboundHandlerB());

                            //输出结果为：从InboundHandler开始顺序执行，然后从Outbound逆序执行
                            // InboundHandlerB
                            // InboundHandlerA
                            // InboundHandlerC
                            // OutboundHandlerB.write
                            // OutboundHandlerA.write
                            // OutboundHandlerC.write

                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //启动
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
