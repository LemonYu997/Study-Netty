package example.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.TimeUnit;

/**
 * 自定义Outbound事件处理器B
 */
public class OutboundHandlerB extends ChannelOutboundHandlerAdapter {
    //向Client发送信息
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerB.write");
        //执行下一个OutboundChannel
        ctx.write(msg, promise);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
                ctx.channel().write("say hello");
            }
        }, 3, TimeUnit.SECONDS);
    }
}
