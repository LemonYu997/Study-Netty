package example.demo.tomcat.netty;

import example.demo.tomcat.netty.GPRequest;
import example.demo.tomcat.netty.GPResponse;

/**
 * 用户业务代码1
 */
public class FirstServlet extends GPServlet {
    @Override
    public void doPost(GPRequest request, GPResponse response) throws Exception {
        response.write("This is First Servlet");
    }

    @Override
    public void doGet(GPRequest request, GPResponse response) throws Exception {
        doPost(request, response);
    }
}
