package example.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 自定义InBound事件处理器C
 */
public class InboundHandlerC extends ChannelInboundHandlerAdapter {
    //读取Client发送来的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InboundHandlerC");
        //向下传播
        ctx.fireChannelRead(msg);
    }
}
