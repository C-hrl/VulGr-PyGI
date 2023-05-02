

package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.*;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.config.DefaultBeanSelectionProvider;
import org.apache.struts2.config.DefaultPropertiesProvider;
import org.apache.struts2.config.PropertiesConfigurationProvider;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.AttributeMap;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.apache.struts2.util.fs.JBossFileManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Dispatcher {

    
    private static final Logger LOG = LogManager.getLogger(Dispatcher.class);

    
    private static ThreadLocal<Dispatcher> instance = new ThreadLocal<>();

    
    private static List<DispatcherListener> dispatcherListeners = new CopyOnWriteArrayList<>();

    
    private boolean devMode;

    
    private boolean disableRequestAttributeValueStackLookup;

    
    private String defaultEncoding;

    
    private String defaultLocale;

    
    private String multipartSaveDir;

    
    private String multipartHandlerName;

    
    private static final String DEFAULT_CONFIGURATION_PATHS = "struts-default.xml,struts-plugin.xml,struts.xml";

    
    private boolean paramsWorkaroundEnabled = false;

    
    private boolean handleException;

    
    private DispatcherErrorHandler errorHandler;

    
    protected ConfigurationManager configurationManager;

    
    public static Dispatcher getInstance() {
        return instance.get();
    }

    
    public static void setInstance(Dispatcher instance) {
        Dispatcher.instance.set(instance);
    }

    
    public static void addDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.add(listener);
    }

    
    public static void removeDispatcherListener(DispatcherListener listener) {
        dispatcherListeners.remove(listener);
    }

    private ValueStackFactory valueStackFactory;

    
    protected ServletContext servletContext;
    protected Map<String, String> initParams;

    
    public Dispatcher(ServletContext servletContext, Map<String, String> initParams) {
        this.servletContext = servletContext;
        this.initParams = initParams;
    }

    
    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    
    @Inject(value=StrutsConstants.STRUTS_DISABLE_REQUEST_ATTRIBUTE_VALUE_STACK_LOOKUP, required=false)
    public void setDisableRequestAttributeValueStackLookup(String disableRequestAttributeValueStackLookup) {
        this.disableRequestAttributeValueStackLookup = BooleanUtils.toBoolean(disableRequestAttributeValueStackLookup);
    }

    
    @Inject(value=StrutsConstants.STRUTS_LOCALE, required=false)
    public void setDefaultLocale(String val) {
        defaultLocale = val;
    }

    
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setDefaultEncoding(String val) {
        defaultEncoding = val;
    }

    
    @Inject(StrutsConstants.STRUTS_MULTIPART_SAVEDIR)
    public void setMultipartSaveDir(String val) {
        multipartSaveDir = val;
    }

    @Inject(StrutsConstants.STRUTS_MULTIPART_PARSER)
    public void setMultipartHandler(String val) {
        multipartHandlerName = val;
    }

    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject(StrutsConstants.STRUTS_HANDLE_EXCEPTION)
    public void setHandleException(String handleException) {
        this.handleException = Boolean.parseBoolean(handleException);
    }

    @Inject
    public void setDispatcherErrorHandler(DispatcherErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    
    public void cleanup() {

    	
        ObjectFactory objectFactory = getContainer().getInstance(ObjectFactory.class);
        if (objectFactory == null) {
        	LOG.warn("Object Factory is null, something is seriously wrong, no clean up will be performed");
        }
        if (objectFactory instanceof ObjectFactoryDestroyable) {
            try {
                ((ObjectFactoryDestroyable)objectFactory).destroy();
            }
            catch(Exception e) {
                
                LOG.error("Exception occurred while destroying ObjectFactory [{}]", objectFactory.toString(), e);
            }
        }

        
        instance.set(null);

        
        if (!dispatcherListeners.isEmpty()) {
            for (DispatcherListener l : dispatcherListeners) {
                l.dispatcherDestroyed(this);
            }
        }

        
        Set<Interceptor> interceptors = new HashSet<>();
        Collection<PackageConfig> packageConfigs = configurationManager.getConfiguration().getPackageConfigs().values();
        for (PackageConfig packageConfig : packageConfigs) {
            for (Object config : packageConfig.getAllInterceptorConfigs().values()) {
                if (config instanceof InterceptorStackConfig) {
                    for (InterceptorMapping interceptorMapping : ((InterceptorStackConfig) config).getInterceptors()) {
                	    interceptors.add(interceptorMapping.getInterceptor());
                    }
                }
            }
        }
        for (Interceptor interceptor : interceptors) {
        	interceptor.destroy();
        }

        
        ContainerHolder.clear();

        
        ActionContext.setContext(null);

        
    	configurationManager.destroyConfiguration();
    	configurationManager = null;
    }

    private void init_FileManager() throws ClassNotFoundException {
        if (initParams.containsKey(StrutsConstants.STRUTS_FILE_MANAGER)) {
            final String fileManagerClassName = initParams.get(StrutsConstants.STRUTS_FILE_MANAGER);
            final Class<FileManager> fileManagerClass = (Class<FileManager>) Class.forName(fileManagerClassName);
            LOG.info("Custom FileManager specified: {}", fileManagerClassName);
            configurationManager.addContainerProvider(new FileManagerProvider(fileManagerClass, fileManagerClass.getSimpleName()));
        } else {
            
            configurationManager.addContainerProvider(new FileManagerProvider(JBossFileManager.class, "jboss"));
        }
        if (initParams.containsKey(StrutsConstants.STRUTS_FILE_MANAGER_FACTORY)) {
            final String fileManagerFactoryClassName = initParams.get(StrutsConstants.STRUTS_FILE_MANAGER_FACTORY);
            final Class<FileManagerFactory> fileManagerFactoryClass = (Class<FileManagerFactory>) Class.forName(fileManagerFactoryClassName);
            LOG.info("Custom FileManagerFactory specified: {}", fileManagerFactoryClassName);
            configurationManager.addContainerProvider(new FileManagerFactoryProvider(fileManagerFactoryClass));
        }
    }

    private void init_DefaultProperties() {
        configurationManager.addContainerProvider(new DefaultPropertiesProvider());
    }
    
    private void init_LegacyStrutsProperties() {
        configurationManager.addContainerProvider(new PropertiesConfigurationProvider());
    }

    private void init_TraditionalXmlConfigurations() {
        String configPaths = initParams.get("config");
        if (configPaths == null) {
            configPaths = DEFAULT_CONFIGURATION_PATHS;
        }
        String[] files = configPaths.split("\\s*[,]\\s*");
        for (String file : files) {
            if (file.endsWith(".xml")) {
                if ("xwork.xml".equals(file)) {
                    configurationManager.addContainerProvider(createXmlConfigurationProvider(file, false));
                } else {
                    configurationManager.addContainerProvider(createStrutsXmlConfigurationProvider(file, false, servletContext));
                }
            } else {
                throw new IllegalArgumentException("Invalid configuration file name");
            }
        }
    }

    protected XmlConfigurationProvider createXmlConfigurationProvider(String filename, boolean errorIfMissing) {
        return new XmlConfigurationProvider(filename, errorIfMissing);
    }

    protected XmlConfigurationProvider createStrutsXmlConfigurationProvider(String filename, boolean errorIfMissing, ServletContext ctx) {
        return new StrutsXmlConfigurationProvider(filename, errorIfMissing, ctx);
    }

    private void init_CustomConfigurationProviders() {
        String configProvs = initParams.get("configProviders");
        if (configProvs != null) {
            String[] classes = configProvs.split("\\s*[,]\\s*");
            for (String cname : classes) {
                try {
                    Class cls = ClassLoaderUtil.loadClass(cname, this.getClass());
                    ConfigurationProvider prov = (ConfigurationProvider)cls.newInstance();
                    if (prov instanceof ServletContextAwareConfigurationProvider) {
                        ((ServletContextAwareConfigurationProvider)prov).initWithContext(servletContext);
                    }
                    configurationManager.addContainerProvider(prov);
                } catch (InstantiationException e) {
                    throw new ConfigurationException("Unable to instantiate provider: "+cname, e);
                } catch (IllegalAccessException e) {
                    throw new ConfigurationException("Unable to access provider: "+cname, e);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException("Unable to locate provider class: "+cname, e);
                }
            }
        }
    }

    private void init_FilterInitParameters() {
        configurationManager.addContainerProvider(new ConfigurationProvider() {
            public void destroy() {
            }

            public void init(Configuration configuration) throws ConfigurationException {
            }

            public void loadPackages() throws ConfigurationException {
            }

            public boolean needsReload() {
                return false;
            }

            public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
                props.putAll(initParams);
            }
        });
    }

    private void init_AliasStandardObjects() {
        configurationManager.addContainerProvider(new DefaultBeanSelectionProvider());
    }

    private Container init_PreloadConfiguration() {
        Container container = getContainer();

        boolean reloadi18n = Boolean.valueOf(container.getInstance(String.class, StrutsConstants.STRUTS_I18N_RELOAD));
        LocalizedTextUtil.setReloadBundles(reloadi18n);

        boolean devMode = Boolean.valueOf(container.getInstance(String.class, StrutsConstants.STRUTS_DEVMODE));
        LocalizedTextUtil.setDevMode(devMode);

        return container;
    }

    private void init_CheckWebLogicWorkaround(Container container) {
        
        if (servletContext != null && StringUtils.contains(servletContext.getServerInfo(), "WebLogic")) {
            LOG.info("WebLogic server detected. Enabling Struts parameter access work-around.");
            paramsWorkaroundEnabled = true;
        } else {
            paramsWorkaroundEnabled = "true".equals(container.getInstance(String.class,
                    StrutsConstants.STRUTS_DISPATCHER_PARAMETERSWORKAROUND));
        }
    }

    
    public void init() {

    	if (configurationManager == null) {
    		configurationManager = createConfigurationManager(DefaultBeanSelectionProvider.DEFAULT_BEAN_NAME);
    	}

        try {
            init_FileManager();
            init_DefaultProperties(); 
            init_TraditionalXmlConfigurations(); 
            init_LegacyStrutsProperties(); 
            init_CustomConfigurationProviders(); 
            init_FilterInitParameters() ; 
            init_AliasStandardObjects() ; 

            Container container = init_PreloadConfiguration();
            container.inject(this);
            init_CheckWebLogicWorkaround(container);

            if (!dispatcherListeners.isEmpty()) {
                for (DispatcherListener l : dispatcherListeners) {
                    l.dispatcherInitialized(this);
                }
            }
            errorHandler.init(servletContext);

        } catch (Exception ex) {
            LOG.error("Dispatcher initialization failed", ex);
            throw new StrutsException(ex);
        }
    }

    protected ConfigurationManager createConfigurationManager(String name) {
        return new ConfigurationManager(name);
    }

    
    public void serviceAction(HttpServletRequest request, HttpServletResponse response, ActionMapping mapping)
            throws ServletException {

        Map<String, Object> extraContext = createContextMap(request, response, mapping);

        
        ValueStack stack = (ValueStack) request.getAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY);
        boolean nullStack = stack == null;
        if (nullStack) {
            ActionContext ctx = ActionContext.getContext();
            if (ctx != null) {
                stack = ctx.getValueStack();
            }
        }
        if (stack != null) {
            extraContext.put(ActionContext.VALUE_STACK, valueStackFactory.createValueStack(stack));
        }

        String timerKey = "Handling request from Dispatcher";
        try {
            UtilTimerStack.push(timerKey);
            String namespace = mapping.getNamespace();
            String name = mapping.getName();
            String method = mapping.getMethod();

            ActionProxy proxy = getContainer().getInstance(ActionProxyFactory.class).createActionProxy(
                    namespace, name, method, extraContext, true, false);

            request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, proxy.getInvocation().getStack());

            
            if (mapping.getResult() != null) {
                Result result = mapping.getResult();
                result.execute(proxy.getInvocation());
            } else {
                proxy.execute();
            }

            
            if (!nullStack) {
                request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
            }
        } catch (ConfigurationException e) {
            logConfigurationException(request, e);
            sendError(request, response, HttpServletResponse.SC_NOT_FOUND, e);
        } catch (Exception e) {
            if (handleException || devMode) {
                sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
            } else {
                throw new ServletException(e);
            }
        } finally {
            UtilTimerStack.pop(timerKey);
        }
    }

    
    protected void logConfigurationException(HttpServletRequest request, ConfigurationException e) {
        
        String uri = request.getRequestURI();
        if (request.getQueryString() != null) {
            uri = uri + "?" + request.getQueryString();
        }
        if (devMode) {
            LOG.error("Could not find action or result: {}", uri, e);
        } else if (LOG.isWarnEnabled()) {
            LOG.warn("Could not find action or result: {}", uri, e);
        }
    }

    
    public Map<String,Object> createContextMap(HttpServletRequest request, HttpServletResponse response,
            ActionMapping mapping) {

        
        Map requestMap = new RequestMap(request);

        
        Map params = new HashMap(request.getParameterMap());

        
        Map session = new SessionMap(request);

        
        Map application = new ApplicationMap(servletContext);

        Map<String,Object> extraContext = createContextMap(requestMap, params, session, application, request, response);

        if (mapping != null) {
            extraContext.put(ServletActionContext.ACTION_MAPPING, mapping);
        }
        return extraContext;
    }

    
    public HashMap<String,Object> createContextMap(Map requestMap,
                                    Map parameterMap,
                                    Map sessionMap,
                                    Map applicationMap,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        HashMap<String, Object> extraContext = new HashMap<>();
        extraContext.put(ActionContext.PARAMETERS, new HashMap(parameterMap));
        extraContext.put(ActionContext.SESSION, sessionMap);
        extraContext.put(ActionContext.APPLICATION, applicationMap);

        Locale locale;
        if (defaultLocale != null) {
            locale = LocalizedTextUtil.localeFromString(defaultLocale, request.getLocale());
        } else {
            locale = request.getLocale();
        }

        extraContext.put(ActionContext.LOCALE, locale);

        extraContext.put(StrutsStatics.HTTP_REQUEST, request);
        extraContext.put(StrutsStatics.HTTP_RESPONSE, response);
        extraContext.put(StrutsStatics.SERVLET_CONTEXT, servletContext);

        
        extraContext.put("request", requestMap);
        extraContext.put("session", sessionMap);
        extraContext.put("application", applicationMap);
        extraContext.put("parameters", parameterMap);

        AttributeMap attrMap = new AttributeMap(extraContext);
        extraContext.put("attr", attrMap);

        return extraContext;
    }

    
    private String getSaveDir() {
        String saveDir = multipartSaveDir.trim();

        if (saveDir.equals("")) {
            File tempdir = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        	LOG.info("Unable to find 'struts.multipart.saveDir' property setting. Defaulting to javax.servlet.context.tempdir");

            if (tempdir != null) {
                saveDir = tempdir.toString();
                setMultipartSaveDir(saveDir);
            }
        } else {
            File multipartSaveDir = new File(saveDir);

            if (!multipartSaveDir.exists()) {
                if (!multipartSaveDir.mkdirs()) {
                    String logMessage;
                    try {
                        logMessage = "Could not find create multipart save directory '" + multipartSaveDir.getCanonicalPath() + "'.";
                    } catch (IOException e) {
                        logMessage = "Could not find create multipart save directory '" + multipartSaveDir.toString() + "'.";
                    }
                    if (devMode) {
                        LOG.error(logMessage);
                    } else {
                        LOG.warn(logMessage);
                    }
                }
            }
        }

        LOG.debug("saveDir={}", saveDir);

        return saveDir;
    }

    
    public void prepare(HttpServletRequest request, HttpServletResponse response) {
        String encoding = null;
        if (defaultEncoding != null) {
            encoding = defaultEncoding;
        }
        
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            encoding = "UTF-8";
        }

        Locale locale = null;
        if (defaultLocale != null) {
            locale = LocalizedTextUtil.localeFromString(defaultLocale, request.getLocale());
        }

        if (encoding != null) {
            applyEncoding(request, encoding);
        }

        if (locale != null) {
            response.setLocale(locale);
        }

        if (paramsWorkaroundEnabled) {
            request.getParameter("foo"); 
        }
    }

    private void applyEncoding(HttpServletRequest request, String encoding) {
        try {
            if (!encoding.equals(request.getCharacterEncoding())) {
                
                
                request.setCharacterEncoding(encoding);
            }
        } catch (Exception e) {
            LOG.error("Error setting character encoding to '{}' - ignoring.", encoding, e);
        }
    }

    
    public HttpServletRequest wrapRequest(HttpServletRequest request) throws IOException {
        
        if (request instanceof StrutsRequestWrapper) {
            return request;
        }

        String content_type = request.getContentType();
        if (content_type != null && content_type.contains("multipart/form-data")) {
            MultiPartRequest mpr = getMultiPartRequest();
            LocaleProvider provider = getContainer().getInstance(LocaleProvider.class);
            request = new MultiPartRequestWrapper(mpr, request, getSaveDir(), provider, disableRequestAttributeValueStackLookup);
        } else {
            request = new StrutsRequestWrapper(request, disableRequestAttributeValueStackLookup);
        }

        return request;
    }

    
    protected MultiPartRequest getMultiPartRequest() {
        MultiPartRequest mpr = null;
        
        Set<String> multiNames = getContainer().getInstanceNames(MultiPartRequest.class);
        for (String multiName : multiNames) {
            if (multiName.equals(multipartHandlerName)) {
                mpr = getContainer().getInstance(MultiPartRequest.class, multiName);
            }
        }
        if (mpr == null ) {
            mpr = getContainer().getInstance(MultiPartRequest.class);
        }
        return mpr;
    }

    
    public void cleanUpRequest(HttpServletRequest request) {
        ContainerHolder.clear();
        if (!(request instanceof MultiPartRequestWrapper)) {
            return;
        }
        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) request;
        multiWrapper.cleanUp();
    }

    
    public void sendError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
        errorHandler.handleError(request, response, code, e);
    }

    
    public void cleanUpAfterInit() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Cleaning up resources used to init Dispatcher");
        }
        ContainerHolder.clear();
    }

    
    public static class Locator {
        public Location getLocation(Object obj) {
            Location loc = LocationUtils.getLocation(obj);
            if (loc == null) {
                return Location.UNKNOWN;
            }
            return loc;
        }
    }

    
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    
    public Container getContainer() {
        if (ContainerHolder.get() != null) {
            return ContainerHolder.get();
        }
        ConfigurationManager mgr = getConfigurationManager();
        if (mgr == null) {
            throw new IllegalStateException("The configuration manager shouldn't be null");
        } else {
            Configuration config = mgr.getConfiguration();
            if (config == null) {
                throw new IllegalStateException("Unable to load configuration");
            } else {
                Container container = config.getContainer();
                ContainerHolder.store(container);
                return container;
            }
        }
    }

}
