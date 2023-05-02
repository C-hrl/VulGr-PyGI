

package org.apache.struts2.views.freemarker;

import java.net.URL;

import com.opensymphony.xwork2.util.ClassLoaderUtil;

import freemarker.cache.URLTemplateLoader;


public class StrutsClassTemplateLoader extends URLTemplateLoader {
    protected URL getURL(String name) {
        return ClassLoaderUtil.getResource(name, getClass());
    }
}
