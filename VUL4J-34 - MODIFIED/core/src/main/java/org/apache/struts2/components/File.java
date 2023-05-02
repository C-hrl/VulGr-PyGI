

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(
    name="file",
    tldTagClass="org.apache.struts2.views.jsp.ui.FileTag",
    description="Render a file input field",
    allowDynamicAttributes=true)
public class File extends UIBean {
    private final static Logger LOG = LogManager.getLogger(File.class);

    final public static String TEMPLATE = "file";

    protected String accept;
    protected String size;

    public File(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateParams() {
        super.evaluateParams();

        Form form = (Form) findAncestor(Form.class);
        if (form != null) {
            String encType = (String) form.getParameters().get("enctype");
            if (!"multipart/form-data".equals(encType)) {
                
                LOG.warn("Struts has detected a file upload UI tag (s:file) being used without a form set to enctype 'multipart/form-data'. This is probably an error!");
            }

            String method = (String) form.getParameters().get("method");
            if (!"post".equalsIgnoreCase(method)) {
                
                LOG.warn("Struts has detected a file upload UI tag (s:file) being used without a form set to method 'POST'. This is probably an error!");
            }
        }

        if (accept != null) {
            addParameter("accept", findString(accept));
        }

        if (size != null) {
            addParameter("size", findString(size));
        }
    }

    @StrutsTagAttribute(description="HTML accept attribute to indicate accepted file mimetypes")
    public void setAccept(String accept) {
        this.accept = accept;
    }

    @StrutsTagAttribute(description="HTML size attribute", required=false, type="Integer")
    public void setSize(String size) {
        this.size = size;
    }
}
