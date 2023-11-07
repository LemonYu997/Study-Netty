package example.demo.client;

import example.demo.handler.IMDecoder;
import example.demo.handler.IMEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 客户端
 */
public class ChatClient {
    private ChatClientHandler clientHandler;
    private String host;
    private int port;

    public static void main(String[] args) {
        new ChatClient("Cover").connect("127.0.0.1", 8080);
        String url = "http://localhost:8080/images/a.png";
        System.out.println(url.toLowerCase().matches(".*\\.(gif|png|jpg)$"));
    }

    public ChatClient(String nickName) {
        this.clientHandler = new ChatClientHandler(nickName);
    }

    public void connect(String host, int port) {
        this.host = host;
        this.port = port;

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel sc) throws Exception {
                    sc.pipeline().addLast(new IMDecoder());
                    sc.pipeline().addLast(new IMEncoder());
                    sc.pipeline().addLast(clientHandler);
                }
            });
            ChannelFuture f = b.connect(this.host, this.port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
