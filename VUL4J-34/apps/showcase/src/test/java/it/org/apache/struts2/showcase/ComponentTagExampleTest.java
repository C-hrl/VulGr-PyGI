
package it.org.apache.struts2.showcase;

public class ComponentTagExampleTest extends ITBaseTest {
    public void test() {
        beginAt("/tags/ui/componentTagExample.jsp");
        assertTextPresent("Freemarker Custom Template - parameter 'paramName' - paramValue1");
        assertTextPresent("Freemarker Custom Template - parameter 'paramName' - paramValue4");
        assertTextPresent("JSP Custom Template - parameter 'paramName' - paramValue2");
        assertTextPresent("JSP Custom Template - parameter 'paramName' - paramValue3");
    }
}
