package example.demo.rcp.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义传输协议
 */
@Data
public class InvokerProtocol implements Serializable {
    //类名
    private String className;

    //方法名
    private String methodName;

    //参数类型
    private Class<?>[] parames;

    //参数列表
    private Object[] values;
}
