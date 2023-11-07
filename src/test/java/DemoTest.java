import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DemoTest {
    @Test
    public void test1() {
        Pattern pattern = Pattern.compile("^\\[(.*)](\\s-\\s(.*))?");
        String msg = "[SYSTEM] [12343234123] [Tom] - Student加入聊天室";
        Matcher m = pattern.matcher(msg);

        int n = m.groupCount();
        System.out.println(n);

        if (m.matches()) {
            String[] headers = m.group(1).split("]\\[");

            for (String header : headers) {
                System.out.println(header);
            }
        }



    }
}
