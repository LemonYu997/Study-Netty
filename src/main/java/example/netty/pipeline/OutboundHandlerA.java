package example.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * 自定义Outbound事件处理器A
 */
public class OutboundHandlerA extends ChannelOutboundHandlerAdapter {
    //向Client发送信息
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("OutboundHandlerA.write");
        //执行下一个OutboundChannel
        ctx.write(msg, promise);
    }
}
