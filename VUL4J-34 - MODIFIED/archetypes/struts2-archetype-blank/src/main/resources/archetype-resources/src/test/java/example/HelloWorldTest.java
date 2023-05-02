

package ${package}.example;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.StrutsTestCase;

public class HelloWorldTest extends StrutsTestCase {

    public void testHelloWorld() throws Exception {
        HelloWorld hello_world = new HelloWorld();
        String result = hello_world.execute();
        assertTrue("Expected a success result!",
                ActionSupport.SUCCESS.equals(result));
        assertTrue("Expected the default message!",
                hello_world.getText(HelloWorld.MESSAGE).equals(hello_world.getMessage()));
    }
}
