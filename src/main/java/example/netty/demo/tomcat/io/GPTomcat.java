package example.netty.demo.tomcat.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 传统IO模式实现Tomcat容器
 */
public class GPTomcat {
    private int port = 8080;
    private ServerSocket server;
    private Map<String, GPServlet> servletMapping = new HashMap<>();
    private Properties webxml = new Properties();

    //初始化
    private void init() {
        //加载web.properties文件，同时初始化ServletMapping对象
        try {
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");

            webxml.load(fis);

            //解析url和className
            for (Object k : webxml.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webxml.getProperty(key);
                    String className = webxml.getProperty(servletName + ".className");

                    //单实例，多线程
                    GPServlet obj = (GPServlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //服务就绪阶段
    public void start() {
        //1、加载配置文件，初始化ServletMapping
        init();

        try {
            server = new ServerSocket(this.port);
            System.out.println("GPTomcat已启动，监听的端口是：" + this.port);

            //2、等待用户请求，用一个死循环
            while (true) {
                Socket client = server.accept();
                //3、HTTP请求，发送的数据就是字符串——有规律的字符串（HTTP）
                process(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接受请求阶段，完成请求处理
     */
    private void process(Socket client) throws Exception {
        InputStream is = client.getInputStream();
        OutputStream os = client.getOutputStream();

        //4、得到请求和响应
        GPRequest request = new GPRequest(is);
        GPResponse response = new GPResponse(os);

        //5、从协议中获取URL，把相应的Servlet用反射进行实例化
        String url = request.getUrl();

        if (servletMapping.containsKey(url)) {
            //6、调用实例化对象的service()方法，执行具体的逻辑doGet()/doPost()方法
            servletMapping.get(url).service(request, response);
        } else {
            response.write("404 - Not Found");
        }

        os.flush();
        os.close();

        is.close();
        client.close();
    }

    public static void main(String[] args) {
        new GPTomcat().start();
    }
}
