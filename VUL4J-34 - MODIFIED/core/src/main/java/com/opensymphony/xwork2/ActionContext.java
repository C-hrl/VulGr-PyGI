
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



public class ActionContext implements Serializable {

    static ThreadLocal<ActionContext> actionContext = new ThreadLocal<>();

    
    public static final String ACTION_NAME = "com.opensymphony.xwork2.ActionContext.name";

    
    public static final String VALUE_STACK = ValueStack.VALUE_STACK;

    
    public static final String SESSION = "com.opensymphony.xwork2.ActionContext.session";

    
    public static final String APPLICATION = "com.opensymphony.xwork2.ActionContext.application";

    
    public static final String PARAMETERS = "com.opensymphony.xwork2.ActionContext.parameters";

    
    public static final String LOCALE = "com.opensymphony.xwork2.ActionContext.locale";

    
    public static final String TYPE_CONVERTER = "com.opensymphony.xwork2.ActionContext.typeConverter";

    
    public static final String ACTION_INVOCATION = "com.opensymphony.xwork2.ActionContext.actionInvocation";

    
    public static final String CONVERSION_ERRORS = "com.opensymphony.xwork2.ActionContext.conversionErrors";


    
    public static final String CONTAINER = "com.opensymphony.xwork2.ActionContext.container";
    
    private Map<String, Object> context;

    
    public ActionContext(Map<String, Object> context) {
        this.context = context;
    }


    
    public void setActionInvocation(ActionInvocation actionInvocation) {
        put(ACTION_INVOCATION, actionInvocation);
    }

    
    public ActionInvocation getActionInvocation() {
        return (ActionInvocation) get(ACTION_INVOCATION);
    }

    
    public void setApplication(Map<String, Object> application) {
        put(APPLICATION, application);
    }

    
    public Map<String, Object> getApplication() {
        return (Map<String, Object>) get(APPLICATION);
    }

    
    public static void setContext(ActionContext context) {
        actionContext.set(context);
    }

    
    public static ActionContext getContext() {
        return actionContext.get();
    }

    
    public void setContextMap(Map<String, Object> contextMap) {
        getContext().context = contextMap;
    }

    
    public Map<String, Object> getContextMap() {
        return context;
    }

    
    public void setConversionErrors(Map<String, Object> conversionErrors) {
        put(CONVERSION_ERRORS, conversionErrors);
    }

    
    public Map<String, Object> getConversionErrors() {
        Map<String, Object> errors = (Map) get(CONVERSION_ERRORS);

        if (errors == null) {
            errors = new HashMap<>();
            setConversionErrors(errors);
        }

        return errors;
    }

    
    public void setLocale(Locale locale) {
        put(LOCALE, locale);
    }

    
    public Locale getLocale() {
        Locale locale = (Locale) get(LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
            setLocale(locale);
        }

        return locale;
    }

    
    public void setName(String name) {
        put(ACTION_NAME, name);
    }

    
    public String getName() {
        return (String) get(ACTION_NAME);
    }

    
    public void setParameters(Map<String, Object> parameters) {
        put(PARAMETERS, parameters);
    }

    
    public Map<String, Object> getParameters() {
        return (Map<String, Object>) get(PARAMETERS);
    }

    
    public void setSession(Map<String, Object> session) {
        put(SESSION, session);
    }

    
    public Map<String, Object> getSession() {
        return (Map<String, Object>) get(SESSION);
    }

    
    public void setValueStack(ValueStack stack) {
        put(VALUE_STACK, stack);
    }

    
    public ValueStack getValueStack() {
        return (ValueStack) get(VALUE_STACK);
    }
    
    
    public void setContainer(Container cont) {
        put(CONTAINER, cont);
    }
    
    
    public Container getContainer() {
        return (Container) get(CONTAINER);
    }
    
    public <T> T getInstance(Class<T> type) {
        Container cont = getContainer();
        if (cont != null) {
            return cont.getInstance(type);
        } else {
            throw new XWorkException("Cannot find an initialized container for this request.");
        }
    }

    
    public Object get(String key) {
        return context.get(key);
    }

    
    public void put(String key, Object value) {
        context.put(key, value);
    }
}
