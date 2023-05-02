

package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.util.VelocityStrutsUtil;
import org.apache.struts2.views.TagLibraryDirectiveProvider;
import org.apache.struts2.views.jsp.ui.OgnlTool;
import org.apache.struts2.views.util.ContextUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;



public class VelocityManager {
    private static final Logger LOG = LogManager.getLogger(VelocityManager.class);
    public static final String STRUTS = "struts";
    private ObjectFactory objectFactory;

    public static final String KEY_VELOCITY_STRUTS_CONTEXT = ".KEY_velocity.struts2.context";

    
    public static final String PARENT = "parent";

    
    public static final String TAG = "tag";

    private VelocityEngine velocityEngine;

    
    protected ToolboxManager toolboxManager = null;
    private String toolBoxLocation;


    
    private String[] chainedContextNames;

    private Properties velocityProperties;

    private String customConfigFile;

    private List<TagLibraryDirectiveProvider> tagLibraries;

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setContainer(Container container) {
        List<TagLibraryDirectiveProvider> list = new ArrayList<>();
        Set<String> prefixes = container.getInstanceNames(TagLibraryDirectiveProvider.class);
        for (String prefix : prefixes) {
            list.add(container.getInstance(TagLibraryDirectiveProvider.class, prefix));
        }
        this.tagLibraries = Collections.unmodifiableList(list);
    }

    
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    
    public Context createContext(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        Context result = null;
        VelocityContext[] chainedContexts = prepareChainedContexts(req, res, stack.getContext());
        StrutsVelocityContext context = new StrutsVelocityContext(chainedContexts, stack);
        Map standardMap = ContextUtil.getStandardContext(stack, req, res);
        for (Iterator iterator = standardMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            context.put((String) entry.getKey(), entry.getValue());
        }
        context.put(STRUTS, new VelocityStrutsUtil(velocityEngine, context, stack, req, res));


        ServletContext ctx = null;
        try {
            ctx = ServletActionContext.getServletContext();
        } catch (NullPointerException npe) {
            
            LOG.debug("internal toolbox context ignored");
        }

        if (toolboxManager != null && ctx != null) {
            ChainedContext chained = new ChainedContext(context, velocityEngine, req, res, ctx);
            chained.setToolbox(toolboxManager.getToolbox(chained));
            result = chained;
        } else {
            result = context;
        }

        req.setAttribute(KEY_VELOCITY_STRUTS_CONTEXT, result);
        return result;
    }

    
    protected VelocityContext[] prepareChainedContexts(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Map extraContext) {
        if (this.chainedContextNames == null) {
            return null;
        }
        List contextList = new ArrayList();
        for (int i = 0; i < chainedContextNames.length; i++) {
            String className = chainedContextNames[i];
            try {
                VelocityContext velocityContext = (VelocityContext) objectFactory.buildBean(className, null);
                contextList.add(velocityContext);
            } catch (Exception e) {
                LOG.warn("Warning. {} caught while attempting to instantiate a chained VelocityContext, {} -- skipping", e.getClass().getName(), className);
            }
        }
        if (contextList.size() > 0) {
            VelocityContext[] extraContexts = new VelocityContext[contextList.size()];
            contextList.toArray(extraContexts);
            return extraContexts;
        } else {
            return null;
        }
    }

    
    public synchronized void init(ServletContext context) {
        if (velocityEngine == null) {
            velocityEngine = newVelocityEngine(context);
        }
        this.initToolbox(context);
    }

    
    public Properties loadConfiguration(ServletContext context) {
        if (context == null) {
            String gripe = "Error attempting to create a loadConfiguration from a null ServletContext!";
            LOG.error(gripe);
            throw new IllegalArgumentException(gripe);
        }

        Properties properties = new Properties();

        
        applyDefaultConfiguration(context, properties);


        String defaultUserDirective = properties.getProperty("userdirective");

        
        String configfile;

        if (customConfigFile != null) {
            configfile = customConfigFile;
        } else {
            configfile = "velocity.properties";
        }

        configfile = configfile.trim();

        InputStream in = null;
        String resourceLocation = null;

        try {
            if (context.getRealPath(configfile) != null) {
                
                String filename = context.getRealPath(configfile);

                if (filename != null) {
                    File file = new File(filename);

                    if (file.isFile()) {
                        resourceLocation = file.getCanonicalPath() + " from file system";
                        in = new FileInputStream(file);
                    }

                    
                    if (in == null) {
                        file = new File(context.getRealPath("/WEB-INF/" + configfile));

                        if (file.isFile()) {
                            resourceLocation = file.getCanonicalPath() + " from file system";
                            in = new FileInputStream(file);
                        }
                    }
                }
            }

            
            if (in == null) {
                in = VelocityManager.class.getClassLoader().getResourceAsStream(configfile);
                if (in != null) {
                    resourceLocation = configfile + " from classloader";
                }
            }

            
            if (in != null) {
                LOG.info("Initializing velocity using {}", resourceLocation);
                properties.load(in);
            }
        } catch (IOException e) {
            LOG.warn("Unable to load velocity configuration {}", resourceLocation, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        
        if (this.velocityProperties != null) {
            Iterator keys = this.velocityProperties.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                properties.setProperty(key, this.velocityProperties.getProperty(key));
            }
        }

        String userdirective = properties.getProperty("userdirective");

        if ((userdirective == null) || userdirective.trim().equals("")) {
            userdirective = defaultUserDirective;
        } else {
            userdirective = userdirective.trim() + "," + defaultUserDirective;
        }

        properties.setProperty("userdirective", userdirective);


        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Initializing Velocity with the following properties ...");

            for (Iterator iter = properties.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                String value = properties.getProperty(key);
                LOG.debug("    '{}' = '{}'", key, value);
            }
        }

        return properties;
    }

