package example.demo.server;

import example.demo.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务端
 */
@Slf4j
public class ChatServer {
    private int port = 8080;

    public static void main(String[] args) {
        if (args.length > 0) {
            new ChatServer().start(Integer.parseInt(args[0]));
        } else {
            new ChatServer().start();
        }
    }

    public void start(int port) {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            ChannelPipeline pipeline = sc.pipeline();

                            //解析自定义协议
                            pipeline.addLast(new IMDecoder());  //Inbound
                            pipeline.addLast(new IMEncoder());  //Outbound
                            pipeline.addLast(new TermianlServerHandler());  //Inbound

                            //解析HTTP请求
                            pipeline.addLast(new HttpServerCodec());  //Outbound

                            //将同一个HTTP请求或响应的多个消息对象变成一个 fullHttpRequest 完整的消息对象
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));  //Inbound

                            //处理大数据流，比如1GB的文件传输过来会占满JVM内存
                            pipeline.addLast(new ChunkedWriteHandler());    //Inbound Outbound
                            pipeline.addLast(new HttpServerHandler());      //Inbound

                            //解析WebSocket请求
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));    //Inbound
                            pipeline.addLast(new WebSocketServerHandler()); //Inbound
                        }
                    });
            ChannelFuture f = b.bind(this.port).sync();
            log.info("服务已启动，监听端口：" + this.port);
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }

    public void start() {
        start(this.port);
    }
}
