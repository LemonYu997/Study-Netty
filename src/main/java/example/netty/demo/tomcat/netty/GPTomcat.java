package example.netty.demo.tomcat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 使用netty实现Tomcat容器
 */
public class GPTomcat {
    private int port = 8080;
    private Map<String, GPServlet> servletMapping = new HashMap<>();
    private Properties webxml = new Properties();

    //初始化
    private void init() {
        //加载web.properties，同时初始化ServletMapping对象
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");

            webxml.load(fis);

            //解析url和className
            for (Object k : webxml.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");

                    //单实例，多线程
                    GPServlet obj = (GPServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //准备就绪阶段
    public void start() {
        init();

        //Netty封装了NIO的Reactor模型
        //Boss和Worker
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //1、创建对象
            ServerBootstrap server = new ServerBootstrap();

            //2、配置参数
            server.group(bossGroup, workerGroup)
                    //主线程处理类
                    .channel(NioServerSocketChannel.class)
                    //子线程处理类 Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //客户端初始化处理
                        @Override
                        protected void initChannel(SocketChannel client) throws Exception {
                            //无锁化串行编程
                            //Netty对HTTP的封装顺序有要求
                            //HttpResponseEncoder 编码器
                            //责任链模式 双向链表 Inbound OutBound
                            client.pipeline().addLast(new HttpResponseEncoder());
                            //HttpResponseDecoder 解码器
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //业务处理逻辑
                            client.pipeline().addLast(new GPTomcatHandler());
                        }
                    })
                    //针对主线程的配置，分配县城最大数量 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //针对子线程的配置，保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            //3、启动服务器
            ChannelFuture f = server.bind(port).sync();
            System.out.println("GPTomcat已启动，监听的端口是：" + port);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 业务逻辑处理类
     */
    public class GPTomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                System.out.println("Hello");
                HttpRequest req = (HttpRequest) msg;

                //转交给自己的Request实现
                GPRequest request = new GPRequest(ctx, req);
                //转交给自己的response实现
                GPResponse response = new GPResponse(ctx, req);
                //实际处理业务
                String url = request.getUrl();

                if (servletMapping.containsKey(url)) {
                    servletMapping.get(url).service(request, response);
                } else {
                    response.write("404 - Not Found");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }
}
