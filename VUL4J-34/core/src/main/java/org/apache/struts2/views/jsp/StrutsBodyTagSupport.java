

package org.apache.struts2.views.jsp;

import java.io.PrintWriter;

import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.util.FastByteArrayOutputStream;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;



public class StrutsBodyTagSupport extends BodyTagSupport {

    private static final long serialVersionUID = -1201668454354226175L;

    protected ValueStack getStack() {
        return TagUtils.getStack(pageContext);
    }

    protected String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    protected Object findValue(String expr) {
    	expr = ComponentUtils.stripExpressionIfAltSyntax(getStack(), expr);

        return getStack().findValue(expr);
    }

    protected Object findValue(String expr, Class toType) {
        if (ComponentUtils.altSyntax(getStack()) && toType == String.class) {
        	return TextParseUtil.translateVariables('%', expr, getStack());
            
        } else {
        	expr = ComponentUtils.stripExpressionIfAltSyntax(getStack(), expr);

            return getStack().findValue(expr, toType);
        }
    }

    protected String toString(Throwable t) {
        try (FastByteArrayOutputStream bout = new FastByteArrayOutputStream();
                PrintWriter wrt = new PrintWriter(bout)) {
            t.printStackTrace(wrt);

            return bout.toString();
        }
    }

    protected String getBody() {
        if (bodyContent == null) {
            return "";
        } else {
            return bodyContent.getString().trim();
        }
    }
}
