
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.Assert;



public class NestedAction implements Action {

    private String nestedProperty = ActionNestingTest.NESTED_VALUE;


    public NestedAction() {
    }


    public String getNestedProperty() {
        return nestedProperty;
    }

    public String execute() throws Exception {
        Assert.fail();

        return null;
    }

    public String noStack() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        
        Assert.assertEquals(2, stack.size());
        Assert.assertNull(stack.findValue(ActionNestingTest.KEY));
        Assert.assertEquals(ActionNestingTest.NESTED_VALUE, stack.findValue(ActionNestingTest.NESTED_KEY));

        return SUCCESS;
    }

    public String stack() {
        ValueStack stack = ActionContext.getContext().getValueStack();
        
        Assert.assertEquals(3, stack.size());
        Assert.assertNotNull(stack.findValue(ActionNestingTest.KEY));
        Assert.assertEquals(ActionContext.getContext().getValueStack().findValue(ActionNestingTest.KEY), ActionNestingTest.VALUE);
        Assert.assertEquals(ActionNestingTest.NESTED_VALUE, stack.findValue(ActionNestingTest.NESTED_KEY));

        return SUCCESS;
    }
}
