

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.util.TextProviderHelper;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(
    name="label",
    tldTagClass="org.apache.struts2.views.jsp.ui.LabelTag",
    description="Render a label that displays read-only information",
    allowDynamicAttributes=true)
public class Label extends UIBean {
    final public static String TEMPLATE = "label";

    protected String forAttr;

    public Label(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (forAttr != null) {
            addParameter("for", findString(forAttr));
        }

        
        if (value != null) {
            addParameter("nameValue", findString(value));
        } else if (key != null) {
            Object nameValue = parameters.get("nameValue");
            if (nameValue == null || nameValue.toString().length() == 0) {
                
                String providedLabel = TextProviderHelper.getText(key, key, stack);
                addParameter("nameValue", providedLabel);
            }
        } else if (name != null) {
            String expr = completeExpressionIfAltSyntax(name);
            addParameter("nameValue", findString(expr));
        }
    }

    @StrutsTagAttribute(description=" HTML for attribute")
    public void setFor(String forAttr) {
        this.forAttr = forAttr;
    }
}
