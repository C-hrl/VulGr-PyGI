
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ParameterFilterInterceptorTest extends XWorkTestCase {

    ActionInvocation invocation;
    ParameterFilterInterceptor interceptor;
    Mock mockInvocation;
    ValueStack stack;
    Map<String, Object> contextMap;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contextMap = new HashMap<>();
        stack = ActionContext.getContext().getValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.expectAndReturn("getStack", stack);
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(contextMap));
        mockInvocation.matchAndReturn("getAction", new SimpleAction());
        invocation = (ActionInvocation) mockInvocation.proxy();
        interceptor = new ParameterFilterInterceptor();
        interceptor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
    }

    public void testBasicBlockAll() throws Exception {
        runFilterTest(null,null,true,new String[] {"blah", "bladeblah", "bladebladeblah"});
        assertEquals(0, getParameterNames().size());
    }
    
    public void testBasicAllowed() throws Exception {
        runFilterTest("blah",null,true,new String[] {"blah"});
        assertEquals(1, getParameterNames().size()); 
    }
    
    public void testBasicBlocked() throws Exception {
        runFilterTest(null,"blah",false,new String[] {"blah"});
        assertEquals(0, getParameterNames().size()); 
    }      
    public void testAllSubpropertiesBlocked() throws Exception {
        runFilterTest(null,"blah",false,new String[] {"blah.deblah", "blah.somethingelse", "blah(22)"});
        assertEquals(0, getParameterNames().size()); 
    }

    public void testAllSubpropertiesAllowed() throws Exception {
        runFilterTest("blah",null,true,
                new String[] {"blah.deblah", "blah.somethingelse", "blah(22)"});
        assertEquals(3, getParameterNames().size()); 
    }
    
    public void testTreeBlocking() throws Exception {
        runFilterTest("blah.deblah","blah,blah.deblah.deblah",false,
                new String[] {"blah", "blah.deblah", "blah.deblah.deblah"});
        Collection paramNames=getParameterNames();
        assertEquals(1, paramNames.size());
        assertEquals(paramNames.iterator().next(),"blah.deblah");
    }
    
    public void testEnsureOnlyPropsBlocked() throws Exception {
        runFilterTest(null,"blah",false,new String[] {"blahdeblah"});
        assertEquals(1, getParameterNames().size()); 
    }
  
    
    private void runFilterTest(String allowed, String blocked, boolean defaultBlocked, String[] paramNames) throws Exception {
        interceptor.setAllowed(allowed);
        interceptor.setBlocked(blocked);
        interceptor.setDefaultBlock(defaultBlocked);
        setUpParameters(paramNames);
        runAction();
        
    }
    
    private void setUpParameters(String [] paramNames) {
        Map<String, String> params = new HashMap<>();
        for (String paramName : paramNames) {
            params.put(paramName, "irrelevant what this is");

        }
        contextMap.put(ActionContext.PARAMETERS, params);
    }
    
    private Collection getParameterNames() {
        return ((Map)contextMap.get(ActionContext.PARAMETERS)).keySet();
    }
    
    public void runAction() throws Exception  {
        interceptor.intercept(invocation);
    }
    
}
