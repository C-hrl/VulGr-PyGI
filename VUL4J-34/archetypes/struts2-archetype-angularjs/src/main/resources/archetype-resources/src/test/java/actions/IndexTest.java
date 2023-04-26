
package ${package}.actions;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.StrutsRestTestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class IndexTest extends StrutsRestTestCase<Index> {

    @Test
    public void testIndex() throws Exception {
        Index index = new Index();
        String result = index.execute();
        assertTrue("Expected a success result!", ActionSupport.SUCCESS.equals(result));
        assertFalse(index.isUseMinifiedResources());
    }
}

