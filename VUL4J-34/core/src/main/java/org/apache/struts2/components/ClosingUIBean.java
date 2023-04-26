

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;


public abstract class ClosingUIBean extends UIBean {
    private static final Logger LOG = LogManager.getLogger(ClosingUIBean.class);

    protected ClosingUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    String openTemplate;

    public abstract String getDefaultOpenTemplate();

    @StrutsTagAttribute(description="Set template to use for opening the rendered html.")
    public void setOpenTemplate(String openTemplate) {
        this.openTemplate = openTemplate;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        try {
            evaluateParams();

            mergeTemplate(writer, buildTemplateName(openTemplate, getDefaultOpenTemplate()));
        } catch (Exception e) {
            LOG.error("Could not open template", e);
        }

        return result;
    }
}
