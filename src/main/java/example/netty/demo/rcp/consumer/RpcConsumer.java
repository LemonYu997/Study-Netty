package example.netty.demo.rcp.consumer;

import example.netty.demo.rcp.api.IRpcHelloService;
import example.netty.demo.rcp.api.IRpcService;

/**
 * 客户端
 */
public class RpcConsumer {
    public static void main(String[] args) {
        IRpcHelloService rpcHello = RpcProxy.create(IRpcHelloService.class);

        System.out.println(rpcHello.hello("Lemon"));

        IRpcService service = RpcProxy.create(IRpcService.class);

        System.out.println("8 + 2 = " + service.add(8, 2));
        System.out.println("8 - 2 = " + service.sub(8, 2));
        System.out.println("8 * 2 = " + service.mult(8 , 2));
        System.out.println("8 / 2 = " + service.div(8,  2));
    }
}
