
package org.apache.struts2.dispatcher;

import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.RequestUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ExecuteOperations {

    private Dispatcher dispatcher;

    public ExecuteOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    
    public boolean executeStaticResourceRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        
        String resourcePath = RequestUtils.getServletPath(request);

        if ("".equals(resourcePath) && null != request.getPathInfo()) {
            resourcePath = request.getPathInfo();
        }

        StaticContentLoader staticResourceLoader = dispatcher.getContainer().getInstance(StaticContentLoader.class);
        if (staticResourceLoader.canHandle(resourcePath)) {
            staticResourceLoader.findStaticResource(resourcePath, request, response);
            
            return true;

        } else {
            
            return false;
        }
    }

    
    public void executeAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping) throws ServletException {
        dispatcher.serviceAction(request, response, mapping);
    }
}
