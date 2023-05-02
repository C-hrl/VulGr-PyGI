

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(
    name="password",
    tldTagClass="org.apache.struts2.views.jsp.ui.PasswordTag",
    description="Render an HTML input tag of type password",
    allowDynamicAttributes=true)
public class Password extends TextField {
    final public static String TEMPLATE = "password";

    protected String showPassword;

    public Password(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (showPassword != null) {
            addParameter("showPassword", findValue(showPassword, Boolean.class));
        }
    }

    @StrutsTagAttribute(description="Whether to show input", type="Boolean", defaultValue="false")
    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }

}
