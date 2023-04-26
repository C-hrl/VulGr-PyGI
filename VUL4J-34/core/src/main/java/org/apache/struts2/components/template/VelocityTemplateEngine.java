

package org.apache.struts2.components.template;

import com.opensymphony.xwork2.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.velocity.VelocityManager;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;


public class VelocityTemplateEngine extends BaseTemplateEngine {
    private static final Logger LOG = LogManager.getLogger(VelocityTemplateEngine.class);
    
    private VelocityManager velocityManager;
    
    @Inject
    public void setVelocityManager(VelocityManager mgr) {
        this.velocityManager = mgr;
    }

    public void renderTemplate(TemplateRenderingContext templateContext) throws Exception {
        
        Map actionContext = templateContext.getStack().getContext();
        ServletContext servletContext = (ServletContext) actionContext.get(ServletActionContext.SERVLET_CONTEXT);
        HttpServletRequest req = (HttpServletRequest) actionContext.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) actionContext.get(ServletActionContext.HTTP_RESPONSE);

        
        velocityManager.init(servletContext);
        VelocityEngine velocityEngine = velocityManager.getVelocityEngine();

        
        List<Template> templates = templateContext.getTemplate().getPossibleTemplates(this);

        
        org.apache.velocity.Template template = null;
        String templateName = null;
        Exception exception = null;
        for (Template t : templates) {
            templateName = getFinalTemplateName(t);
            try {
                
                template = velocityEngine.getTemplate(templateName);
                break;
            } catch (Exception e) {
                if (exception == null) {
                    exception = e;
                }
            }
        }

        if (template == null) {
            LOG.error("Could not load template {}", templateContext.getTemplate());
            if (exception != null) {
                throw exception;
            } else {
                return;
            }
        }

        LOG.debug("Rendering template {}", templateName);

        Context context = velocityManager.createContext(templateContext.getStack(), req, res);

        Writer outputWriter = templateContext.getWriter();
        context.put("tag", templateContext.getTag());
        context.put("parameters", templateContext.getParameters());

        template.merge(context, outputWriter);
    }

    protected String getSuffix() {
        return "vm";
    }
}
