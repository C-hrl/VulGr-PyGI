

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import static javax.servlet.http.HttpServletResponse.SC_FOUND;


public class ServletRedirectResult extends StrutsResultSupport implements ReflectionExceptionHandler {

    private static final long serialVersionUID = 6316947346435301270L;

    private static final Logger LOG = LogManager.getLogger(ServletRedirectResult.class);

    protected boolean prependServletContext = true;
    protected ActionMapper actionMapper;
    protected int statusCode = SC_FOUND;
    protected boolean suppressEmptyParameters = false;
    protected Map<String, Object> requestParameters = new LinkedHashMap<>();
    protected String anchor;

    private UrlHelper urlHelper;

    public ServletRedirectResult() {
        super();
    }

    public ServletRedirectResult(String location) {
        this(location, null);
    }

    public ServletRedirectResult(String location, String anchor) {
        super(location);
        this.anchor = anchor;
    }

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    public void setStatusCode(int code) {
        this.statusCode = code;
    }

    
    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    
    public void setPrependServletContext(boolean prependServletContext) {
        this.prependServletContext = prependServletContext;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        if (anchor != null) {
            anchor = conditionalParse(anchor, invocation);
        }
        super.execute(invocation);
    }

    
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);

        if (isPathUrl(finalLocation)) {
            if (!finalLocation.startsWith("/")) {
                ActionMapping mapping = actionMapper.getMapping(request, Dispatcher.getInstance().getConfigurationManager());
                String namespace = null;
                if (mapping != null) {
                    namespace = mapping.getNamespace();
                }

                if ((namespace != null) && (namespace.length() > 0) && (!"/".equals(namespace))) {
                    finalLocation = namespace + "/" + finalLocation;
                } else {
                    finalLocation = "/" + finalLocation;
                }
            }

            
            if (prependServletContext && (request.getContextPath() != null) && (request.getContextPath().length() > 0)) {
                finalLocation = request.getContextPath() + finalLocation;
            }
        }
        ResultConfig resultConfig = invocation.getProxy().getConfig().getResults().get(invocation.getResultCode());
        if (resultConfig != null) {
            Map<String, String> resultConfigParams = resultConfig.getParams();

            List<String> prohibitedResultParams = getProhibitedResultParams();
            for (Map.Entry<String, String> e : resultConfigParams.entrySet()) {
                if (!prohibitedResultParams.contains(e.getKey())) {
                    Collection<String> values = conditionalParseCollection(e.getValue(), invocation, suppressEmptyParameters);
                    if (!suppressEmptyParameters || !values.isEmpty()) {
                        requestParameters.put(e.getKey(), values);
                    }
                }
            }
        }

        StringBuilder tmpLocation = new StringBuilder(finalLocation);
        urlHelper.buildParametersString(requestParameters, tmpLocation, "&");

        
        if (anchor != null) {
            tmpLocation.append('#').append(anchor);
        }

        finalLocation = response.encodeRedirectURL(tmpLocation.toString());

        LOG.debug("Redirecting to finalLocation: {}", finalLocation);

        sendRedirect(response, finalLocation);
    }

    protected List<String> getProhibitedResultParams() {
        return Arrays.asList(
                DEFAULT_PARAM,
                "namespace",
                "method",
                "encode",
                "parse",
                "location",
                "prependServletContext",
                "suppressEmptyParameters",
                "anchor",
                "statusCode"
        );
    }

    
    protected void sendRedirect(HttpServletResponse response, String finalLocation) throws IOException {
        if (SC_FOUND == statusCode) {
            response.sendRedirect(finalLocation);
        } else {
            response.setStatus(statusCode);
            response.setHeader("Location", finalLocation);
            response.getWriter().write(finalLocation);
            response.getWriter().close();
        }

    }

    
    protected boolean isPathUrl(String url) {
        try {
            String rawUrl = url;
            if (url.contains("?")) {
                rawUrl = url.substring(0, url.indexOf("?"));
            }
            URI uri = URI.create(rawUrl.replaceAll(" ", "%20"));
            if (uri.isAbsolute()) {
                URL validUrl = uri.toURL();
                LOG.debug("[{}] is full url, not a path", url);
                return validUrl.getProtocol() == null;
            } else {
                LOG.debug("[{}] isn't absolute URI, assuming it's a path", url);
                return true;
            }
        } catch (IllegalArgumentException e) {
            LOG.debug("[{}] isn't a valid URL, assuming it's a path", url, e);
            return true;
        } catch (MalformedURLException e) {
            LOG.debug("[{}] isn't a valid URL, assuming it's a path", url, e);
            return true;
        }
    }

    
    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
    }

    
    public ServletRedirectResult addParameter(String key, Object value) {
        requestParameters.put(key, String.valueOf(value));
        return this;
    }

    public void handle(ReflectionException ex) {
        
        if (LOG.isDebugEnabled()) {
            LOG.debug(ex.getMessage(), ex);
        }
    }

}
