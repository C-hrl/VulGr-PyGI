

package org.apache.struts2.config;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Context;
import com.opensymphony.xwork2.inject.Factory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class StrutsXmlConfigurationProvider extends XmlConfigurationProvider {

    private static final Logger LOG = LogManager.getLogger(StrutsXmlConfigurationProvider.class);
    private File baseDir = null;
    private String filename;
    private String reloadKey;
    private ServletContext servletContext;

    
    public StrutsXmlConfigurationProvider(boolean errorIfMissing) {
        this("struts.xml", errorIfMissing, null);
    }

    
    public StrutsXmlConfigurationProvider(String filename, boolean errorIfMissing, ServletContext ctx) {
        super(filename, errorIfMissing);
        this.servletContext = ctx;
        this.filename = filename;
        reloadKey = "configurationReload-"+filename;
        Map<String,String> dtdMappings = new HashMap<String,String>(getDtdMappings());
        dtdMappings.put("-
        dtdMappings.put("-
        dtdMappings.put("-
        dtdMappings.put("-
        dtdMappings.put("-
        setDtdMappings(dtdMappings);
        File file = new File(filename);
        if (file.getParent() != null) {
            this.baseDir = file.getParentFile();
        }
    }
    
    
    @Override
    public void register(ContainerBuilder containerBuilder, LocatableProperties props) throws ConfigurationException {
        if (servletContext != null && !containerBuilder.contains(ServletContext.class)) {
            containerBuilder.factory(ServletContext.class, new Factory<ServletContext>() {
                public ServletContext create(Context context) throws Exception {
                    return servletContext;
                }
            });
        }
        super.register(containerBuilder, props);
    }

    
    @Override
    public void loadPackages() {
        ActionContext ctx = ActionContext.getContext();
        ctx.put(reloadKey, Boolean.TRUE);
        super.loadPackages();
    }

    
    @Override
    protected Iterator<URL> getConfigurationUrls(String fileName) throws IOException {
        URL url = null;
        if (baseDir != null) {
            url = findInFileSystem(fileName);
            if (url == null) {
                return super.getConfigurationUrls(fileName);
            }
        }
        if (url != null) {
            List<URL> list = new ArrayList<>();
            list.add(url);
            return list.iterator();
        } else {
            return super.getConfigurationUrls(fileName);
        }
    }

    protected URL findInFileSystem(String fileName) throws IOException {
        URL url = null;
        File file = new File(fileName);
        LOG.debug("Trying to load file: {}", file);

        
        if (!file.exists()) {
            file = new File(baseDir, fileName);
        }
        if (file.exists()) {
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException e) {
                throw new IOException("Unable to convert "+file+" to a URL");
            }
        }
        return url;
    }

    
    @Override
    public boolean needsReload() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.get(reloadKey) == null && super.needsReload();
        } else {
            return super.needsReload();
        }

    }
    
    public String toString() {
        return ("Struts XML configuration provider ("+filename+")");
    }
}
