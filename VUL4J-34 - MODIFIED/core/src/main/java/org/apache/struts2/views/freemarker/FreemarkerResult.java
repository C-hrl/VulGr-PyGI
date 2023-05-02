

package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.result.StrutsResultSupport;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;



public class FreemarkerResult extends StrutsResultSupport {

    private static final long serialVersionUID = -3778230771704661631L;

    private static final Logger LOG = LogManager.getLogger(FreemarkerResult.class);

    protected ActionInvocation invocation;
    protected Configuration configuration;
    protected ObjectWrapper wrapper;
    protected FreemarkerManager freemarkerManager;
    private Writer writer;
    private boolean writeIfCompleted = false;
    
    protected String location;
    private String pContentType = "text/html";
    private static final String PARENT_TEMPLATE_WRITER = FreemarkerResult.class.getName() +  ".parentWriter";

    public FreemarkerResult() {
        super();
    }

    public FreemarkerResult(String location) {
        super(location);
    }
    
    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }

    public void setContentType(String aContentType) {
        pContentType = aContentType;
    }

    
    public String getContentType() {
        return pContentType;
    }

    
    public void doExecute(String locationArg, ActionInvocation invocation) throws IOException, TemplateException {
        this.location = locationArg;
        this.invocation = invocation;
        this.configuration = getConfiguration();
        this.wrapper = getObjectWrapper();

        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);

        String absoluteLocation;
        if (location.startsWith("/")) {
            absoluteLocation = location; 
        } else { 
            String namespace = invocation.getProxy().getNamespace();
            if (namespace == null || namespace.length() == 0 || namespace.equals("/")) {
                absoluteLocation = "/" + location;
            } else if (namespace.startsWith("/")) {
                absoluteLocation = namespace + "/" + location;
            } else {
                absoluteLocation = "/" + namespace + "/" + location;
            }
        }

        Template template = configuration.getTemplate(absoluteLocation, deduceLocale());
        TemplateModel model = createModel();

        
        if (preTemplateProcess(template, model)) {
            try {
                
                Writer writer = getWriter();
                if (isWriteIfCompleted() || configuration.getTemplateExceptionHandler() == TemplateExceptionHandler.RETHROW_HANDLER) {
                    CharArrayWriter parentCharArrayWriter = (CharArrayWriter) req.getAttribute(PARENT_TEMPLATE_WRITER);
                    boolean isTopTemplate = false;
                    if (isTopTemplate = (parentCharArrayWriter == null)) {
                        
                        parentCharArrayWriter = new CharArrayWriter();
                        
                        req.setAttribute(PARENT_TEMPLATE_WRITER, parentCharArrayWriter);
                    }

                    try {
                        template.process(model, parentCharArrayWriter);

                        if (isTopTemplate) {
                            parentCharArrayWriter.flush();
                            parentCharArrayWriter.writeTo(writer);
                        }
                    } catch (TemplateException e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error("Error processing Freemarker result!", e);
                        }
                        throw e;
                    } catch (IOException e) {
                        if (LOG.isErrorEnabled()){
                            LOG.error("Error processing Freemarker result!", e);
                        }
                        throw e;
                    } finally {
                        if (isTopTemplate && parentCharArrayWriter != null) {
                            req.removeAttribute(PARENT_TEMPLATE_WRITER);
                            parentCharArrayWriter.close();
                        }
                    }
                } else {
                    template.process(model, writer);
                }
            } finally {
                
                postTemplateProcess(template, model);
            }
        }
    }

    
    protected Configuration getConfiguration() throws TemplateException {
        return freemarkerManager.getConfiguration(ServletActionContext.getServletContext());
    }

    
    protected ObjectWrapper getObjectWrapper() {
        return configuration.getObjectWrapper();
    }


    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    
    protected Writer getWriter() throws IOException {
        if(writer != null) {
            return writer;
        }
        return ServletActionContext.getResponse().getWriter();
    }

    
    protected TemplateModel createModel() throws TemplateModelException {
        ServletContext servletContext = ServletActionContext.getServletContext();
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        ValueStack stack = ServletActionContext.getContext().getValueStack();

        Object action = null;
        if(invocation!= null ) action = invocation.getAction(); 
        return freemarkerManager.buildTemplateModel(stack, action, servletContext, request, response, wrapper);
    }

    
    protected Locale deduceLocale() {
        if (invocation.getAction() instanceof LocaleProvider) {
            return ((LocaleProvider) invocation.getAction()).getLocale();
        } else {
            return configuration.getLocale();
        }
    }

    
    protected void postTemplateProcess(Template template, TemplateModel model) throws IOException {
    }

    
    protected boolean preTemplateProcess(Template template, TemplateModel model) throws IOException {
        Object attrContentType = template.getCustomAttribute("content_type");

        HttpServletResponse response = ServletActionContext.getResponse();
        if (response.getContentType() == null) {
            if (attrContentType != null) {
                response.setContentType(attrContentType.toString());
            } else {
                String contentType = getContentType();

                if (contentType == null) {
                    contentType = "text/html";
                }

                String encoding = template.getEncoding();

                if (encoding != null) {
                    contentType = contentType + "; charset=" + encoding;
                }

                response.setContentType(contentType);
            }
        } else if(isInsideActionTag()){
             
            response.setContentType(response.getContentType());
        }

        return true;
    }

    private boolean isInsideActionTag() {
        Object attribute = ServletActionContext.getRequest().getAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION);
        return (Boolean) ObjectUtils.defaultIfNull(attribute, Boolean.FALSE);
    }

    
    public boolean isWriteIfCompleted() {
        return writeIfCompleted;
    }

    
    public void setWriteIfCompleted(boolean writeIfCompleted) {
        this.writeIfCompleted = writeIfCompleted;
    }
}
