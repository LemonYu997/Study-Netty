package example.demo.tomcat.io;

/**
 * 用户业务代码2
 */
public class SecondServlet extends GPServlet {
    @Override
    public void doPost(GPRequest request, GPResponse response) throws Exception {
        response.write("This is Second Servlet");
    }

    @Override
    public void doGet(GPRequest request, GPResponse response) throws Exception {
        doPost(request, response);
    }
}
