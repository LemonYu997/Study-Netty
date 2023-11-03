package example.netty.demo.rcp.registry;

import example.netty.demo.rcp.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务注册和服务调用的具体逻辑
 * 因为这里代码都在一个模块中，没有跨服务，所以不采用远程调用，而是本地扫描Class的方式实现
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
    //保存所有可用的服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>();
    //保存所有相关的服务类
    private List<String> classNames = new ArrayList<>();

    public RegistryHandler() {
        //实现递归扫描
        scannerClass("example.netty.demo.rcp.provider");
        doRegister();
    }

    //服务方法调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;

        //当客户端建立连接时，需要从自定义协议中获取信息，以及具体的服务和实参
        //使用反射调用
        if (registryMap.containsKey(request.getClassName())) {
            //根据传参的类名、方法名、参数来调取另一个服务（类）的方法
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParames());
            result = method.invoke(clazz, request.getValues());
        }

        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 递归扫描
     */
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            //如果是个文件夹，继续递归
            if (file.isDirectory()) {
                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    /**
     * 完成注册
     */
    private void doRegister() {
        if (classNames.size() == 0) {
            return;
        }

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

