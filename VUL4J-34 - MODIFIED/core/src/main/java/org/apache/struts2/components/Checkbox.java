

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(
    name="checkbox",
    tldTagClass="org.apache.struts2.views.jsp.ui.CheckboxTag",
    description="Render a checkbox input field",
    allowDynamicAttributes=true)
public class Checkbox extends UIBean {
    final public static String TEMPLATE = "checkbox";

    protected String fieldValue;

    public Checkbox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        if (fieldValue != null) {
            addParameter("fieldValue", findString(fieldValue));
        } else {
            addParameter("fieldValue", "true");
        }
    }

    protected Class getValueClassType() {
        return Boolean.class; 
    }

    @StrutsTagAttribute(description="The actual HTML value attribute of the checkbox.", defaultValue="true")
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

}
