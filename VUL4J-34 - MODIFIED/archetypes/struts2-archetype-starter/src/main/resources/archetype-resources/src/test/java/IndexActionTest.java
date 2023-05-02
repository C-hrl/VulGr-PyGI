
package ${package};

import junit.framework.TestCase;

import com.opensymphony.xwork2.Action;


public class IndexActionTest extends TestCase {
    
    public void testIndexAction() throws Exception {
        IndexAction action = new IndexAction();
        String result = action.execute();
        assertEquals(Action.SUCCESS, result);
    }
}
