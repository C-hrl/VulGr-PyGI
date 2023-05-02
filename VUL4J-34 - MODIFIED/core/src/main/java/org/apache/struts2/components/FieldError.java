

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Param.UnnamedParametric;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@StrutsTag(name="fielderror", tldTagClass="org.apache.struts2.views.jsp.ui.FieldErrorTag", description="Render field error (all " +
                "or partial depending on param tag nested)if they exists")
public class FieldError extends UIBean implements UnnamedParametric {

    private List<String> errorFieldNames = new ArrayList<>();
    private boolean escape = true;

    public FieldError(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    private static final String TEMPLATE = "fielderror";

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (errorFieldNames != null)
            addParameter("errorFieldNames", errorFieldNames);

        addParameter("escape", escape);
    }

    public void addParameter(Object value) {
        if (value != null) {
            errorFieldNames.add(value.toString());
        }
    }

    public List<String> getFieldErrorFieldNames() {
        return errorFieldNames;
    }

    @StrutsTagAttribute(description="Field name for single field attribute usage", type="String")
    public void setFieldName(String fieldName) {
        addParameter(fieldName);
    }

    @StrutsTagAttribute(description=" Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}

