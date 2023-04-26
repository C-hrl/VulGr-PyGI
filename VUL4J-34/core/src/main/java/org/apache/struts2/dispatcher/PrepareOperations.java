
package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class PrepareOperations {

    private static final Logger LOG = LogManager.getLogger(PrepareOperations.class);

    
    private static ThreadLocal<Boolean> devModeOverride = new InheritableThreadLocal<>();


    private Dispatcher dispatcher;
    private static final String STRUTS_ACTION_MAPPING_KEY = "struts.actionMapping";
    public static final String CLEANUP_RECURSION_COUNTER = "__cleanup_recursion_counter";

    public PrepareOperations(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    
    public ActionContext createActionContext(HttpServletRequest request, HttpServletResponse response) {
        ActionContext ctx;
        Integer counter = 1;
        Integer oldCounter = (Integer) request.getAttribute(CLEANUP_RECURSION_COUNTER);
        if (oldCounter != null) {
            counter = oldCounter + 1;
        }
        
        ActionContext oldContext = ActionContext.getContext();
        if (oldContext != null) {
            
            ctx = new ActionContext(new HashMap<>(oldContext.getContextMap()));
        } else {
            ValueStack stack = dispatcher.getContainer().getInstance(ValueStackFactory.class).createValueStack();
            stack.getContext().putAll(dispatcher.createContextMap(request, response, null));
            ctx = new ActionContext(stack.getContext());
        }
        request.setAttribute(CLEANUP_RECURSION_COUNTER, counter);
        ActionContext.setContext(ctx);
        return ctx;
    }

    
    public void cleanupRequest(HttpServletRequest request) {
        Integer counterVal = (Integer) request.getAttribute(CLEANUP_RECURSION_COUNTER);
        if (counterVal != null) {
            counterVal -= 1;
            request.setAttribute(CLEANUP_RECURSION_COUNTER, counterVal);
            if (counterVal > 0 ) {
                LOG.debug("skipping cleanup counter={}", counterVal);
                return;
            }
        }
        
        try {
            dispatcher.cleanUpRequest(request);
        } finally {
            ActionContext.setContext(null);
            Dispatcher.setInstance(null);
            devModeOverride.remove();
        }
    }

    
    public void assignDispatcherToThread() {
        Dispatcher.setInstance(dispatcher);
    }

    
    public void setEncodingAndLocale(HttpServletRequest request, HttpServletResponse response) {
        dispatcher.prepare(request, response);
    }

    
    public HttpServletRequest wrapRequest(HttpServletRequest oldRequest) throws ServletException {
        HttpServletRequest request = oldRequest;
        try {
            
            
            request = dispatcher.wrapRequest(request);
            ServletActionContext.setRequest(request);
        } catch (IOException e) {
            throw new ServletException("Could not wrap servlet request with MultipartRequestWrapper!", e);
        }
        return request;
    }

    
    public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response) {
        return findActionMapping(request, response, false);
    }

    
    public ActionMapping findActionMapping(HttpServletRequest request, HttpServletResponse response, boolean forceLookup) {
        ActionMapping mapping = (ActionMapping) request.getAttribute(STRUTS_ACTION_MAPPING_KEY);
        if (mapping == null || forceLookup) {
            try {
                mapping = dispatcher.getContainer().getInstance(ActionMapper.class).getMapping(request, dispatcher.getConfigurationManager());
                if (mapping != null) {
                    request.setAttribute(STRUTS_ACTION_MAPPING_KEY, mapping);
                }
            } catch (Exception ex) {
                dispatcher.sendError(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex);
            }
        }

        return mapping;
    }

    
    public void cleanupDispatcher() {
        if (dispatcher == null) {
            throw new StrutsException("Something is seriously wrong, Dispatcher is not initialized (null) ");
        } else {
            try {
                dispatcher.cleanup();
            } finally {
                ActionContext.setContext(null);
            }
        }
    }

    
    public boolean isUrlExcluded( HttpServletRequest request, List<Pattern> excludedPatterns ) {
        if (excludedPatterns != null) {
            String uri = RequestUtils.getUri(request);
            for ( Pattern pattern : excludedPatterns ) {
                if (pattern.matcher(uri).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    
    public static void overrideDevMode(boolean devMode) {
        devModeOverride.set(devMode);
    }

    
    public static Boolean getDevModeOverride()
    {
        return devModeOverride.get();
    }

}
