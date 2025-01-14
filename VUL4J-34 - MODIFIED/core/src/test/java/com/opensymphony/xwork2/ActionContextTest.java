
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

import java.util.HashMap;
import java.util.Map;



public class ActionContextTest extends XWorkTestCase {

    private static final String APPLICATION_KEY = "com.opensymphony.xwork2.ActionContextTest.application";
    private static final String SESSION_KEY = "com.opensymphony.xwork2.ActionContextTest.session";
    private static final String PARAMETERS_KEY = "com.opensymphony.xwork2.ActionContextTest.params";
    private static final String ACTION_NAME = "com.opensymphony.xwork2.ActionContextTest.actionName";

    private ActionContext context;

    @Override public void setUp() throws Exception {
        super.setUp();
        ValueStack valueStack = container.getInstance(ValueStackFactory.class).createValueStack();
        Map<String, Object> extraContext = valueStack.getContext();
        Map<String, Object> application = new HashMap<>();
        application.put(APPLICATION_KEY, APPLICATION_KEY);

        Map<String, Object> session = new HashMap<>();
        session.put(SESSION_KEY, SESSION_KEY);

        Map<String, Object> params = new HashMap<>();
        params.put(PARAMETERS_KEY, PARAMETERS_KEY);
        extraContext.put(ActionContext.APPLICATION, application);
        extraContext.put(ActionContext.SESSION, session);
        extraContext.put(ActionContext.PARAMETERS, params);
        extraContext.put(ActionContext.ACTION_NAME, ACTION_NAME);
        context = new ActionContext(extraContext);
        ActionContext.setContext(context);
    }

    public void testContextParams() {
        assertTrue(ActionContext.getContext().getApplication().containsKey(APPLICATION_KEY));
        assertTrue(ActionContext.getContext().getSession().containsKey(SESSION_KEY));
        assertTrue(ActionContext.getContext().getParameters().containsKey(PARAMETERS_KEY));
        assertEquals(ActionContext.getContext().getName(), ACTION_NAME);
    }

    public void testGetContext() {
        ActionContext threadContext = ActionContext.getContext();
        assertEquals(context, threadContext);
    }

    public void testNewActionContextCanFindDefaultTexts() {
        ValueStack valueStack = context.getValueStack();
        String actionErrorMessage = (String) valueStack.findValue("getText('xwork.error.action.execution')");
        assertNotNull(actionErrorMessage);
        assertEquals("Error during Action invocation", actionErrorMessage);
    }

    public void testApplication() {
        Map<String, Object> app = new HashMap<>();
        context.setApplication(app);
        assertEquals(app, context.getApplication());
    }

    public void testContextMap() {
        Map<String, Object> map = new HashMap<>();
        context.setContextMap(map);
        assertEquals(map, context.getContextMap());
    }

    public void testParameters() {
        Map<String, Object> param = new HashMap<>();
        context.setParameters(param);
        assertEquals(param, context.getParameters());
    }

    public void testConversionErrors() {
        Map<String, Object> errors = context.getConversionErrors();
        assertNotNull(errors);
        assertEquals(0, errors.size());

        Map<String, Object> errors2 = new HashMap<>();
        context.setConversionErrors(errors);
        assertEquals(errors2, context.getConversionErrors());
    }

    public void testStaticMethods() {
        assertEquals(context, ActionContext.getContext());

        ActionContext context2 = new ActionContext(null);
        ActionContext.setContext(context2);

        assertEquals(context2, ActionContext.getContext());
    }

}
