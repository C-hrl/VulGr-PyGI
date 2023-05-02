package org.apache.struts2.dispatcher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface DispatcherErrorHandler {

    
    public void init(ServletContext ctx);

    
    public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e);

}