    @Inject(StrutsConstants.STRUTS_VELOCITY_CONFIGFILE)
    public void setCustomConfigFile(String val) {
        this.customConfigFile = val;
    }

    @Inject(StrutsConstants.STRUTS_VELOCITY_TOOLBOXLOCATION)
    public void setToolBoxLocation(String toolboxLocation) {
        this.toolBoxLocation = toolboxLocation;
    }

    public ToolboxManager getToolboxManager() {
        return toolboxManager;
    }

    
    @Inject(StrutsConstants.STRUTS_VELOCITY_CONTEXTS)
    public void setChainedContexts(String contexts) {
        
        StringTokenizer st = new StringTokenizer(contexts, ",");
        List<String> contextList = new ArrayList<>();

        while (st.hasMoreTokens()) {
            String classname = st.nextToken();
            contextList.add(classname);
        }
        if (contextList.size() > 0) {
            String[] chainedContexts = new String[contextList.size()];
            contextList.toArray(chainedContexts);
            this.chainedContextNames = chainedContexts;
        }
    }

    
    protected void initToolbox(ServletContext context) {
        
        if (StringUtils.isNotBlank(toolBoxLocation)) {
            toolboxManager = ServletToolboxManager.getInstance(context, toolBoxLocation);
        } else {
            Velocity.info("VelocityViewServlet: No toolbox entry in configuration.");
        }
    }




    
    protected VelocityEngine newVelocityEngine(ServletContext context) {
        if (context == null) {
            String gripe = "Error attempting to create a new VelocityEngine from a null ServletContext!";
            LOG.error(gripe);
            throw new IllegalArgumentException(gripe);
        }

        Properties p = loadConfiguration(context);

        VelocityEngine velocityEngine = new VelocityEngine();

        
        
        velocityEngine.setApplicationAttribute(ServletContext.class.getName(),
                context);

        try {
            velocityEngine.init(p);
        } catch (Exception e) {
            throw new StrutsException("Unable to instantiate VelocityEngine!", e);
        }

        return velocityEngine;
    }

    
    private void applyDefaultConfiguration(ServletContext context, Properties properties) {
        

        
        if (properties.getProperty(Velocity.RESOURCE_LOADER) == null) {
            properties.setProperty(Velocity.RESOURCE_LOADER, "strutsfile, strutsclass");
        }

        
        if (context.getRealPath("") != null) {
            properties.setProperty("strutsfile.resource.loader.description", "Velocity File Resource Loader");
            properties.setProperty("strutsfile.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            properties.setProperty("strutsfile.resource.loader.path", context.getRealPath(""));
            properties.setProperty("strutsfile.resource.loader.modificationCheckInterval", "2");
            properties.setProperty("strutsfile.resource.loader.cache", "true");
        } else {
            
            String prop = properties.getProperty(Velocity.RESOURCE_LOADER);
            if (prop.indexOf("strutsfile,") != -1) {
                prop = replace(prop, "strutsfile,", "");
            } else if (prop.indexOf(", strutsfile") != -1) {
                prop = replace(prop, ", strutsfile", "");
            } else if (prop.indexOf("strutsfile") != -1) {
                prop = replace(prop, "strutsfile", "");
            }

            properties.setProperty(Velocity.RESOURCE_LOADER, prop);
        }

        
        properties.setProperty("strutsclass.resource.loader.description", "Velocity Classpath Resource Loader");
        properties.setProperty("strutsclass.resource.loader.class", "org.apache.struts2.views.velocity.StrutsResourceLoader");
        properties.setProperty("strutsclass.resource.loader.modificationCheckInterval", "2");
        properties.setProperty("strutsclass.resource.loader.cache", "true");

        
        StringBuilder sb = new StringBuilder();

        for (TagLibraryDirectiveProvider tagLibrary : tagLibraries) {
            List<Class> directives = tagLibrary.getDirectiveClasses();
            for (Class directive : directives) {
                addDirective(sb, directive);
            }
        }

        String directives = sb.toString();

        String userdirective = properties.getProperty("userdirective");
        if ((userdirective == null) || userdirective.trim().equals("")) {
            userdirective = directives;
        } else {
            userdirective = userdirective.trim() + "," + directives;
        }

        properties.setProperty("userdirective", userdirective);
    }

    private void addDirective(StringBuilder sb, Class clazz) {
        sb.append(clazz.getName()).append(",");
    }

    private static final String replace(String string, String oldString, String newString) {
        if (string == null) {
            return null;
        }
        
        if (newString == null) {
            return string;
        }
        int i = 0;
        
        if ((i = string.indexOf(oldString, i)) >= 0) {
            
            char[] string2 = string.toCharArray();
            char[] newString2 = newString.toCharArray();
            int oLength = oldString.length();
            StringBuilder buf = new StringBuilder(string2.length);
            buf.append(string2, 0, i).append(newString2);
            i += oLength;
            int j = i;
            
            while ((i = string.indexOf(oldString, i)) > 0) {
                buf.append(string2, j, i - j).append(newString2);
                i += oLength;
                j = i;
            }
            buf.append(string2, j, string2.length - j);
            return buf.toString();
        }
        return string;
    }

    
    public Properties getVelocityProperties() {
        return velocityProperties;
    }

    
    public void setVelocityProperties(Properties velocityProperties) {
        this.velocityProperties = velocityProperties;
    }
}
