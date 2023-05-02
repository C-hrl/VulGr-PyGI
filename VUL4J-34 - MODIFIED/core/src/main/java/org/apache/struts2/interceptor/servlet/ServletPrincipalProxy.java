

package org.apache.struts2.interceptor.servlet;

import org.apache.struts2.interceptor.PrincipalProxy;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


public class ServletPrincipalProxy implements PrincipalProxy {
    private HttpServletRequest request;

    
    public ServletPrincipalProxy(HttpServletRequest request) {
        this.request = request;
    }

    
    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    
    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    
    public boolean isRequestSecure() {
        return request.isSecure();
    }

}
