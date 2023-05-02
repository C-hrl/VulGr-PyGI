

package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.annotations.StrutsTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(name="head",
        tldBodyContent="empty",
        tldTagClass="org.apache.struts2.views.jsp.ui.HeadTag",
        description="Render a chunk of HEAD for your HTML file",
        allowDynamicAttributes = true)
public class Head extends UIBean {
    public static final String TEMPLATE = "head";

    private String encoding;

    public Head(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void evaluateParams() {
        super.evaluateParams();

        addParameter("encoding", encoding);
    }
}
