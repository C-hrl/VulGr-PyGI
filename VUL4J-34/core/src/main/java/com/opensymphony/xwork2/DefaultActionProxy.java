
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Locale;



public class DefaultActionProxy implements ActionProxy, Serializable {

    private static final long serialVersionUID = 3293074152487468527L;

    private static final Logger LOG = LogManager.getLogger(DefaultActionProxy.class);

    protected Configuration configuration;
    protected ActionConfig config;
    protected ActionInvocation invocation;
    protected UnknownHandlerManager unknownHandlerManager;
    protected String actionName;
    protected String namespace;
    protected String method;
    protected boolean executeResult;
    protected boolean cleanupContext;

    protected ObjectFactory objectFactory;

    protected ActionEventListener actionEventListener;

    private boolean methodSpecified = true;

    
    protected DefaultActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {

        this.invocation = inv;
        this.cleanupContext = cleanupContext;
        LOG.debug("Creating an DefaultActionProxy for namespace [{}] and action name [{}]", namespace, actionName);

        this.actionName = StringEscapeUtils.escapeHtml4(actionName);
        this.namespace = namespace;
        this.executeResult = executeResult;
        this.method = StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeHtml4(methodName));
    }

    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }

    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }

    @Inject
    public void setUnknownHandler(UnknownHandlerManager unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }

    @Inject(required = false)
    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }

    public Object getAction() {
        return invocation.getAction();
    }

    public String getActionName() {
        return actionName;
    }

    public ActionConfig getConfig() {
        return config;
    }

    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    public boolean getExecuteResult() {
        return executeResult;
    }

    public ActionInvocation getInvocation() {
        return invocation;
    }

    public String getNamespace() {
        return namespace;
    }

    public String execute() throws Exception {
        ActionContext nestedContext = ActionContext.getContext();
        ActionContext.setContext(invocation.getInvocationContext());

        String retCode = null;

        String profileKey = "execute: ";
        try {
            UtilTimerStack.push(profileKey);

            retCode = invocation.invoke();
        } finally {
            if (cleanupContext) {
                ActionContext.setContext(nestedContext);
            }
            UtilTimerStack.pop(profileKey);
        }

        return retCode;
    }


    public String getMethod() {
        return method;
    }

    private void resolveMethod() {
        
        
        if (StringUtils.isEmpty(this.method)) {
            this.method = config.getMethodName();
            if (StringUtils.isEmpty(this.method)) {
                this.method = ActionConfig.DEFAULT_METHOD;
            }
            methodSpecified = false;
        }
    }

    protected void prepare() {
        String profileKey = "create DefaultActionProxy: ";
        try {
            UtilTimerStack.push(profileKey);
            config = configuration.getRuntimeConfiguration().getActionConfig(namespace, actionName);

            if (config == null && unknownHandlerManager.hasUnknownHandlers()) {
                config = unknownHandlerManager.handleUnknownAction(namespace, actionName);
            }
            if (config == null) {
                throw new ConfigurationException(getErrorMessage());
            }

            resolveMethod();

            if (config.isAllowedMethod(method)) {
                invocation.init(this);
            } else {
                throw new ConfigurationException("This method: " + method + " for action " + actionName + " is not allowed!");
            }
        } finally {
            UtilTimerStack.pop(profileKey);
        }
    }

    protected String getErrorMessage() {
        if ((namespace != null) && (namespace.trim().length() > 0)) {
            return LocalizedTextUtil.findDefaultText(
                    XWorkMessages.MISSING_PACKAGE_ACTION_EXCEPTION,
                    Locale.getDefault(),
                    new String[]{namespace, actionName});
        } else {
            return LocalizedTextUtil.findDefaultText(
                    XWorkMessages.MISSING_ACTION_EXCEPTION,
                    Locale.getDefault(),
                    new String[]{actionName});
        }
    }

    public boolean isMethodSpecified() {
        return methodSpecified;
    }
}
