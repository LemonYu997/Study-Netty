package example.demo.tomcat.io;

import java.io.InputStream;

/**
 * 对HTTP请求头信息进行解析
 */
public class GPRequest {
    private String method;
    private String url;

    public GPRequest(InputStream in) {
        try {
            //获取HTTP内容
            String content = "";
            byte[] buff = new byte[1024];
            int len = 0;
            if ((len = in.read(buff)) > 0) {
                content = new String(buff, 0, len);
            }

            String line = content.split("\\n")[0];
            String[] arr = line.split("\\s");

            //获取请求方式和URL
            this.method = arr[0];
            this.url = arr[1].split("\\?")[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }
}
