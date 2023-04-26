

package org.apache.struts2.components.template;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.freemarker.FreemarkerManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;


public class FreemarkerTemplateEngine extends BaseTemplateEngine {
    static Class bodyContent = null;
    protected FreemarkerManager freemarkerManager;

    static {
        try {
            bodyContent = ClassLoaderUtil.loadClass("javax.servlet.jsp.tagext.BodyContent",
                    FreemarkerTemplateEngine.class);
        } catch (ClassNotFoundException e) {
            
            
            
            
        }
    }

    private static final Logger LOG = LogManager.getLogger(FreemarkerTemplateEngine.class);

    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }
    
    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
    	
        ValueStack stack = templateContext.getStack();
        Map context = stack.getContext();
        ServletContext servletContext = (ServletContext) context.get(ServletActionContext.SERVLET_CONTEXT);
        HttpServletRequest req = (HttpServletRequest) context.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) context.get(ServletActionContext.HTTP_RESPONSE);

        
        Configuration config = freemarkerManager.getConfiguration(servletContext);

        
        List<Template> templates = templateContext.getTemplate().getPossibleTemplates(this);

        
        freemarker.template.Template template = null;
        String templateName = null;
        Exception exception = null;
        for (Template t : templates) {
            templateName = getFinalTemplateName(t);
                try {
                    
                    template = config.getTemplate(templateName);
                    break;
                } catch (ParseException e) {
                    
                    exception = e;
                    break;
                } catch (IOException e) {
                    
                    if (exception == null) {
                        exception = e;
                    }
                }
        }

        if (template == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not load the FreeMarker template named '{}':", templateContext.getTemplate().getName());
                for (Template t : templates) {
                    LOG.error("Attempted: {}", getFinalTemplateName(t));
                }
                LOG.error("The TemplateLoader provided by the FreeMarker Configuration was a: {}", config.getTemplateLoader().getClass().getName());
            }
            if (exception != null) {
                throw exception;
            } else {
                return;
            }
        }

        LOG.debug("Rendering template: {}", templateName);

        ActionInvocation ai = ActionContext.getContext().getActionInvocation();

        Object action = (ai == null) ? null : ai.getAction();
        SimpleHash model = freemarkerManager.buildTemplateModel(stack, action, servletContext, req, res, config.getObjectWrapper());

        model.put("tag", templateContext.getTag());
        model.put("themeProperties", getThemeProps(templateContext.getTemplate()));

        
        
        Writer writer = templateContext.getWriter();
        final Writer wrapped = writer;
        writer = new Writer() {
            public void write(char cbuf[], int off, int len) throws IOException {
                wrapped.write(cbuf, off, len);
            }

            public void flush() throws IOException {
                
            }

            public void close() throws IOException {
                wrapped.close();
            }
        };

        try {
            stack.push(templateContext.getTag());
            template.process(model, writer);
        } finally {
            stack.pop();
        }
    }

    protected String getSuffix() {
        return "ftl";
    }
}