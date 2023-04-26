package org.apache.struts2.util;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.util.ContextUtil;


public class ComponentUtils {

    
    public static String stripExpressionIfAltSyntax(ValueStack stack, String expr) {
        if (altSyntax(stack)) {
            
            if (isExpression(expr)) {
                return expr.substring(2, expr.length() - 1);
            }
        }
        return expr;
    }

    
    public static boolean altSyntax(ValueStack stack) {
        return ContextUtil.isUseAltSyntax(stack.getContext());
    }

    
    public static boolean isExpression(String expr) {
        return expr.startsWith("%{") && expr.endsWith("}");
    }

    public static boolean containsExpression(String expr) {
        return expr.contains("%{") && expr.contains("}");
    }

}
