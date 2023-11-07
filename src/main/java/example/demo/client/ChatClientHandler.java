package example.demo.client;

import example.demo.potocol.IMMessage;
import example.demo.potocol.IMP;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 聊天客户端逻辑实现
 * 将控制台输入的文本信息转换为IMMessage对象发送到服务端，将服务端传过来Java Object转换为文本输出到控制台
 * 服务端消息包括从HTML页面发送过来的消息，也包括其他客户端从Java控制台发送过来的消息
 */
@Slf4j
public class ChatClientHandler extends SimpleChannelInboundHandler<IMMessage> {
    private ChannelHandlerContext ctx;
    private String nickName;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 启动客户端控制台
     */
    private void session() throws IOException {
        new Thread() {
            public void run() {
                System.out.println(nickName + "，你好，请在控制台输入对话内容：");
                IMMessage msg = null;
                Scanner scanner = new Scanner(System.in);
                do {
                    if (scanner.hasNext()) {
                        String input = scanner.nextLine();
                        if ("exit".equals(input)) {
                            msg = new IMMessage(IMP.LOGOUT.getName(), "Console", System.currentTimeMillis(), nickName);
                        } else {
                            msg = new IMMessage(IMP.CHAT.getName(), System.currentTimeMillis(), nickName, input);
                        }
                    }
                } while (sendMsg(msg));
                scanner.close();
            }
        }.start();
    }

    /**
     * TCP链路建立成功后调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        IMMessage msg = new IMMessage(IMP.LOGIN.getName(), "Console", System.currentTimeMillis(), this.nickName);
        sendMsg(msg);
        log.info("成功连接服务器，已执行登录操作");
        session();
    }

    /**
     * 发送消息
     */
    private boolean sendMsg(IMMessage msg) {
        ctx.channel().writeAndFlush(msg);
        System.out.println("继续输入开始对话...");
        return msg.getCmd().equals(IMP.LOGOUT) ? false : true;
    }

    /**
     * 收到消息后调用
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IMMessage msg) throws Exception {
        IMMessage m = msg;
        System.out.println((null == m.getSender() ? "" : (m.getSender() + ":")) + removeHtmlTag(m.getContent()));
    }

    public static String removeHtmlTag(String htmlStr) {
        //定义script的正则表达式
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义Style的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义Html标签的正则表达式
        String regEx_html = "<[^>]+>";

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        //过滤script标签
        htmlStr = m_script.replaceAll("");

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        //过滤Style标签
        htmlStr = m_style.replaceAll("");

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        //过滤HTML标签
        htmlStr = m_html.replaceAll("");

        //返回文本字符串
        return htmlStr.trim();
    }

    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("与服务器断开连接：" + cause.getMessage());
        ctx.close();
    }
}
