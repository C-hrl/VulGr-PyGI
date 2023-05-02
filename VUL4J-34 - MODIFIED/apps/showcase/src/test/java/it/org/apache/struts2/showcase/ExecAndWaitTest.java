
package it.org.apache.struts2.showcase;

public class ExecAndWaitTest extends ITBaseTest {
    public void testNodelay() throws InterruptedException {
        beginAt("/wait/example1.jsp");

        setTextField("time", "7000");
        submit();
        assertTextPresent("We are processing your request. Please wait.");

        
        beginAt("/wait/longProcess1.action?time=1000");
        assertTextPresent("We are processing your request. Please wait.");
    }
}
