package example.netty.demo.rcp.api;

/**
 * 确认服务是否可用接口
 */
public interface IRpcHelloService {
    String hello(String name);
}
