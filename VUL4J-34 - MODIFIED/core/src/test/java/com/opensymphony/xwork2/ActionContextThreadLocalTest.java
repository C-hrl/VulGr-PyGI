
package com.opensymphony.xwork2;

import junit.framework.TestCase;

import java.util.HashMap;



public class ActionContextThreadLocalTest extends TestCase {

	
	public void testGetContext() throws Exception {
	    ActionContext.setContext(null);
		assertNull(ActionContext.getContext());
	}
	
	public void testSetContext() throws Exception {
		ActionContext context = new ActionContext(new HashMap<String, Object>());
		ActionContext.setContext(context);
		assertEquals(context, ActionContext.getContext());
	}
}
