

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(
    name="textarea",
    tldTagClass="org.apache.struts2.views.jsp.ui.TextareaTag",
    description="Render HTML textarea tag.",
    allowDynamicAttributes=true)
public class TextArea extends UIBean {
    final public static String TEMPLATE = "textarea";

    protected String cols;
    protected String readonly;
    protected String rows;
    protected String wrap;

    public TextArea(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (readonly != null) {
            addParameter("readonly", findValue(readonly, Boolean.class));
        }

        if (cols != null) {
            addParameter("cols", findString(cols));
        }

        if (rows != null) {
            addParameter("rows", findString(rows));
        }

        if (wrap != null) {
            addParameter("wrap", findString(wrap));
        }
    }

    @StrutsTagAttribute(description="HTML cols attribute", type="Integer")
    public void setCols(String cols) {
        this.cols = cols;
    }

    @StrutsTagAttribute(description="Whether the textarea is readonly", type="Boolean", defaultValue="false")
    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    @StrutsTagAttribute(description="HTML rows attribute", type="Integer")
    public void setRows(String rows) {
        this.rows = rows;
    }

    @StrutsTagAttribute(description="HTML wrap attribute")
    public void setWrap(String wrap) {
        this.wrap = wrap;
    }
}
