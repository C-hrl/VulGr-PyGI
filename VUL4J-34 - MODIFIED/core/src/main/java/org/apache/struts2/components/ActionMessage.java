

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;


@StrutsTag(name="actionmessage", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.ui.ActionMessageTag", description="Render action messages if they exists")
public class ActionMessage extends UIBean {

    private static final String TEMPLATE = "actionmessage";
    protected boolean escape = true;

    public ActionMessage(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        boolean isEmptyList = true;
        Collection<String> actionMessages = (List) findValue("actionMessages");
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

    @StrutsTagAttribute(description = "Whether to escape HTML", type = "Boolean", defaultValue = "true")
    public void setEscape(boolean escape) {
        this.escape = escape;
    }
}
