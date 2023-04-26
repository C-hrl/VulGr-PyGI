

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.JspSupportServlet;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;
import java.io.OutputStreamWriter;
import java.io.Writer;



public class VelocityResult extends StrutsResultSupport {

    private static final long serialVersionUID = 7268830767762559424L;

    private static final Logger LOG = LogManager.getLogger(VelocityResult.class);
    
    private String defaultEncoding;
    private VelocityManager velocityManager;
    private JspFactory jspFactory = JspFactory.getDefaultFactory();

    public VelocityResult() {
        super();
    }

    public VelocityResult(String location) {
        super(location);
    }
    
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String val) {
        defaultEncoding = val;
    }
    
    @Inject
    public void setVelocityManager(VelocityManager mgr) {
        this.velocityManager = mgr;
    }

    
    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ValueStack stack = ActionContext.getContext().getValueStack();

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ServletContext servletContext = ServletActionContext.getServletContext();
        Servlet servlet = JspSupportServlet.jspSupportServlet;

        velocityManager.init(servletContext);

        boolean usedJspFactory = false;
        PageContext pageContext = (PageContext) ActionContext.getContext().get(ServletActionContext.PAGE_CONTEXT);

        if (pageContext == null && servlet != null) {
            pageContext = jspFactory.getPageContext(servlet, request, response, null, true, 8192, true);
            ActionContext.getContext().put(ServletActionContext.PAGE_CONTEXT, pageContext);
            usedJspFactory = true;
        }

        try {
            String encoding = getEncoding(finalLocation);
            String contentType = getContentType(finalLocation);

            if (encoding != null) {
                contentType = contentType + ";charset=" + encoding;
            }

            Template t = getTemplate(stack, velocityManager.getVelocityEngine(), invocation, finalLocation, encoding);

            Context context = createContext(velocityManager, stack, request, response, finalLocation);
            Writer writer = new OutputStreamWriter(response.getOutputStream(), encoding);


            response.setContentType(contentType);

            t.merge(context, writer);

            
            
            writer.flush();
        } catch (Exception e) {
            LOG.error("Unable to render velocity template: '{}'", finalLocation, e);
            throw e;
        } finally {
            if (usedJspFactory) {
                jspFactory.releasePageContext(pageContext);
            }
        }
    }

    
    protected String getContentType(String templateLocation) {
        return "text/html";
    }

    
    protected String getEncoding(String templateLocation) {
        String encoding = defaultEncoding;
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    
    protected Template getTemplate(ValueStack stack, VelocityEngine velocity, ActionInvocation invocation, String location, String encoding) throws Exception {
        if (!location.startsWith("/")) {
            location = invocation.getProxy().getNamespace() + "/" + location;
        }

        Template template = velocity.getTemplate(location, encoding);

        return template;
    }

    
    protected Context createContext(VelocityManager velocityManager, ValueStack stack, HttpServletRequest request, HttpServletResponse response, String location) {
        return velocityManager.createContext(stack, request, response);
    }
}
