package example.demo.potocol;

/**
 * 自定义及时通信协议 IMP (Instant Messaging Protocol)
 * 协议规则：
 *  下行命令：服务端向客户端发送的命令
 *      SYSTEM: 系统命令 [命令][命令发送时间][接收人] - 系统提示内容
 *              [SYSTEM][12343234123][Tom] - Student加入聊天室
 *
 *  上行命令：客户端向服务端发送的命令
 *      LOGIN:  登录 [命令][命令发送时间][发送人]
 *              [LOGIN][12343234123][Tom]
 *      LOGOUT: 退出登录 [命令][命令发送时间][发送人]
 *              [LOGOUT][12343234123][Tom]
 *      CHAT:   聊天 [命令][命令发送时间][发送人][接收人] - 聊天内容
 *              [CHAT][12343234123][Tom][ALL] - 大家好，我是Tom
 *      FLOWER: 鲜花特效 [命令][命令发送时间][发送人][接收人]
 *              [FLOWER][12343234123][you][ALL]
 */
public enum IMP {
    /**
     * 系统消息
     */
    SYSTEM("SYSTEM"),
    /**
     * 登录指令
     */
    LOGIN("LOGIN"),
    /**
     * 退出指令
     */
    LOGOUT("LOGOUT"),
    /**
     * 聊天消息
     */
    CHAT("CHAT"),
    /**
     * 送鲜花
     */
    FLOWER("FLOWER");

    private String name;

    public static boolean isIMP(String content) {
        return content.matches("^\\[(SYSTEM|LOGIN|LOGOUT|CHAT)\\]");
    }

    IMP(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }
}
