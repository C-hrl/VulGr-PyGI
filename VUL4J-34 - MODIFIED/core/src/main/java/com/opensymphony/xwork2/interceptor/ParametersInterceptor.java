
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.conversion.impl.InstantiatingNullHandler;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


public class ParametersInterceptor extends MethodFilterInterceptor {

    private static final Logger LOG = LogManager.getLogger(ParametersInterceptor.class);

    protected static final int PARAM_NAME_MAX_LENGTH = 100;

    private int paramNameMaxLength = PARAM_NAME_MAX_LENGTH;
    private boolean devMode = false;

    protected boolean ordered = false;

    private ValueStackFactory valueStackFactory;
    private ExcludedPatternsChecker excludedPatterns;
    private AcceptedPatternsChecker acceptedPatterns;

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject
    public void setExcludedPatterns(ExcludedPatternsChecker excludedPatterns) {
        this.excludedPatterns = excludedPatterns;
    }

    @Inject
    public void setAcceptedPatterns(AcceptedPatternsChecker acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    
    public void setParamNameMaxLength(int paramNameMaxLength) {
        this.paramNameMaxLength = paramNameMaxLength;
    }

    static private int countOGNLCharacters(String s) {
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            char c = s.charAt(i);
            if (c == '.' || c == '[') count++;
        }
        return count;
    }

    
    static final Comparator<String> rbCollator = new Comparator<String>() {
        public int compare(String s1, String s2) {
            int l1 = countOGNLCharacters(s1),
                l2 = countOGNLCharacters(s2);
            return l1 < l2 ? -1 : (l2 < l1 ? 1 : s1.compareTo(s2));
        }

    };

    @Override
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        if (!(action instanceof NoParameters)) {
            ActionContext ac = invocation.getInvocationContext();
            final Map<String, Object> parameters = retrieveParameters(ac);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Setting params {}", getParameterLogMap(parameters));
            }

