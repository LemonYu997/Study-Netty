package example.netty.demo.rcp.api;

/**
 * 实际业务处理接口
 */
public interface IRpcService {
    /**
     * 加法
     */
    public int add(int a, int b);

    /**
     * 减法
     */
    public int sub(int a, int b);

    /**
     * 乘法
     */
    public int mult(int a, int b);

    /**
     * 除法
     */
    public int div(int a, int b);
}
