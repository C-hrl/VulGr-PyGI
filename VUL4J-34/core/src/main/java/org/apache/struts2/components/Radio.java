

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(name="radio",
        tldTagClass="org.apache.struts2.views.jsp.ui.RadioTag",
        description="Renders a radio button input field",
        allowDynamicAttributes = true)
public class Radio extends ListUIBean {
    final public static String TEMPLATE = "radiomap";
    
    public Radio(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    public void evaluateExtraParams() {
    	super.evaluateExtraParams();
    }
}