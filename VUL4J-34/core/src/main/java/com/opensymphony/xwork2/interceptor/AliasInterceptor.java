

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;



public class AliasInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(AliasInterceptor.class);

    private static final String DEFAULT_ALIAS_KEY = "aliases";
    protected String aliasesKey = DEFAULT_ALIAS_KEY;

    protected ValueStackFactory valueStackFactory;
    static boolean devMode = false;

    @Inject(XWorkConstants.DEV_MODE)
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }   

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    
    public void setAliasesKey(String aliasesKey) {
        this.aliasesKey = aliasesKey;
    }

    @Override public String intercept(ActionInvocation invocation) throws Exception {

        ActionConfig config = invocation.getProxy().getConfig();
        ActionContext ac = invocation.getInvocationContext();
        Object action = invocation.getAction();

        
        final Map<String, String> parameters = config.getParams();

        if (parameters.containsKey(aliasesKey)) {

            String aliasExpression = parameters.get(aliasesKey);
            ValueStack stack = ac.getValueStack();
            Object obj = stack.findValue(aliasExpression);

            if (obj != null && obj instanceof Map) {
                
                ValueStack newStack = valueStackFactory.createValueStack(stack);
                boolean clearableStack = newStack instanceof ClearableValueStack;
                if (clearableStack) {
                    
                    
                    ((ClearableValueStack)newStack).clearContextValues();
                    Map<String, Object> context = newStack.getContext();
                    ReflectionContextState.setCreatingNullObjects(context, true);
                    ReflectionContextState.setDenyMethodExecution(context, true);
                    ReflectionContextState.setReportingConversionErrors(context, true);

                    
                    context.put(ActionContext.LOCALE, stack.getContext().get(ActionContext.LOCALE));
                }

                
                Map aliases = (Map) obj;
                for (Object o : aliases.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    String name = entry.getKey().toString();
                    String alias = (String) entry.getValue();
                    Object value = stack.findValue(name);
                    if (null == value) {
                        
                        Map<String, Object> contextParameters = ActionContext.getContext().getParameters();

                        if (null != contextParameters) {
                            value = contextParameters.get(name);
                        }
                    }
                    if (null != value) {
                        try {
                            newStack.setValue(alias, value);
                        } catch (RuntimeException e) {
                            if (devMode) {
                                String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class, "devmode.notification", ActionContext.getContext().getLocale(), "Developer Notification:\n{0}", new Object[]{
                                        "Unexpected Exception caught setting '" + entry.getKey() + "' on '" + action.getClass() + ": " + e.getMessage()
                                });
                                LOG.error(developerNotification);
                                if (action instanceof ValidationAware) {
                                    ((ValidationAware) action).addActionMessage(developerNotification);
                                }
                            }
                        }
                    }
                }

                if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
                    stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));
            } else {
                LOG.debug("invalid alias expression: {}", aliasesKey);
            }
        }
        
        return invocation.invoke();
    }
    
}
