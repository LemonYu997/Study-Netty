package example.effective.tools;

import io.netty.util.concurrent.FastThreadLocal;

/**
 * Netty中的FastThreadLocal性能要高于JDK的ThreadLocal
 */
public class FastThreadLocalDemo {
    final class FastThreadLocalTest extends FastThreadLocal<Object> {
        //初始化线程共享对象
        @Override
        protected Object initialValue() throws Exception {
            return new Object();
        }
    }

    //成员属性
    private final FastThreadLocalTest fastThreadLocalTest;

    //构造方法
    public FastThreadLocalDemo() {
        fastThreadLocalTest = new FastThreadLocalTest();
    }

    //启动两个线程
    public static void main(String[] args) {
        FastThreadLocalDemo demo = new FastThreadLocalDemo();

        //遍历修改共享对象的值
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object obj = demo.fastThreadLocalTest.get();
                try {
                    for (int i = 0; i < 10; i++) {
                        demo.fastThreadLocalTest.set(new Object());
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //遍历获取比较
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Object obj = demo.fastThreadLocalTest.get();
                    for (int i = 0; i < 10; i++) {
                        //全为true 说明当前线程获取的是ThreadLocal的值，而不用在意其他线程修改
                        System.out.println(obj == demo.fastThreadLocalTest.get());
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
