

package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;



public class InvocationSessionStore {

    private static final String INVOCATION_MAP_KEY = "org.apache.struts2.util.InvocationSessionStore.invocationMap";


    private InvocationSessionStore() {
    }


    
    public static ActionInvocation loadInvocation(String key, String token) {
        InvocationContext invocationContext = (InvocationContext) getInvocationMap().get(key);

        if ((invocationContext == null) || !invocationContext.token.equals(token)) {
            return null;
        }

        ValueStack stack = invocationContext.invocation.getStack();
        ActionContext.getContext().setValueStack(stack);

        return invocationContext.invocation.deserialize(ActionContext.getContext());
    }

    
    public static void storeInvocation(String key, String token, ActionInvocation invocation) {
        InvocationContext invocationContext = new InvocationContext(invocation.serialize(), token);
        Map invocationMap = getInvocationMap();
        invocationMap.put(key, invocationContext);
        setInvocationMap(invocationMap);
    }

    static void setInvocationMap(Map invocationMap) {
        Map session = ActionContext.getContext().getSession();

        if (session == null) {
            throw new IllegalStateException("Unable to access the session.");
        }

        session.put(INVOCATION_MAP_KEY, invocationMap);
    }

    static Map getInvocationMap() {
        Map session = ActionContext.getContext().getSession();

        if (session == null) {
            throw new IllegalStateException("Unable to access the session.");
        }

        Map invocationMap = (Map) session.get(INVOCATION_MAP_KEY);

        if (invocationMap == null) {
            invocationMap = new HashMap();
            setInvocationMap(invocationMap);
        }

        return invocationMap;
    }


    private static class InvocationContext implements Serializable {

        private static final long serialVersionUID = -286697666275777888L;

        ActionInvocation invocation;
        String token;

        public InvocationContext(ActionInvocation invocation, String token) {
            this.invocation = invocation;
            this.token = token;
        }
    }
}
