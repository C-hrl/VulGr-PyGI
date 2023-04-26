

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(
    name="hidden",
    tldTagClass="org.apache.struts2.views.jsp.ui.HiddenTag",
    description="Render a hidden input field",
    allowDynamicAttributes=true)
public class Hidden extends UIBean {
    final public static String TEMPLATE = "hidden";

    public Hidden(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

}
