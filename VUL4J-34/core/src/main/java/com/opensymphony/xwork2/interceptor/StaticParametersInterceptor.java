
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.Parameterizable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;



public class StaticParametersInterceptor extends AbstractInterceptor {

    private boolean parse;
    private boolean overwrite;
    private boolean merge = true;
    private boolean devMode = false;

    private static final Logger LOG = LogManager.getLogger(StaticParametersInterceptor.class);

    private ValueStackFactory valueStackFactory;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        devMode = BooleanUtils.toBoolean(mode);
    }    

    public void setParse(String value) {
        this.parse = BooleanUtils.toBoolean(value);
    }

     public void setMerge(String value) {
         this.merge = BooleanUtils.toBoolean(value);
    }

    
    public void setOverwrite(String value) {
        this.overwrite = BooleanUtils.toBoolean(value);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionConfig config = invocation.getProxy().getConfig();
        Object action = invocation.getAction();

        final Map<String, String> parameters = config.getParams();

        LOG.debug("Setting static parameters: {}", parameters);

        
        if (action instanceof Parameterizable) {
            ((Parameterizable) action).setParams(parameters);
        }

        if (parameters != null) {
            ActionContext ac = ActionContext.getContext();
            Map<String, Object> contextMap = ac.getContextMap();
            try {
                ReflectionContextState.setCreatingNullObjects(contextMap, true);
                ReflectionContextState.setReportingConversionErrors(contextMap, true);
                final ValueStack stack = ac.getValueStack();

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

                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    Object val = entry.getValue();
                    if (parse && val instanceof String) {
                        val = TextParseUtil.translateVariables(val.toString(), stack);
                    }
                    try {
                        newStack.setValue(entry.getKey(), val);
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

                 if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
                    stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));

                if (merge)
                    addParametersToContext(ac, parameters);
            } finally {
                ReflectionContextState.setCreatingNullObjects(contextMap, false);
                ReflectionContextState.setReportingConversionErrors(contextMap, false);
            }
        }
        return invocation.invoke();
    }


    
    protected Map<String, String> retrieveParameters(ActionContext ac) {
        ActionConfig config = ac.getActionInvocation().getProxy().getConfig();
        if (config != null) {
            return config.getParams();
        } else {
            return Collections.emptyMap();
        }
    }

    
    protected void addParametersToContext(ActionContext ac, Map<String, ?> newParams) {
        Map<String, Object> previousParams = ac.getParameters();

        Map<String, Object> combinedParams;
        if ( overwrite ) {
            if (previousParams != null) {
                combinedParams = new TreeMap<>(previousParams);
            } else {
                combinedParams = new TreeMap<>();
            }
            if ( newParams != null) {
                combinedParams.putAll(newParams);
            }
        } else {
            if (newParams != null) {
                combinedParams = new TreeMap<>(newParams);
            } else {
                combinedParams = new TreeMap<>();
            }
            if ( previousParams != null) {
                combinedParams.putAll(previousParams);
            }
        }
        ac.setParameters(combinedParams);
    }
}
