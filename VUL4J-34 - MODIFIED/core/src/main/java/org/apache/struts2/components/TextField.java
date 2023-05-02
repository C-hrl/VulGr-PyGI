

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(
    name="textfield",
    tldTagClass="org.apache.struts2.views.jsp.ui.TextFieldTag",
    description="Render an HTML input field of type text",
    allowDynamicAttributes=true)
public class TextField extends UIBean {
    
    final public static String TEMPLATE = "text";


    protected String maxlength;
    protected String readonly;
    protected String size;
    protected String type;

    public TextField(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (size != null) {
            addParameter("size", findString(size));
        }

        if (maxlength != null) {
            addParameter("maxlength", findString(maxlength));
        }

        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }

        if (type != null) {
            addParameter("type", findString(type));
        }

    }

    @StrutsTagAttribute(description="HTML maxlength attribute", type="Integer")
    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    @StrutsTagAttribute(description="Deprecated. Use maxlength instead.", type="Integer")
    public void setMaxLength(String maxlength) {
        this.maxlength = maxlength;
    }

    @StrutsTagAttribute(description="Whether the input is readonly", type="Boolean", defaultValue="false")
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    @StrutsTagAttribute(description="HTML size attribute",  type="Integer")
    public void setSize(String size) {
        this.size = size;
    }

    @StrutsTagAttribute(description="Specifies the html5 type element to display. e.g. text, email, url", defaultValue="text")
    public void setType(String type) {
        this.type = type;
    }
}
