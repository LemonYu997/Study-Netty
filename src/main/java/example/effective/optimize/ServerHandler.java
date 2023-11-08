package example.effective.optimize;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Netty应用性能优化——服务端处理器
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static final ChannelHandler INSTANCE = new ServerHandler();

    //主线程
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        ByteBuf data = Unpooled.directBuffer();
        //从客户端读一个时间戳
        data.writeBytes(byteBuf);
        //模拟一次业务处理，可能是数据库操作或者逻辑处理
        Object result = getResult(data);
        //重新写会给客户端
        ctx.channel().writeAndFlush(result);
    }

    //模拟去数据库读取一个结果
    protected Object getResult(ByteBuf data) {
        int level = ThreadLocalRandom.current().nextInt(1, 1000);

        //计算出每次响应需要的时间，用来做QPS的参考数据
        //90.0% == 1ms
        int time;
        if (level <= 900) {
            time = 1;
        } else if (level <= 950) {
            //95% == 10ms
            time = 10;
        } else if (level <= 990) {
            //99% == 100ms
            time = 100;
        } else {
            //99.9% = 1000ms
            time = 1000;
        }

        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}
