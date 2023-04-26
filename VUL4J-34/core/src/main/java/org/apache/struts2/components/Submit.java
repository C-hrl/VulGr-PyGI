

package org.apache.struts2.components;

import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@StrutsTag(
    name="submit",
    tldTagClass="org.apache.struts2.views.jsp.ui.SubmitTag",
    description="Render a submit button",
    allowDynamicAttributes=true)
public class Submit extends FormButton {

    private static final Logger LOG = LogManager.getLogger(Submit.class);
    final public static String OPEN_TEMPLATE = "submit";
    final public static String TEMPLATE = "submit-close";
    protected String src;

    public Submit(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        if ((key == null) && (value == null)) {
            value = "Submit";
        }

        if (((key != null)) && (value == null)) {
            this.value = "%{getText('"+key +"')}";
        }

        super.evaluateParams();
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (src != null)
            addParameter("src", findString(src));
    }

    
    protected boolean supportsImageType() {
        return true;
    }

    @StrutsTagAttribute(description="Supply an image src for <i>image</i> type submit button. Will have no effect for types <i>input</i> and <i>button</i>.")
    public void setSrc(String src) {
        this.src = src;
    }


    @Override
    public boolean usesBody() {
        return true;
    }

    
    public boolean end(Writer writer, String body) {
        evaluateParams();
        try {
            addParameter("body", body);

            mergeTemplate(writer, buildTemplateName(template, getDefaultTemplate()));
        } catch (Exception e) {
            LOG.error("error when rendering", e);
        }
        finally {
            popComponentStack();
        }

        return false;
    }
}
