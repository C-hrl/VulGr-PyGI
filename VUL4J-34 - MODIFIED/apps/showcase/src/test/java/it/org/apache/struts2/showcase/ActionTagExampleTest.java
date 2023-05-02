
package it.org.apache.struts2.showcase;

public class ActionTagExampleTest extends ITBaseTest {
    public void test() {
        beginAt("/tags/ui/actionTagExample!input.action");
        assertTextPresent("This text is from the called class");
    }

}
