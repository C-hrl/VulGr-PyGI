
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;

import java.util.HashMap;


public class ExceptionMappingInterceptorTest extends XWorkTestCase {

    ActionInvocation invocation;
    ExceptionMappingInterceptor interceptor;
    Mock mockInvocation;
    ValueStack stack;


    public void testThrownExceptionMatching() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new XWorkException("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertNotNull(stack.findValue("exception"));
        assertEquals(stack.findValue("exception"), exception);
        assertEquals(result, "spooky");
        ExceptionHolder holder = (ExceptionHolder) stack.getRoot().get(0); 
        assertNotNull(holder.getExceptionStack()); 
    }

    public void testThrownExceptionMatching2() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new ValidationException("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertNotNull(stack.findValue("exception"));
        assertEquals(stack.findValue("exception"), exception);
        assertEquals(result, "throwable");
    }

    public void testNoThrownException() throws Exception {
        this.setUpWithExceptionMappings();

        Mock action = new Mock(Action.class);
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));
        String result = interceptor.intercept(invocation);
        assertEquals(result, Action.SUCCESS);
        assertNull(stack.findValue("exception"));
    }

    public void testThrownExceptionNoMatch() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLogging() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategory() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelFatal() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("fatal");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
        
        assertEquals("fatal", interceptor.getLogLevel());
        assertEquals(true, interceptor.isLogEnabled());
        assertEquals("showcase.unhandled", interceptor.getLogCategory());
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelError() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("error");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelWarn() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("warn");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelInfo() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("info");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelDebug() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("debug");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingCategoryLevelTrace() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogCategory("showcase.unhandled");
        	interceptor.setLogLevel("trace");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (Exception e) {
            assertEquals(e, exception);
        }
    }

    public void testThrownExceptionNoMatchLoggingUnknownLevel() throws Exception {
        this.setupWithoutExceptionMappings();

        Mock action = new Mock(Action.class);
        Exception exception = new Exception("test");
        mockInvocation.expectAndThrow("invoke", exception);
        mockInvocation.matchAndReturn("getAction", ((Action) action.proxy()));

        try {
        	interceptor.setLogEnabled(true);
        	interceptor.setLogLevel("xxx");
            interceptor.intercept(invocation);
            fail("Should not have reached this point.");
        } catch (IllegalArgumentException e) {
        	
        }
    }

    private void setupWithoutExceptionMappings() {
        ActionConfig actionConfig = new ActionConfig.Builder("", "", "").build();
        Mock actionProxy = new Mock(ActionProxy.class);
        actionProxy.expectAndReturn("getConfig", actionConfig);
        mockInvocation.expectAndReturn("getProxy", ((ActionProxy) actionProxy.proxy()));
        invocation = (ActionInvocation) mockInvocation.proxy();
    }

    private void setUpWithExceptionMappings() {
        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addExceptionMapping(new ExceptionMappingConfig.Builder("xwork", "com.opensymphony.xwork2.XWorkException", "spooky").build())
                .addExceptionMapping(new ExceptionMappingConfig.Builder("throwable", "java.lang.Throwable", "throwable").build())
                .build();
        Mock actionProxy = new Mock(ActionProxy.class);
        actionProxy.expectAndReturn("getConfig", actionConfig);
        mockInvocation.expectAndReturn("getProxy", ((ActionProxy) actionProxy.proxy()));

        invocation = (ActionInvocation) mockInvocation.proxy();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.expectAndReturn("getStack", stack);
        mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(new HashMap<String, Object>()));
        interceptor = new ExceptionMappingInterceptor();
        interceptor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        interceptor.destroy();
        invocation = null;
        interceptor = null;
        mockInvocation = null;
        stack = null;
    }

}
