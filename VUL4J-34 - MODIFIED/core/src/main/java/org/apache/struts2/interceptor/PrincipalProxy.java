

package org.apache.struts2.interceptor;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;


public interface PrincipalProxy {

    
    boolean isUserInRole(String role);

    
    Principal getUserPrincipal();

    
    String getRemoteUser();

    
    boolean isRequestSecure();

}
