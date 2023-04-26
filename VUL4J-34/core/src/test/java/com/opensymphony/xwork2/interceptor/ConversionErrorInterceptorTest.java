
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.HashMap;
import java.util.Map;



public class ConversionErrorInterceptorTest extends XWorkTestCase {

    protected ActionContext context;
    protected ActionInvocation invocation;
    protected ConversionErrorInterceptor interceptor;
    protected Map<String, Object> conversionErrors;
    protected Mock mockInvocation;
    protected ValueStack stack;


    public void testFieldErrorAdded() throws Exception {
        conversionErrors.put("foo", 123L);

        SimpleAction action = new SimpleAction();
        mockInvocation.expectAndReturn("getAction", action);
        stack.push(action);
        mockInvocation.matchAndReturn("getAction", action);
        assertNull(action.getFieldErrors().get("foo"));
        interceptor.intercept(invocation);
        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));
    }

    public void testFieldErrorWithMapKeyAdded() throws Exception {
        String fieldName = "foo['1'].intValue";
        conversionErrors.put(fieldName, "bar");
        ActionSupport action = new ActionSupport();
        mockInvocation.expectAndReturn("getAction", action);
        stack.push(action);
        mockInvocation.matchAndReturn("getAction", action);
        assertNull(action.getFieldErrors().get(fieldName));
        interceptor.intercept(invocation);
        assertTrue(action.hasFieldErrors()); 
        assertNotNull(action.getFieldErrors().get(fieldName));
    }

    public void testWithPreResultListener() throws Exception {
        conversionErrors.put("foo", "Hello");

        ActionContext ac = createActionContext();
        MockActionInvocation mai = createActionInvocation(ac);
        SimpleAction action = createAction(mai);

        assertNull(action.getFieldErrors().get("foo"));
        assertEquals(55, stack.findValue("foo"));

        interceptor.intercept(mai);

        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));

        assertEquals("Hello", stack.findValue("foo")); 
    }

    
    public void testWithPreResultListenerAgainstMaliciousCode() throws Exception {
        conversionErrors.put("foo", "\" + #root + \"");

        ActionContext ac = createActionContext();

        MockActionInvocation mai = createActionInvocation(ac);

        SimpleAction action = createAction(mai);
        assertNull(action.getFieldErrors().get("foo"));
        assertEquals(55, stack.findValue("foo"));

        interceptor.intercept(mai);

        assertTrue(action.hasFieldErrors());
        assertNotNull(action.getFieldErrors().get("foo"));

        assertEquals("\" + #root + \"", stack.findValue("foo"));
    }

    private MockActionInvocation createActionInvocation(ActionContext ac) {
        MockActionInvocation mai = new MockActionInvocation();
        mai.setInvocationContext(ac);
        mai.setStack(stack);
        return mai;
    }

    private SimpleAction createAction(MockActionInvocation mai) {
        SimpleAction action = new SimpleAction();
        action.setFoo(55);
        mai.setAction(action);
        stack.push(action);
        return action;
    }

    private ActionContext createActionContext() {
        ActionContext ac = new ActionContext(stack.getContext());
        ac.setConversionErrors(conversionErrors);
        ac.setValueStack(stack);
        return ac;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        interceptor = new ConversionErrorInterceptor();
        mockInvocation = new Mock(ActionInvocation.class);
        invocation = (ActionInvocation) mockInvocation.proxy();
        stack = ActionContext.getContext().getValueStack();
        context = new ActionContext(stack.getContext());
        conversionErrors = new HashMap<>();
        context.setConversionErrors(conversionErrors);
        mockInvocation.matchAndReturn("getInvocationContext", context);
        mockInvocation.expect("addPreResultListener", C.isA(PreResultListener.class));
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
    }
}
