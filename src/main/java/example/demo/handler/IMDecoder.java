package example.demo.handler;

import example.demo.potocol.IMMessage;
import example.demo.potocol.IMP;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;
import org.msgpack.MessageTypeException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义IMP的解码器
 */
public class IMDecoder extends ByteToMessageDecoder {
    //请求内容的正则
    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            //可读字节数
            final int length = in.readableBytes();
            final byte[] array = new byte[length];
            //读取到的消息
            String content = new String(array, in.readerIndex(), length);

            //空消息不解析
            if (null == content || "".equals(content.trim())) {
                if (!IMP.isIMP(content)) {
                    ctx.channel().pipeline().remove(this);
                    return;
                }
            }

            in.getBytes(in.readerIndex(), array, 0, length);
            out.add(new MessagePack().read(array, IMMessage.class));
            in.clear();
        } catch (MessageTypeException e) {
            ctx.channel().pipeline().remove(this);
        }
    }

    /**
     * 字符串解析成自定义即时通信协议
     */
    public IMMessage decode(String msg) {
        if (null == msg || "".equals(msg.trim())) {
            return null;
        }

        try {
            //匹配协议头和协议体
            Matcher m = pattern.matcher(msg);
            String header = "";
            String content = "";
            if (m.matches()) {
                header = m.group(1);
                content = m.group(3);
            }

            //协议头
            String[] headers = header.split("\\]\\[");

            //时间
            long time = 0;
            try {
                time = Long.parseLong(headers[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //昵称 最多十个字符长度
            String nickName = headers[2];
            nickName = nickName.length() < 10 ? nickName : nickName.substring(0, 9);

            if (msg.startsWith("[" + IMP.LOGIN.getName() + "]")) {
                return new IMMessage(headers[0], nickName, time, headers[3]);
            } else if (msg.startsWith("[" + IMP.CHAT.getName() + "]")) {
                return new IMMessage(headers[0], time, nickName, content);
            } else if (msg.startsWith("[" + IMP.FLOWER.getName() + "]")) {
                return new IMMessage(headers[0], headers[3], time, nickName);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
