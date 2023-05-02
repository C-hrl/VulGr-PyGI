

package org.apache.struts2.views.xslt;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;



public class XSLTResult implements Result {

    private static final long serialVersionUID = 6424691441777176763L;

    
    private static final Logger LOG = LogManager.getLogger(XSLTResult.class);

    
    public static final String DEFAULT_PARAM = "stylesheetLocation";

    
    private static final Map<String, Templates> templatesCache;

    static {
        templatesCache = new HashMap<>();
    }

    

    
    protected boolean noCache;

    
    private String stylesheetLocation;

    
    private String matchingPattern;

    
    private String excludingPattern;

    
    private String exposedValue;

    
    private int status = 200;

    private String encoding = "UTF-8";

    private boolean parse;
    private AdapterFactory adapterFactory;

    public XSLTResult() {
    }

    public XSLTResult(String stylesheetLocation) {
        this();
        setStylesheetLocation(stylesheetLocation);
    }
    
    @Inject(StrutsConstants.STRUTS_XSLT_NOCACHE)
    public void setNoCache(String xsltNoCache) {
        this.noCache = BooleanUtils.toBoolean(xsltNoCache);
    }

    public void setStylesheetLocation(String location) {
        if (location == null)
            throw new IllegalArgumentException("Null location");
        this.stylesheetLocation = location;
    }

    public String getStylesheetLocation() {
        return stylesheetLocation;
    }

    public String getExposedValue() {
        return exposedValue;
    }

    public void setExposedValue(String exposedValue) {
        this.exposedValue = exposedValue;
    }

    public String getStatus() {
        return String.valueOf(status);
    }

    public void setStatus(String status) {
        try {
            this.status = Integer.valueOf(status);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Status value not number " + e.getMessage(), e);
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();
        String location = getStylesheetLocation();

        if (parse) {
            ValueStack stack = ActionContext.getContext().getValueStack();
            location = TextParseUtil.translateVariables(location, stack);
        }


        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.setStatus(status);
            response.setCharacterEncoding(encoding);
            PrintWriter writer = response.getWriter();

            
            Templates templates = null;
            Transformer transformer;
            if (location != null) {
                templates = getTemplates(location);
                transformer = templates.newTransformer();
            } else
                transformer = TransformerFactory.newInstance().newTransformer();

            transformer.setURIResolver(getURIResolver());
            transformer.setErrorListener(new ErrorListener() {

                public void error(TransformerException exception)
                        throws TransformerException {
                    throw new StrutsException("Error transforming result", exception);
                }

                public void fatalError(TransformerException exception)
                        throws TransformerException {
                    throw new StrutsException("Fatal error transforming result", exception);
                }

                public void warning(TransformerException exception)
                        throws TransformerException {
                    if (LOG.isWarnEnabled()) {
                	LOG.warn(exception.getMessage(), exception);
                    }
                }
                
            });

            String mimeType;
            if (templates == null)
                mimeType = "text/xml"; 
            else
                mimeType = templates.getOutputProperties().getProperty(OutputKeys.MEDIA_TYPE);
            if (mimeType == null) {
                
                mimeType = "text/html";
            }

            response.setContentType(mimeType);

            Object result = invocation.getAction();
            if (exposedValue != null) {
                ValueStack stack = invocation.getStack();
                result = stack.findValue(exposedValue);
            }

            Source xmlSource = getDOMSourceForStack(result);

            
            LOG.debug("xmlSource = {}", xmlSource);
            transformer.transform(xmlSource, new StreamResult(writer));

            writer.flush(); 

            LOG.debug("Time: {}ms", (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            LOG.error("Unable to render XSLT Template, '{}'", location, e);
            throw e;
        }
    }

    protected AdapterFactory getAdapterFactory() {
        if (adapterFactory == null)
            adapterFactory = new AdapterFactory();
        return adapterFactory;
    }

    protected void setAdapterFactory(AdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    
    protected URIResolver getURIResolver() {
        return new ServletURIResolver(
                ServletActionContext.getServletContext());
    }

    protected Templates getTemplates(String path) throws TransformerException, IOException {
        String pathFromRequest = ServletActionContext.getRequest().getParameter("xslt.location");

        if (pathFromRequest != null)
            path = pathFromRequest;

        if (path == null)
            throw new TransformerException("Stylesheet path is null");

        Templates templates = templatesCache.get(path);

        if (noCache || (templates == null)) {
            synchronized (templatesCache) {
                URL resource = ServletActionContext.getServletContext().getResource(path);

                if (resource == null) {
                    throw new TransformerException("Stylesheet " + path + " not found in resources.");
                }

                LOG.debug("Preparing XSLT stylesheet templates: {}", path);

                TransformerFactory factory = TransformerFactory.newInstance();
                factory.setURIResolver(getURIResolver());
                templates = factory.newTemplates(new StreamSource(resource.openStream()));
                templatesCache.put(path, templates);
            }
        }

        return templates;
    }

    protected Source getDOMSourceForStack(Object value)
            throws IllegalAccessException, InstantiationException {
        return new DOMSource(getAdapterFactory().adaptDocument("result", value) );
    }
}
