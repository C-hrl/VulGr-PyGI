
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.UnknownHandlerConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class DefaultUnknownHandlerManager implements UnknownHandlerManager {

    private Container container;

    protected ArrayList<UnknownHandler> unknownHandlers;

    @Inject
    public void setContainer(Container container) {
        this.container = container;
        try {
            build();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }

    
    protected void build() throws Exception {
        Configuration configuration = container.getInstance(Configuration.class);
        ObjectFactory factory = container.getInstance(ObjectFactory.class);

        if (configuration != null && container != null) {
            List<UnknownHandlerConfig> unkownHandlerStack = configuration.getUnknownHandlerStack();
            unknownHandlers = new ArrayList<>();

            if (unkownHandlerStack != null && !unkownHandlerStack.isEmpty()) {
                
                for (UnknownHandlerConfig unknownHandlerConfig : unkownHandlerStack) {
                    UnknownHandler uh = factory.buildUnknownHandler(unknownHandlerConfig.getName(), new HashMap<String, Object>());
                    unknownHandlers.add(uh);
                }
            } else {
                
                Set<String> unknownHandlerNames = container.getInstanceNames(UnknownHandler.class);
                for (String unknownHandlerName : unknownHandlerNames) {
                    UnknownHandler uh = container.getInstance(UnknownHandler.class, unknownHandlerName);
                    unknownHandlers.add(uh);
                }
            }
        }
    }

    
    public Result handleUnknownResult(ActionContext actionContext, String actionName, ActionConfig actionConfig, String resultCode) {
        for (UnknownHandler unknownHandler : unknownHandlers) {
            Result result = unknownHandler.handleUnknownResult(actionContext, actionName, actionConfig, resultCode);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    
    public Object handleUnknownMethod(Object action, String methodName) throws NoSuchMethodException {
        for (UnknownHandler unknownHandler : unknownHandlers) {
            Object result = unknownHandler.handleUnknownActionMethod(action, methodName);
            if (result != null) {
                return result;
            }
        }

        if (unknownHandlers.isEmpty()) {
            throw new NoSuchMethodException(String.format("No UnknownHandlers defined to handle method [%s]", methodName));
        } else {
            throw new NoSuchMethodException(String.format("None of defined UnknownHandlers can handle method [%s]", methodName));
        }
    }

    
    public ActionConfig handleUnknownAction(String namespace, String actionName) {
        for (UnknownHandler unknownHandler : unknownHandlers) {
            ActionConfig result = unknownHandler.handleUnknownAction(namespace, actionName);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public boolean hasUnknownHandlers() {
        return unknownHandlers != null && !unknownHandlers.isEmpty();
    }

    public List<UnknownHandler> getUnknownHandlers() {
        return unknownHandlers;
    }

}
