
package org.apache.struts2.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface StaticContentLoader {

    
    public boolean canHandle(String path);

    
    public abstract void setHostConfig(HostConfig filterConfig);

    
    public abstract void findStaticResource(String path, HttpServletRequest request, HttpServletResponse response)
            throws IOException;

}
