package example.netty.demo.tomcat.io;

/**
 * 实现tomcat中servlet的功能
 *
 */
public abstract class GPServlet {
    public void service(GPRequest request, GPResponse response) throws Exception {
        //由service()方法决定是调用doGet()还是调用doPost()
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    public abstract void doPost(GPRequest request, GPResponse response) throws Exception;

    public abstract void doGet(GPRequest request, GPResponse response) throws Exception;
}
