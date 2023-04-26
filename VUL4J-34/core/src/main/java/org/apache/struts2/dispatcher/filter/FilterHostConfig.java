
package org.apache.struts2.dispatcher.filter;

import org.apache.struts2.util.MakeIterator;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Iterator;

import org.apache.struts2.dispatcher.HostConfig;


public class FilterHostConfig implements HostConfig {

    private FilterConfig config;

    public FilterHostConfig(FilterConfig config) {
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
