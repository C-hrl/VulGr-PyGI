

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;


@StrutsTag(name="actionerror", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.ui.ActionErrorTag", description="Render action errors if they exists")
public class ActionError extends UIBean {

    public static final String TEMPLATE = "actionerror";
    private boolean escape = true;

    public ActionError(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        boolean isEmptyList = true;
        Collection<String> actionMessages = (List) findValue("actionErrors");
        if (actionMessages != null) {
            for (String message : actionMessages) {
                if (StringUtils.isNotBlank(message)) {
                    isEmptyList = false;
                    break;
                }
            }
        }

        addParameter("isEmptyList", isEmptyList);
        addParameter("escape", escape);
    }

    @StrutsTagAttribute(description=" Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
