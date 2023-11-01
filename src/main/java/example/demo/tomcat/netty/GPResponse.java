package example.demo.tomcat.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

/**
 * Netty方式实现Response
 */
public class GPResponse {
    //socketChannel的封装
    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public GPResponse(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String out) throws Exception {
        try {
            if (out == null || out.length() == 0) {
                return;
            }
            //设置HTTP及请求头信息
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    //设置版本为HTTP1.1
                    HttpVersion.HTTP_1_1,
                    //设置响应状态码
                    HttpResponseStatus.OK,
                    //将输出内容编码格式设置为UTF-8
                    Unpooled.wrappedBuffer(out.getBytes(StandardCharsets.UTF_8)));

            response.headers().set("Content-Type", "text/html;");
            ctx.write(response);
        } finally {
            ctx.flush();
            ctx.close();
        }
    }
}
