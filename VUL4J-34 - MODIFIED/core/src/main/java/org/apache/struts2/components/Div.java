

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(
    name="div",
    tldTagClass="org.apache.struts2.views.jsp.ui.DivTag",
    description="Render an HTML div",
    allowDynamicAttributes=true)
public class Div extends ClosingUIBean {

    public static final String TEMPLATE = "div";
    public static final String TEMPLATE_CLOSE = "div-close";
    public static final String COMPONENT_NAME = Div.class.getName();

    public Div(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE_CLOSE;
    }
}
