
package com.opensymphony.xwork2.util.finder;

import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;
import java.io.InputStream;


public interface ClassLoaderInterface {

    
    public final String CLASS_LOADER_INTERFACE = "__current_class_loader_interface";

    Class<?> loadClass(String name) throws ClassNotFoundException;

    URL getResource(String name);

    public Enumeration<URL> getResources(String name) throws IOException;

    public InputStream getResourceAsStream(String name) throws IOException;

    ClassLoaderInterface getParent();
}
