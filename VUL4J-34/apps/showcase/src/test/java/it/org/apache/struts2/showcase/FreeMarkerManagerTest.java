
package it.org.apache.struts2.showcase;

import java.net.MalformedURLException;
import java.net.URL;

public class FreeMarkerManagerTest extends ITBaseTest {
    public void testCustomManager() {
        beginAt("/freemarker/customFreemarkerManagerDemo.action");

        String date = getElementTextByXPath("
        assertNotNull(date);
        assertTrue(date.length() > 0);

        String time = getElementTextByXPath("
        assertNotNull(time);
        assertTrue(time.length() > 0);
    }

    public void testTags() {
        beginAt("/freemarker/standardTags.action");
        assertElementPresent("test_name");
        assertElementPresent("test_");
    }
}
