

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class RolesInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(RolesInterceptor.class);

    private boolean isProperlyConfigured = true;
    
    protected List<String> allowedRoles = Collections.emptyList();
    protected List<String> disallowedRoles = Collections.emptyList();

    public void setAllowedRoles(String roles) {
        allowedRoles = stringToList(roles);
        checkRoles(allowedRoles);
    }

    public void setDisallowedRoles(String roles) {
        disallowedRoles = stringToList(roles);
        checkRoles(disallowedRoles);
    }
    
    private void checkRoles(List<String> roles){
        if (!areRolesValid(roles)){
          LOG.fatal("An unknown Role was configured: {}", roles);
          isProperlyConfigured = false;
          throw new IllegalArgumentException("An unknown role was configured: " + roles);
        }
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        if (!isProperlyConfigured) {
          throw new IllegalArgumentException("RolesInterceptor is misconfigured, check logs for erroneous configuration!");
        }
        if (!isAllowed(request, invocation.getAction())) {
            LOG.debug("Request is NOT allowed. Rejecting.");
            return handleRejection(invocation, response);
        } else {
            LOG.debug("Request is allowed. Invoking.");
            return invocation.invoke();
        }
    }

    
    protected List<String> stringToList(String val) {
        if (val != null) {
            String[] list = val.split("[ ]*,[ ]*");
            return Arrays.asList(list);
        } else {
            return Collections.emptyList();
        }
    }

    
    protected boolean isAllowed(HttpServletRequest request, Object action) {
        for (String role : disallowedRoles) {
            if (request.isUserInRole(role)) {
                LOG.debug("User role '{}' is in the disallowedRoles list.", role);
                return false;
            }
        }
  
        if (allowedRoles.isEmpty()){
            LOG.debug("The allowedRoles list is empty.");
            return true;
        }
        
        for (String role : allowedRoles) {
            if (request.isUserInRole(role)) {
                LOG.debug("User role '{}' is in the allowedRoles list.", role);
                return true;
            }
        }
        
        return false;
    }

    
    protected String handleRejection(ActionInvocation invocation, HttpServletResponse response) throws Exception {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return null;
    }
    
    
    protected boolean areRolesValid(List<String> roles){
        return true;
    }

}
