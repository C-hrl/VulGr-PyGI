
package it.org.apache.struts2.showcase;

public class ActionChainingTest extends ITBaseTest {
    public void test() {
        beginAt("/actionchaining/actionChain1!input");
        assertTextPresent("Action Chain 1 Property 1: Property Set In Action Chain 1");
        assertTextPresent("Action Chain 2 Property 1: Property Set in Action Chain 2");
        assertTextPresent("Action Chain 3 Property 1: Property set in Action Chain 3");
    }
}
