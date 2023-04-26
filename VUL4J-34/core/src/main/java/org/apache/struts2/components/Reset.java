

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(
    name="reset",
    tldTagClass="org.apache.struts2.views.jsp.ui.ResetTag",
    description="Render a reset button",
    allowDynamicAttributes=true)
public class Reset extends FormButton {
    final public static String TEMPLATE = "reset";

    protected String src;

    public Reset(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return "empty";
    }

    protected String getDefaultTemplate() {
        return Reset.TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (src != null)
            addParameter("src", findString(src));
    }

    public void evaluateParams() {
        if (value == null) {
            value = (key != null ? "%{getText('"+key+"')}" : "Reset");
        }
        super.evaluateParams();
    }

    
    protected boolean supportsImageType() {
        return false;
    }

    @StrutsTagAttribute(description="Supply a reset button text apart from reset value. Will have no effect for " +
                "<i>input</i> type reset, since button text will always be the value parameter.")
    public void setLabel(String label) {
        super.setLabel(label);
    }

    @StrutsTagAttribute(description="Supply an image src for <i>image</i> type reset button. Will have no effect for types <i>input</i> and <i>button</i>.")
    public void setSrc(String src) {
        this.src = src;
    }

}
