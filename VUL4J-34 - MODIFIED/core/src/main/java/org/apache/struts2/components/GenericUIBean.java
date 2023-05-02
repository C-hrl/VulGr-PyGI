

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.util.ContainUtil;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(name="component", tldTagClass="org.apache.struts2.views.jsp.ui.ComponentTag", description="Render a custom ui widget")
public class GenericUIBean extends UIBean {
    private final static String TEMPLATE = "empty";

    public GenericUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
}
