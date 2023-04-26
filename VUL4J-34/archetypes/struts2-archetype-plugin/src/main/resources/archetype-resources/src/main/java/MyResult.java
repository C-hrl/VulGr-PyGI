
package $package;

import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.Writer;



public class MyResult implements Result {

    
    public void execute(ActionInvocation invocation) throws Exception {

        HttpServletResponse response = ServletActionContext.getResponse();
        Writer writer = response.getWriter();
        writer.write("Hello");
        writer.flush();
    }
}
