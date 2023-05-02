
package org.apache.struts2.dispatcher;

import javax.servlet.ServletContext;
import java.util.Iterator;


public interface HostConfig {

    
    String getInitParameter(String key);

    
    Iterator<String> getInitParameterNames();

    
    ServletContext getServletContext();
}
