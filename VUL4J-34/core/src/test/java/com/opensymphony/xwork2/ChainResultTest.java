

package com.opensymphony.xwork2;

import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.util.ValueStack;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;



public class ChainResultTest extends XWorkTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        
        XmlConfigurationProvider configurationProvider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(configurationProvider);
        loadConfigurationProviders(configurationProvider);
    }

    public void testNamespaceAndActionExpressionEvaluation() throws Exception {
        ActionChainResult result = new ActionChainResult();
        result.setActionName("${actionName}");
        result.setNamespace("${namespace}");

        String expectedActionName = "testActionName";
        String expectedNamespace = "testNamespace";
        Map<String, Object> values = new HashMap<>();
        values.put("actionName", expectedActionName);
        values.put("namespace", expectedNamespace);

        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(values);

        Mock actionProxyMock = new Mock(ActionProxy.class);
        actionProxyMock.expect("execute");

        ActionProxyFactory testActionProxyFactory = new NamespaceActionNameTestActionProxyFactory(expectedNamespace, expectedActionName, (ActionProxy) actionProxyMock.proxy());
        result.setActionProxyFactory(testActionProxyFactory);
        try {

            ActionContext testContext = new ActionContext(stack.getContext());
            ActionContext.setContext(testContext);
            result.execute(null);
            actionProxyMock.verify();
        } finally {
            ActionContext.setContext(null);
        }
    }

    public void testRecursiveChain() throws Exception {
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "InfiniteRecursionChain", null, null);

        try {
            proxy.execute();
            fail("did not detected repeated chain to an action");
        } catch (XWorkException e) {
            assertTrue(true);
        }
    }

    private class NamespaceActionNameTestActionProxyFactory implements ActionProxyFactory {
        private ActionProxy returnVal;
        private String expectedActionName;
        private String expectedNamespace;

        public NamespaceActionNameTestActionProxyFactory(String expectedNamespace, String expectedActionName, ActionProxy returnVal) {
            this.expectedNamespace = expectedNamespace;
            this.expectedActionName = expectedActionName;
            this.returnVal = returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) {
             TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(ActionInvocation actionInvocation, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
             TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(String namespace, String actionName, String method, boolean executeResult, boolean cleanupContext) {
            TestCase.assertEquals(expectedNamespace, namespace);
            TestCase.assertEquals(expectedActionName, actionName);

            return returnVal;
        }

        public ActionProxy createActionProxy(ActionInvocation inv, String namespace, String actionName,
                Map<String, Object> extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
            return null;
        }
    }
}
