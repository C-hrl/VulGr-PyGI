
package com.opensymphony.xwork2.interceptor;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.*;



public class ChainingInterceptorTest extends XWorkTestCase {

    ActionInvocation invocation;
    ChainingInterceptor interceptor;
    Mock mockInvocation;
    ValueStack stack;


    public void testActionErrorsCanBeAddedAfterChain() throws Exception {
        SimpleAction action1 = new SimpleAction();
        SimpleAction action2 = new SimpleAction();
        action1.addActionError("foo");
        mockInvocation.matchAndReturn("getAction", action2);
        stack.push(action1);
        stack.push(action2);
        interceptor.setCopyErrors("true");
        interceptor.setCopyMessages("true");

        interceptor.intercept(invocation);

        assertEquals(action1.getActionErrors(), action2.getActionErrors());
        action2.addActionError("bar");
        assertEquals(1, action1.getActionErrors().size());
        assertEquals(2, action2.getActionErrors().size());
        assertTrue(action2.getActionErrors().contains("bar"));
    }

    public void testActionErrorsNotCopiedAfterChain() throws Exception {
        SimpleAction action1 = new SimpleAction();
        SimpleAction action2 = new SimpleAction();
        action1.addActionError("foo");
        mockInvocation.matchAndReturn("getAction", action2);
        stack.push(action1);
        stack.push(action2);

        interceptor.intercept(invocation);

        assertEquals(Collections.EMPTY_LIST, action2.getActionErrors());
        action2.addActionError("bar");
        assertEquals(1, action1.getActionErrors().size());
        assertEquals(1, action2.getActionErrors().size());
        assertTrue(action2.getActionErrors().contains("bar"));
        assertFalse(action2.getActionErrors().contains("foo"));
    }

    public void testPropertiesChained() throws Exception {
        TestBean bean = new TestBean();
        TestBeanAction action = new TestBeanAction();
        mockInvocation.matchAndReturn("getAction", action);
        bean.setBirth(new Date());
        bean.setName("foo");
        bean.setCount(1);
        stack.push(bean);
        stack.push(action);
        interceptor.setCopyErrors("true");
        interceptor.setCopyMessages("true");

        interceptor.intercept(invocation);

        assertEquals(bean.getBirth(), action.getBirth());
        assertEquals(bean.getName(), action.getName());
        assertEquals(bean.getCount(), action.getCount());
    }

    public void testExcludesPropertiesChained() throws Exception {
        TestBean bean = new TestBean();
        TestBeanAction action = new TestBeanAction();
        mockInvocation.matchAndReturn("getAction", action);
        bean.setBirth(new Date());
        bean.setName("foo");
        bean.setCount(1);
        stack.push(bean);
        stack.push(action);
        interceptor.setCopyErrors("true");
        interceptor.setCopyMessages("true");

        Collection<String> excludes = new ArrayList<>();
        excludes.add("count");
        interceptor.setExcludes(excludes);

        interceptor.intercept(invocation);

        assertEquals(bean.getBirth(), action.getBirth());
        assertEquals(bean.getName(), action.getName());
        assertEquals(0, action.getCount());
        assertEquals(excludes, interceptor.getExcludes());
    }

    public void testTwoExcludesPropertiesChained() throws Exception {
        TestBean bean = new TestBean();
        TestBeanAction action = new TestBeanAction();
        mockInvocation.matchAndReturn("getAction", action);
        bean.setBirth(new Date());
        bean.setName("foo");
        bean.setCount(1);
        stack.push(bean);
        stack.push(action);

        Collection<String> excludes = new ArrayList<>();
        excludes.add("name");
        excludes.add("count");
        interceptor.setExcludes(excludes);
        interceptor.intercept(invocation);
        assertEquals(bean.getBirth(), action.getBirth());
        assertEquals(null, action.getName());
        assertEquals(0, action.getCount());
        assertEquals(excludes, interceptor.getExcludes());
    }

    public void testNullCompoundRootElementAllowsProcessToContinue() throws Exception {
        
        stack.push(null);
        stack.push(null);
        stack.push(null);
        interceptor.intercept(invocation);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        stack = ActionContext.getContext().getValueStack();
        mockInvocation = new Mock(ActionInvocation.class);
        mockInvocation.expectAndReturn("getStack", stack);
        mockInvocation.expectAndReturn("invoke", Action.SUCCESS);
        mockInvocation.expectAndReturn("getInvocationContext", new ActionContext(new HashMap<String, Object>()));
        mockInvocation.expectAndReturn("getResult", new ActionChainResult());
        invocation = (ActionInvocation) mockInvocation.proxy();
        interceptor = new ChainingInterceptor();
        container.inject(interceptor);
    }


    private class TestBeanAction extends TestBean implements Action {
        public String execute() throws Exception {
            return SUCCESS;
        }
    }
}
