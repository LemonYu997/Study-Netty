package example.demo.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * 服务端分发请求
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    //获取Class路径
    private URL baseURL = HttpServerHandler.class.getResource("");
    private final String webroot = "webroot";

    private File getResource(String fileNmae) throws Exception {
        String basePath = baseURL.toURI().toString();
        int start = basePath.indexOf("classes/");
        basePath = (basePath.substring(0, start) + "/" + "classes/").replaceAll("/+", "/");

        String path = basePath + webroot + "/" + fileNmae;
        log.info("BaseURL:" + baseURL);
        path = !path.contains("file:") ? path : path.substring(5);
        path = path.replaceAll("//", "/");
        return new File(path);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();

        RandomAccessFile file = null;
        try {
            String page = uri.equals("/") ? "chat.html" : uri;
            file = new RandomAccessFile(getResource(page), "r");
        } catch (Exception e) {
            ctx.fireChannelRead(request.retain());
            return;
        }

        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        String contextType = "text/html;";
        if (uri.endsWith(".css")) {
            contextType = "text/css;";
        } else if (uri.endsWith(".js")) {
            contextType = "text/javascript;";
        } else if (uri.toLowerCase().matches(".*\\.(jpg|png|gif)$")) {
            String ext = uri.substring(uri.lastIndexOf("."));
            contextType = "image/" + ext;
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contextType + "charset=utf-8;");

        boolean keepAlive = HttpUtil.isKeepAlive(request);

        if (keepAlive) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        ctx.write(response);
        ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));

        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

        file.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel client = ctx.channel();
        log.info("Client:" + client.remoteAddress() + "异常");
        //出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
