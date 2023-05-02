

package org.apache.struts2.views.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.StrutsUtil;
import org.apache.struts2.views.jsp.ui.OgnlTool;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;


public class ContextUtil {
    public static final String REQUEST = "request";
    public static final String REQUEST2 = "request";
    public static final String RESPONSE = "response";
    public static final String RESPONSE2 = "response";
    public static final String SESSION = "session";
    public static final String BASE = "base";
    public static final String STACK = "stack";
    public static final String OGNL = "ognl";
    public static final String STRUTS = "struts";
    public static final String ACTION = "action";
    
    public static Map getStandardContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        HashMap map = new HashMap();
        map.put(REQUEST, req);
        map.put(REQUEST2, req);
        map.put(RESPONSE, res);
        map.put(RESPONSE2, res);
        map.put(SESSION, req.getSession(false));
        map.put(BASE, req.getContextPath());
        map.put(STACK, stack);
        map.put(OGNL, ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(OgnlTool.class));
        map.put(STRUTS, new StrutsUtil(stack, req, res));

        ActionInvocation invocation = (ActionInvocation) stack.getContext().get(ActionContext.ACTION_INVOCATION);
        if (invocation != null) {
            map.put(ACTION, invocation.getAction());
        }
        return map;
    }

    
    public static boolean isUseAltSyntax(Map context) {
        
        
        
        return "true".equals(((Container)context.get(ActionContext.CONTAINER)).getInstance(String.class, StrutsConstants.STRUTS_TAG_ALTSYNTAX)) ||(
                (context.containsKey("useAltSyntax") &&
                        context.get("useAltSyntax") != null &&
                        "true".equals(context.get("useAltSyntax").toString())));
    }
    
    
    public static String getTemplateSuffix(Map context) {
        return context.containsKey("templateSuffix") ? (String) context.get("templateSuffix") : null;
    }
}
