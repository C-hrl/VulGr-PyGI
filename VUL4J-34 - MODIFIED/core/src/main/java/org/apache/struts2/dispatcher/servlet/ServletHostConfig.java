
package org.apache.struts2.dispatcher.servlet;

import org.apache.struts2.util.MakeIterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Iterator;

import org.apache.struts2.dispatcher.HostConfig;


public class ServletHostConfig implements HostConfig {
    private ServletConfig config;

    public ServletHostConfig(ServletConfig config) {
        this.config = config;
    }
    public String getInitParameter(String key) {
        return config.getInitParameter(key);
    }

    public Iterator<String> getInitParameterNames() {
        return MakeIterator.convert(config.getInitParameterNames());
    }

    public ServletContext getServletContext() {
        return config.getServletContext();
    }
}