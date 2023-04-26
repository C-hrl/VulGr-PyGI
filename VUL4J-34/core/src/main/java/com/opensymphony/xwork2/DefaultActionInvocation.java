
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import ognl.MethodFailedException;
import ognl.NoSuchPropertyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class DefaultActionInvocation implements ActionInvocation {

    private static final Logger LOG = LogManager.getLogger(DefaultActionInvocation.class);

    protected Object action;
    protected ActionProxy proxy;
    protected List<PreResultListener> preResultListeners;
    protected Map<String, Object> extraContext;
    protected ActionContext invocationContext;
    protected Iterator<InterceptorMapping> interceptors;
    protected ValueStack stack;
    protected Result result;
    protected Result explicitResult;
    protected String resultCode;
    protected boolean executed = false;
    protected boolean pushAction = true;
    protected ObjectFactory objectFactory;
    protected ActionEventListener actionEventListener;
    protected ValueStackFactory valueStackFactory;
    protected Container container;
    protected UnknownHandlerManager unknownHandlerManager;
    protected OgnlUtil ognlUtil;

    public DefaultActionInvocation(final Map<String, Object> extraContext, final boolean pushAction) {
        this.extraContext = extraContext;
        this.pushAction = pushAction;
    }

    @Inject
    public void setUnknownHandlerManager(UnknownHandlerManager unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory fac) {
        this.valueStackFactory = fac;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setContainer(Container cont) {
        this.container = cont;
    }

    @Inject(required=false)
    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }

    public Object getAction() {
        return action;
    }

    public boolean isExecuted() {
        return executed;
    }

    public ActionContext getInvocationContext() {
        return invocationContext;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    
    public Result getResult() throws Exception {
        Result returnResult = result;

        
        while (returnResult instanceof ActionChainResult) {
            ActionProxy aProxy = ((ActionChainResult) returnResult).getProxy();

            if (aProxy != null) {
                Result proxyResult = aProxy.getInvocation().getResult();

                if ((proxyResult != null) && (aProxy.getExecuteResult())) {
                    returnResult = proxyResult;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        return returnResult;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        if (isExecuted()) {
            throw new IllegalStateException("Result has already been executed.");
        }
        this.resultCode = resultCode;
    }


    public ValueStack getStack() {
        return stack;
    }

    
    public void addPreResultListener(PreResultListener listener) {
        if (preResultListeners == null) {
            preResultListeners = new ArrayList<>(1);
        }

        preResultListeners.add(listener);
    }

    public Result createResult() throws Exception {
        LOG.trace("Creating result related to resultCode [{}]", resultCode);

        if (explicitResult != null) {
            Result ret = explicitResult;
            explicitResult = null;

            return ret;
        }
        ActionConfig config = proxy.getConfig();
        Map<String, ResultConfig> results = config.getResults();

        ResultConfig resultConfig = null;

        try {
            resultConfig = results.get(resultCode);
        } catch (NullPointerException e) {
            LOG.debug("Got NPE trying to read result configuration for resultCode [{}]", resultCode);
        }
        
        if (resultConfig == null) {
            
            resultConfig = results.get("*");
        }

        if (resultConfig != null) {
            try {
                return objectFactory.buildResult(resultConfig, invocationContext.getContextMap());
            } catch (Exception e) {
                LOG.error("There was an exception while instantiating the result of type {}", resultConfig.getClassName(), e);
                throw new XWorkException(e, resultConfig);
            }
        } else if (resultCode != null && !Action.NONE.equals(resultCode) && unknownHandlerManager.hasUnknownHandlers()) {
            return unknownHandlerManager.handleUnknownResult(invocationContext, proxy.getActionName(), proxy.getConfig(), resultCode);
        }
        return null;
    }

    
    public String invoke() throws Exception {
        String profileKey = "invoke: ";
        try {
            UtilTimerStack.push(profileKey);

            if (executed) {
                throw new IllegalStateException("Action has already executed");
            }

            if (interceptors.hasNext()) {
                final InterceptorMapping interceptor = interceptors.next();
                String interceptorMsg = "interceptor: " + interceptor.getName();
                UtilTimerStack.push(interceptorMsg);
                try {
                    resultCode = interceptor.getInterceptor().intercept(DefaultActionInvocation.this);
                } finally {
                    UtilTimerStack.pop(interceptorMsg);
                }
            } else {
                resultCode = invokeActionOnly();
            }

            
            
            if (!executed) {
                result = createResult();

                if (preResultListeners != null) {
                    LOG.trace("Executing PreResultListeners for result [{}]", result);

                    for (Object preResultListener : preResultListeners) {
                        PreResultListener listener = (PreResultListener) preResultListener;

                        String _profileKey = "preResultListener: ";
                        try {
                            UtilTimerStack.push(_profileKey);
                            listener.beforeResult(this, resultCode);
                        }
                        finally {
                            UtilTimerStack.pop(_profileKey);
                        }
                    }
                }

                
                if (proxy.getExecuteResult()) {
                    executeResult();
                }

                executed = true;
            }

            return resultCode;
        }
        finally {
            UtilTimerStack.pop(profileKey);
        }
    }

    public String invokeActionOnly() throws Exception {
        return invokeAction(getAction(), proxy.getConfig());
    }

    protected void createAction(Map<String, Object> contextMap) {
        
        String timerKey = "actionCreate: " + proxy.getActionName();
        try {
            UtilTimerStack.push(timerKey);
            action = objectFactory.buildAction(proxy.getActionName(), proxy.getNamespace(), proxy.getConfig(), contextMap);
        } catch (InstantiationException e) {
            throw new XWorkException("Unable to instantiate Action!", e, proxy.getConfig());
        } catch (IllegalAccessException e) {
            throw new XWorkException("Illegal access to constructor, is it public?", e, proxy.getConfig());
        } catch (Exception e) {
            String gripe;

            if (proxy == null) {
                gripe = "Whoa!  No ActionProxy instance found in current ActionInvocation.  This is bad ... very bad";
            } else if (proxy.getConfig() == null) {
                gripe = "Sheesh.  Where'd that ActionProxy get to?  I can't find it in the current ActionInvocation!?";
            } else if (proxy.getConfig().getClassName() == null) {
                gripe = "No Action defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
            } else {
                gripe = "Unable to instantiate Action, " + proxy.getConfig().getClassName() + ",  defined for '" + proxy.getActionName() + "' in namespace '" + proxy.getNamespace() + "'";
            }

            gripe += (((" -- " + e.getMessage()) != null) ? e.getMessage() : " [no message in exception]");
            throw new XWorkException(gripe, e, proxy.getConfig());
        } finally {
            UtilTimerStack.pop(timerKey);
        }

        if (actionEventListener != null) {
            action = actionEventListener.prepare(action, stack);
        }
    }

    protected Map<String, Object> createContextMap() {
        Map<String, Object> contextMap;

        if ((extraContext != null) && (extraContext.containsKey(ActionContext.VALUE_STACK))) {
            
            stack = (ValueStack) extraContext.get(ActionContext.VALUE_STACK);

            if (stack == null) {
                throw new IllegalStateException("There was a null Stack set into the extra params.");
            }

            contextMap = stack.getContext();
        } else {
            
            
            stack = valueStackFactory.createValueStack();

            
            contextMap = stack.getContext();
        }

        
        if (extraContext != null) {
            contextMap.putAll(extraContext);
        }

        
        contextMap.put(ActionContext.ACTION_INVOCATION, this);
        contextMap.put(ActionContext.CONTAINER, container);

        return contextMap;
    }

    
    private void executeResult() throws Exception {
        String timerKey = "executeResult: " + getResultCode();
        try {
            UtilTimerStack.push(timerKey);
            if (result != null) {
                result.execute(this);
            } else if (resultCode != null && !Action.NONE.equals(resultCode)) {
                throw new ConfigurationException("No result defined for action " + getAction().getClass().getName()
                        + " and result " + getResultCode(), proxy.getConfig());
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No result returned for action {} at {}", getAction().getClass().getName(), proxy.getConfig().getLocation());
                }
            }
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    public void init(ActionProxy proxy) {
        this.proxy = proxy;
        Map<String, Object> contextMap = createContextMap();

        
        
        ActionContext actionContext = ActionContext.getContext();

        if (actionContext != null) {
            actionContext.setActionInvocation(this);
        }

        createAction(contextMap);

        if (pushAction) {
            stack.push(action);
            contextMap.put("action", action);
        }

        invocationContext = new ActionContext(contextMap);
        invocationContext.setName(proxy.getActionName());

        createInterceptors(proxy);
    }

    protected void createInterceptors(ActionProxy proxy) {
        
        List<InterceptorMapping> interceptorList = new ArrayList<>(proxy.getConfig().getInterceptors());
        interceptors = interceptorList.iterator();
    }

    protected String invokeAction(Object action, ActionConfig actionConfig) throws Exception {
        String methodName = proxy.getMethod();

        LOG.debug("Executing action method = {}", methodName);

        String timerKey = "invokeAction: " + proxy.getActionName();
        try {
            UtilTimerStack.push(timerKey);

            Object methodResult;
            try {
                methodResult = ognlUtil.getValue(methodName + "()", getStack().getContext(), action);
            } catch (MethodFailedException e) {
                
                if (e.getReason() instanceof NoSuchMethodException) {
                    if (unknownHandlerManager.hasUnknownHandlers()) {
                        try {
                            methodResult = unknownHandlerManager.handleUnknownMethod(action, methodName);
                        } catch (NoSuchMethodException ignore) {
                            
                            throw e;
                        }
                    } else {
                        
                        throw e;
                    }
                    
                    if (methodResult == null) {
                        throw e;
                    }
                } else {
                    
                    throw e;
                }
            }
            return saveResult(actionConfig, methodResult);
        } catch (NoSuchPropertyException e) {
            throw new IllegalArgumentException("The " + methodName + "() is not defined in action " + getAction().getClass() + "");
        } catch (MethodFailedException e) {
            
            Throwable t = e.getCause();

            if (actionEventListener != null) {
                String result = actionEventListener.handleException(t, getStack());
                if (result != null) {
                    return result;
                }
            }
            if (t instanceof Exception) {
                throw (Exception) t;
            } else {
                throw e;
            }
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    
    protected String saveResult(ActionConfig actionConfig, Object methodResult) {
        if (methodResult instanceof Result) {
            this.explicitResult = (Result) methodResult;

            
            container.inject(explicitResult);
            return null;
        } else {
            return (String) methodResult;
        }
    }

    
    public ActionInvocation serialize() {
        DefaultActionInvocation that = this;
        that.container = null;
        return that;
    }

    
    public ActionInvocation deserialize(ActionContext actionContext) {
        DefaultActionInvocation that = this;
        that.container = actionContext.getContainer();
        return that;
    }

}
