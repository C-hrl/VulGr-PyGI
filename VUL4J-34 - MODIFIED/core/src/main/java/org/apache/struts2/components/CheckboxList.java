

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(
        name="checkboxlist",
        tldTagClass="org.apache.struts2.views.jsp.ui.CheckboxListTag",
        description="Render a list of checkboxes",
        allowDynamicAttributes = true)
public class CheckboxList extends ListUIBean {
    final public static String TEMPLATE = "checkboxlist";
    
    public CheckboxList(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    public void evaluateExtraParams() {
    	super.evaluateExtraParams();
    }

}