package example.netty.demo.rcp.provider;

import example.netty.demo.rcp.api.IRpcHelloService;

public class RpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "hello " + name + "!";
    }
}
