
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;



public class ActionChainResult implements Result {

    private static final Logger LOG = LogManager.getLogger(ActionChainResult.class);

    
    public static final String DEFAULT_PARAM = "actionName";

    
    private static final String CHAIN_HISTORY = "CHAIN_HISTORY";

    
    public static final String SKIP_ACTIONS_PARAM = "skipActions";


    private ActionProxy proxy;
    private String actionName;
    
    private String namespace;

    private String methodName;

    
    private String skipActions;

    private ActionProxyFactory actionProxyFactory;

    public ActionChainResult() {
        super();
    }

    public ActionChainResult(String namespace, String actionName, String methodName) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
    }

    public ActionChainResult(String namespace, String actionName, String methodName, String skipActions) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
        this.skipActions = skipActions;
    }


    
    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    
    public void setSkipActions(String actions) {
        this.skipActions = actions;
    }


    public void setMethod(String method) {
        this.methodName = method;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    
    public static LinkedList<String> getChainHistory() {
        LinkedList<String> chainHistory = (LinkedList<String>) ActionContext.getContext().get(CHAIN_HISTORY);
        
        if (chainHistory == null) {
            chainHistory = new LinkedList<>();
            ActionContext.getContext().put(CHAIN_HISTORY, chainHistory);
        }

        return chainHistory;
    }

    
    public void execute(ActionInvocation invocation) throws Exception {
        
        if (this.namespace == null) {
            this.namespace = invocation.getProxy().getNamespace();
        }

        ValueStack stack = ActionContext.getContext().getValueStack();
        String finalNamespace = TextParseUtil.translateVariables(namespace, stack);
        String finalActionName = TextParseUtil.translateVariables(actionName, stack);
        String finalMethodName = this.methodName != null
                ? TextParseUtil.translateVariables(this.methodName, stack)
                : null;

        if (isInChainHistory(finalNamespace, finalActionName, finalMethodName)) {
            addToHistory(finalNamespace, finalActionName, finalMethodName);
            throw new XWorkException("Infinite recursion detected: " + ActionChainResult.getChainHistory().toString());
        }

        if (ActionChainResult.getChainHistory().isEmpty() && invocation != null && invocation.getProxy() != null) {
            addToHistory(finalNamespace, invocation.getProxy().getActionName(), invocation.getProxy().getMethod());
        }
        addToHistory(finalNamespace, finalActionName, finalMethodName);

        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.VALUE_STACK, ActionContext.getContext().getValueStack());
        extraContext.put(ActionContext.PARAMETERS, ActionContext.getContext().getParameters());
        extraContext.put(CHAIN_HISTORY, ActionChainResult.getChainHistory());

        LOG.debug("Chaining to action {}", finalActionName);

        proxy = actionProxyFactory.createActionProxy(finalNamespace, finalActionName, finalMethodName, extraContext);
        proxy.execute();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ActionChainResult that = (ActionChainResult) o;

        if (actionName != null ? !actionName.equals(that.actionName) : that.actionName != null) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;

        return true;
    }

    @Override public int hashCode() {
        int result;
        result = (actionName != null ? actionName.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }

    private boolean isInChainHistory(String namespace, String actionName, String methodName) {
        LinkedList<? extends String> chainHistory = ActionChainResult.getChainHistory();

        if (chainHistory == null) {
            return false;
        } else {
            
            Set<String> skipActionsList = new HashSet<>();
            if (skipActions != null && skipActions.length() > 0) {
                ValueStack stack = ActionContext.getContext().getValueStack();
                String finalSkipActions = TextParseUtil.translateVariables(this.skipActions, stack);
                skipActionsList.addAll(TextParseUtil.commaDelimitedStringToSet(finalSkipActions));
            }
            if (!skipActionsList.contains(actionName)) {
                
                return chainHistory.contains(makeKey(namespace, actionName, methodName));
            }

            return false;
        }
    }

    private void addToHistory(String namespace, String actionName, String methodName) {
        List<String> chainHistory = ActionChainResult.getChainHistory();
        chainHistory.add(makeKey(namespace, actionName, methodName));
    }

    private String makeKey(String namespace, String actionName, String methodName) {
        if (null == methodName) {
            return namespace + "/" + actionName;
        }

        return namespace + "/" + actionName + "!" + methodName;
    }
}
