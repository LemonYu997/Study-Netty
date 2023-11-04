package example.netty.pipeline;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 自定义Inbound事件处理器A
 */
public class InboundHandlerA extends ChannelInboundHandlerAdapter {
    //读取Client发送来的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("InboundHandlerA");
        //向下传播
        ctx.fireChannelRead(msg);
    }
}
