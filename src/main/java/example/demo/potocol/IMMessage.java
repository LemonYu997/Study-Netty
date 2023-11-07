package example.demo.potocol;

import lombok.Data;
import lombok.ToString;
import org.msgpack.annotation.Message;

/**
 * 自定义消息实体类
 */
@Message
@Data
@ToString
public class IMMessage {
    //IP地址及端口
    private String addr;
    //命令类型 [Login]或[SYSTEM]或[LOGOUT]
    private String cmd;
    //命令发送时间
    private long time;
    //当前在线人数
    private int online;
    //发送人
    private String sender;
    //接收人
    private String receiver;
    //消息内容
    private String content;
    //终端
    private String terminal;

    public IMMessage() {}

    public IMMessage(String cmd, long time, int online, String content) {
        this.cmd = cmd;
        this.time = time;
        this.online = online;
        this.content = content;
    }

    //long类型参数位置不同，来和下边的方法作区分
    public IMMessage(String cmd, String sender, long time, String terminal) {
        this.cmd = cmd;
        this.sender = sender;
        this.time = time;
        this.terminal = terminal;
    }

    public IMMessage(String cmd, long time, String sender, String content) {
        this.cmd = cmd;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }
}