            if (parameters != null) {
                Map<String, Object> contextMap = ac.getContextMap();
                try {
                    ReflectionContextState.setCreatingNullObjects(contextMap, true);
                    ReflectionContextState.setDenyMethodExecution(contextMap, true);
                    ReflectionContextState.setReportingConversionErrors(contextMap, true);

                    ValueStack stack = ac.getValueStack();
                    setParameters(action, stack, parameters);
                } finally {
                    ReflectionContextState.setCreatingNullObjects(contextMap, false);
                    ReflectionContextState.setDenyMethodExecution(contextMap, false);
                    ReflectionContextState.setReportingConversionErrors(contextMap, false);
                }
            }
        }
        return invocation.invoke();
    }

    
    protected Map<String, Object> retrieveParameters(ActionContext ac) {
        return ac.getParameters();
    }


    
    protected void addParametersToContext(ActionContext ac, Map<String, Object> newParams) {
    }

    protected void setParameters(final Object action, ValueStack stack, final Map<String, Object> parameters) {
        Map<String, Object> params;
        Map<String, Object> acceptableParameters;
        if (ordered) {
            params = new TreeMap<>(getOrderedComparator());
            acceptableParameters = new TreeMap<>(getOrderedComparator());
            params.putAll(parameters);
        } else {
            params = new TreeMap<>(parameters);
            acceptableParameters = new TreeMap<>();
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            if (isAcceptableParameter(name, action)) {
                acceptableParameters.put(name, entry.getValue());
            }
        }

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

        boolean memberAccessStack = newStack instanceof MemberAccessValueStack;
        if (memberAccessStack) {
            
            
            MemberAccessValueStack accessValueStack = (MemberAccessValueStack) newStack;
            accessValueStack.setAcceptProperties(acceptedPatterns.getAcceptedPatterns());
            accessValueStack.setExcludeProperties(excludedPatterns.getExcludedPatterns());
        }

        for (Map.Entry<String, Object> entry : acceptableParameters.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue();
            try {
                newStack.setParameter(name, value);
            } catch (RuntimeException e) {
                if (devMode) {
                    notifyDeveloperParameterException(action, name, e.getMessage());
                }
            }
        }

        if (clearableStack && (stack.getContext() != null) && (newStack.getContext() != null))
            stack.getContext().put(ActionContext.CONVERSION_ERRORS, newStack.getContext().get(ActionContext.CONVERSION_ERRORS));

        addParametersToContext(ActionContext.getContext(), acceptableParameters);
    }

    protected void notifyDeveloperParameterException(Object action, String property, String message) {
        String developerNotification = LocalizedTextUtil.findText(ParametersInterceptor.class, "devmode.notification",
                ActionContext.getContext().getLocale(), "Developer Notification:\n{0}",
                new Object[]{
                        "Unexpected Exception caught setting '" + property + "' on '" + action.getClass() + ": " + message
                }
        );
        LOG.error(developerNotification);
        
        if (action instanceof ValidationAware) {
            Collection<String> messages = ((ValidationAware) action).getActionMessages();
            messages.add(message);
            ((ValidationAware) action).setActionMessages(messages);
        }
    }

    
    protected boolean isAcceptableParameter(String name, Object action) {
        ParameterNameAware parameterNameAware = (action instanceof ParameterNameAware) ? (ParameterNameAware) action : null;
        return acceptableName(name) && (parameterNameAware == null || parameterNameAware.acceptableParameterName(name));
    }

    
    protected Comparator<String> getOrderedComparator() {
        return rbCollator;
    }

    protected String getParameterLogMap(Map<String, Object> parameters) {
        if (parameters == null) {
            return "NONE";
        }

        StringBuilder logEntry = new StringBuilder();
        for (Map.Entry entry : parameters.entrySet()) {
            logEntry.append(String.valueOf(entry.getKey()));
            logEntry.append(" => ");
            if (entry.getValue() instanceof Object[]) {
                Object[] valueArray = (Object[]) entry.getValue();
                logEntry.append("[ ");
                if (valueArray.length > 0 ) {
                    for (int indexA = 0; indexA < (valueArray.length - 1); indexA++) {
                        Object valueAtIndex = valueArray[indexA];
                        logEntry.append(String.valueOf(valueAtIndex));
                        logEntry.append(", ");
                    }
                    logEntry.append(String.valueOf(valueArray[valueArray.length - 1]));
                }
                logEntry.append(" ] ");
            } else {
                logEntry.append(String.valueOf(entry.getValue()));
            }
        }

        return logEntry.toString();
    }

    protected boolean acceptableName(String name) {
        boolean accepted = isWithinLengthLimit(name) && !isExcluded(name) && isAccepted(name);
        if (devMode && accepted) { 
            LOG.debug("Parameter [{}] was accepted and will be appended to action!", name);
        }
        return accepted;
    }

	protected boolean isWithinLengthLimit( String name ) {
        boolean matchLength = name.length() <= paramNameMaxLength;
        if (!matchLength) {
            notifyDeveloper("Parameter [{}] is too long, allowed length is [{}]", name, String.valueOf(paramNameMaxLength));
        }
        return matchLength;
	}

    protected boolean isAccepted(String paramName) {
        AcceptedPatternsChecker.IsAccepted result = acceptedPatterns.isAccepted(paramName);
        if (result.isAccepted()) {
            return true;
        }
        notifyDeveloper("Parameter [{}] didn't match accepted pattern [{}]!", paramName, result.getAcceptedPattern());
        return false;
    }

    protected boolean isExcluded(String paramName) {
        ExcludedPatternsChecker.IsExcluded result = excludedPatterns.isExcluded(paramName);
        if (result.isExcluded()) {
            notifyDeveloper("Parameter [{}] matches excluded pattern [{}]!", paramName, result.getExcludedPattern());
            return true;
        }
        return false;
    }

    private void notifyDeveloper(String message, String... parameters) {
        if (devMode) {
            LOG.warn(message, parameters);
        } else {
            LOG.debug(message, parameters);
        }
    }

    
    public boolean isOrdered() {
        return ordered;
    }

    
    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    
    public void setAcceptParamNames(String commaDelim) {
        acceptedPatterns.setAcceptedPatterns(commaDelim);
    }

    
    public void setExcludeParams(String commaDelim) {
        excludedPatterns.setExcludedPatterns(commaDelim);
    }

}
