
package org.apache.struts2.dispatcher.listener;

import org.apache.struts2.dispatcher.HostConfig;

import javax.servlet.ServletContext;
import java.util.Iterator;
import java.util.Collections;


public class ListenerHostConfig implements HostConfig {
    private ServletContext servletContext;

    public ListenerHostConfig(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getInitParameter(String key) {
        return null;
    }

    public Iterator<String> getInitParameterNames() {
        return Collections.<String>emptyList().iterator();
    }

    public ServletContext getServletContext() {
        return servletContext;  
    }
}
