
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;



public class ConversionErrorInterceptor extends AbstractInterceptor {

    public static final String ORIGINAL_PROPERTY_OVERRIDE = "original.property.override";

    protected Object getOverrideExpr(ActionInvocation invocation, Object value) {
        return escape(value);
    }

    protected String escape(Object value) {
        return "\"" + StringEscapeUtils.escapeJava(String.valueOf(value)) + "\"";
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {

        ActionContext invocationContext = invocation.getInvocationContext();
        Map<String, Object> conversionErrors = invocationContext.getConversionErrors();
        ValueStack stack = invocationContext.getValueStack();

        HashMap<Object, Object> fakie = null;

        for (Map.Entry<String, Object> entry : conversionErrors.entrySet()) {
            String propertyName = entry.getKey();
            Object value = entry.getValue();

            if (shouldAddError(propertyName, value)) {
                String message = XWorkConverter.getConversionErrorMessage(propertyName, stack);

                Object action = invocation.getAction();
                if (action instanceof ValidationAware) {
                    ValidationAware va = (ValidationAware) action;
                    va.addFieldError(propertyName, message);
                }

                if (fakie == null) {
                    fakie = new HashMap<>();
                }

                fakie.put(propertyName, getOverrideExpr(invocation, value));
            }
        }

        if (fakie != null) {
            
            stack.getContext().put(ORIGINAL_PROPERTY_OVERRIDE, fakie);
            invocation.addPreResultListener(new PreResultListener() {
                public void beforeResult(ActionInvocation invocation, String resultCode) {
                    Map<Object, Object> fakie = (Map<Object, Object>) invocation.getInvocationContext().get(ORIGINAL_PROPERTY_OVERRIDE);

                    if (fakie != null) {
                        invocation.getStack().setExprOverrides(fakie);
                    }
                }
            });
        }
        return invocation.invoke();
    }

    protected boolean shouldAddError(String propertyName, Object value) {
        return true;
    }
}
