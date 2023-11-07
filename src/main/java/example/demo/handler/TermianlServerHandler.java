package example.demo.handler;

import example.demo.potocol.IMMessage;
import example.demo.server.MsgProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 支持自定义协议
 * 处理控制台发过来的Java Object 消息体
 */
@Slf4j
public class TermianlServerHandler extends SimpleChannelInboundHandler<IMMessage> {

    private MsgProcessor processor = new MsgProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IMMessage msg) throws Exception {
        processor.sendMsg(ctx.channel(), msg);
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("Socket Client: 与客户端断开连接：" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
