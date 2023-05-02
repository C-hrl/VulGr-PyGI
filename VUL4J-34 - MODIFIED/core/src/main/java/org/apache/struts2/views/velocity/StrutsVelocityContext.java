

package org.apache.struts2.views.velocity;

import org.apache.velocity.VelocityContext;

import com.opensymphony.xwork2.util.ValueStack;



public class StrutsVelocityContext extends VelocityContext {

    private static final long serialVersionUID = 8497212428904436963L;
    ValueStack stack;
    VelocityContext[] chainedContexts;


    public StrutsVelocityContext(ValueStack stack) {
        this(null, stack);
    }

    public StrutsVelocityContext(VelocityContext[] chainedContexts, ValueStack stack) {
        this.chainedContexts = chainedContexts;
        this.stack = stack;
    }


    public boolean internalContainsKey(Object key) {
        boolean contains = super.internalContainsKey(key);

        
        if (contains) {
            return true;
        }

        
        if (stack != null) {
            Object o = stack.findValue(key.toString());

            if (o != null) {
                return true;
            }

            o = stack.getContext().get(key.toString());
            if (o != null) {
                return true;
            }
        }

        
        if (chainedContexts != null) {
            for (int index = 0; index < chainedContexts.length; index++) {
                if (chainedContexts[index].containsKey(key)) {
                    return true;
                }
            }
        }

        
        return false;
    }

    public Object internalGet(String key) {
        
        if (super.internalContainsKey(key)) {
            return super.internalGet(key);
        }

        
        if (stack != null) {
            Object object = stack.findValue(key);

            if (object != null) {
                return object;
            }

            object = stack.getContext().get(key);
            if (object != null) {
                return object;
            }

        }

        
        if (chainedContexts != null) {
            for (int index = 0; index < chainedContexts.length; index++) {
                if (chainedContexts[index].containsKey(key)) {
                    return chainedContexts[index].internalGet(key);
                }
            }
        }

        
        return null;
    }
}
