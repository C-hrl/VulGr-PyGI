

package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;



public class StrutsResourceLoader extends ClasspathResourceLoader {

    public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
        if ((name == null) || (name.length() == 0)) {
            throw new ResourceNotFoundException("No template name provided");
        }

        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        try {
            return ClassLoaderUtil.getResourceAsStream(name, StrutsResourceLoader.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e);
        }
    }
}
