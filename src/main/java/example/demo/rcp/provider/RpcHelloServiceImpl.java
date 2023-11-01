package example.demo.rcp.provider;

import example.demo.rcp.api.IRpcHelloService;

public class RpcHelloServiceImpl implements IRpcHelloService {
    @Override
    public String hello(String name) {
        return "hello " + name + "!";
    }
}
