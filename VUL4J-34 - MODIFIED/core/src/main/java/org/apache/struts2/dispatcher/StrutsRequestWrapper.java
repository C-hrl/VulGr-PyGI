

package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import static org.apache.commons.lang3.BooleanUtils.isTrue;


public class StrutsRequestWrapper extends HttpServletRequestWrapper {

    private static final String REQUEST_WRAPPER_GET_ATTRIBUTE = "__requestWrapper.getAttribute";
    private final boolean disableRequestAttributeValueStackLookup;

    
    public StrutsRequestWrapper(HttpServletRequest req) {
        this(req, false);
    }

    
    public StrutsRequestWrapper(HttpServletRequest req, boolean disableRequestAttributeValueStackLookup) {
        super(req);
        this.disableRequestAttributeValueStackLookup = disableRequestAttributeValueStackLookup;
    }

    
    public Object getAttribute(String key) {
        if (key == null) {
            throw new NullPointerException("You must specify a key value");
        }

        if (disableRequestAttributeValueStackLookup || key.startsWith("javax.servlet")) {
            
            
            return super.getAttribute(key);
        }

        ActionContext ctx = ActionContext.getContext();
        Object attribute = super.getAttribute(key);

        if (ctx != null && attribute == null) {
            boolean alreadyIn = isTrue((Boolean) ctx.get(REQUEST_WRAPPER_GET_ATTRIBUTE));

            
            
            if (!alreadyIn && !key.contains("#")) {
                try {
                    
                    ctx.put(REQUEST_WRAPPER_GET_ATTRIBUTE, Boolean.TRUE);
                    ValueStack stack = ctx.getValueStack();
                    if (stack != null) {
                        attribute = stack.findValue(key);
                    }
                } finally {
                    ctx.put(REQUEST_WRAPPER_GET_ATTRIBUTE, Boolean.FALSE);
                }
            }
        }
        return attribute;
    }
}
