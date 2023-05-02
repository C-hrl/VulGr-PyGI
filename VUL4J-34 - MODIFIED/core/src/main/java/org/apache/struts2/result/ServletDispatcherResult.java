

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.util.Map;



public class ServletDispatcherResult extends StrutsResultSupport {

    private static final long serialVersionUID = -1970659272360685627L;

    private static final Logger LOG = LogManager.getLogger(ServletDispatcherResult.class);

    private UrlHelper urlHelper;

    public ServletDispatcherResult() {
        super();
    }

    public ServletDispatcherResult(String location) {
        super(location);
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    
    public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        LOG.debug("Forwarding to location: {}", finalLocation);

        PageContext pageContext = ServletActionContext.getPageContext();

        if (pageContext != null) {
            pageContext.include(finalLocation);
        } else {
            HttpServletRequest request = ServletActionContext.getRequest();
            HttpServletResponse response = ServletActionContext.getResponse();
            RequestDispatcher dispatcher = request.getRequestDispatcher(finalLocation);

            
            
            if (StringUtils.isNotEmpty(finalLocation) && finalLocation.indexOf("?") > 0) {
                String queryString = finalLocation.substring(finalLocation.indexOf("?") + 1);
                Map<String, Object> parameters = getParameters(invocation);
                Map<String, Object> queryParams = urlHelper.parseQueryString(queryString, true);
                if (queryParams != null && !queryParams.isEmpty())
                    parameters.putAll(queryParams);
            }

            
            if (dispatcher == null) {
                response.sendError(404, "result '" + finalLocation + "' not found");
                return;
            }

            
            Boolean insideActionTag = (Boolean) ObjectUtils.defaultIfNull(request.getAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION), Boolean.FALSE);

            
            
            
            if (!insideActionTag && !response.isCommitted() && (request.getAttribute("javax.servlet.include.servlet_path") == null)) {
                request.setAttribute("struts.view_uri", finalLocation);
                request.setAttribute("struts.request_uri", request.getRequestURI());

                dispatcher.forward(request, response);
            } else {
                dispatcher.include(request, response);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getParameters(ActionInvocation invocation) {
        return (Map<String, Object>) invocation.getInvocationContext().getContextMap().get("parameters");
    }

}
