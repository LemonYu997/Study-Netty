package example.effective.optimize;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * Netty应用性能优化——客户端
 */
public class Client {
    private static final String SERVER_HOST = "127.0.0.1";

    public static void main(String[] args) throws Exception {
        new Client().start(8000);
    }

    public void start(int port) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        sc.pipeline().addLast(new FixedLengthFrameDecoder(Long.BYTES));
                        sc.pipeline().addLast(ClientHandler.INSTANCE);
                    }
                });

        //客户端每秒钟向服务端发起1000次请求
        for (int i = 0; i < 1000; i++) {
            b.connect(SERVER_HOST, port).get();
        }
    }
}
