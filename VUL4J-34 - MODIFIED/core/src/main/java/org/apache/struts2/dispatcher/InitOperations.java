
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.struts2.StrutsConstants;

import java.util.*;
import java.util.regex.Pattern;


public class InitOperations {

    public InitOperations() {
    }

    
    @Deprecated
    public void initLogging( HostConfig filterConfig ) {
        String factoryName = filterConfig.getInitParameter("loggerFactory");
        if (factoryName != null) {
            try {
                Class cls = ClassLoaderUtil.loadClass(factoryName, this.getClass());
                LoggerFactory fac = (LoggerFactory) cls.newInstance();
                LoggerFactory.setLoggerFactory(fac);
            } catch ( InstantiationException e ) {
                System.err.println("Unable to instantiate logger factory: " + factoryName + ", using default");
                e.printStackTrace();
            } catch ( IllegalAccessException e ) {
                System.err.println("Unable to access logger factory: " + factoryName + ", using default");
                e.printStackTrace();
            } catch ( ClassNotFoundException e ) {
                System.err.println("Unable to locate logger factory class: " + factoryName + ", using default");
                e.printStackTrace();
            }
        }
    }

    
    public Dispatcher initDispatcher( HostConfig filterConfig ) {
        Dispatcher dispatcher = createDispatcher(filterConfig);
        dispatcher.init();
        return dispatcher;
    }

    
    public StaticContentLoader initStaticContentLoader( HostConfig filterConfig, Dispatcher dispatcher ) {
        StaticContentLoader loader = dispatcher.getContainer().getInstance(StaticContentLoader.class);
        loader.setHostConfig(filterConfig);
        return loader;
    }

    
    public Dispatcher findDispatcherOnThread() {
        Dispatcher dispatcher = Dispatcher.getInstance();
        if (dispatcher == null) {
            throw new IllegalStateException("Must have the StrutsPrepareFilter execute before this one");
        }
        return dispatcher;
    }

    
    private Dispatcher createDispatcher( HostConfig filterConfig ) {
        Map<String, String> params = new HashMap<>();
        for ( Iterator e = filterConfig.getInitParameterNames(); e.hasNext(); ) {
            String name = (String) e.next();
            String value = filterConfig.getInitParameter(name);
            params.put(name, value);
        }
        return new Dispatcher(filterConfig.getServletContext(), params);
    }

    public void cleanup() {
        ActionContext.setContext(null);
    }

    
    public List<Pattern> buildExcludedPatternsList( Dispatcher dispatcher ) {
        return buildExcludedPatternsList(dispatcher.getContainer().getInstance(String.class, StrutsConstants.STRUTS_ACTION_EXCLUDE_PATTERN));
    }
            
    private List<Pattern> buildExcludedPatternsList( String patterns ) {
        if (null != patterns && patterns.trim().length() != 0) {
            List<Pattern> list = new ArrayList<>();
            String[] tokens = patterns.split(",");
            for ( String token : tokens ) {
                list.add(Pattern.compile(token.trim()));
            }
            return Collections.unmodifiableList(list);
        } else {
            return null;
        }
    }

}
