

package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.RequestMap;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.TagUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


@StrutsTag(name="action", tldTagClass="org.apache.struts2.views.jsp.ActionTag", description="Execute an action from within a view")
public class ActionComponent extends ContextBean {
    private static final Logger LOG = LogManager.getLogger(ActionComponent.class);

    protected HttpServletResponse res;
    protected HttpServletRequest req;

    protected ValueStackFactory valueStackFactory;
    protected ActionProxyFactory actionProxyFactory;
    protected ActionProxy proxy;
    protected String name;
    protected String namespace;
    protected boolean executeResult;
    protected boolean ignoreContextParams;
    protected boolean flush = true;
    protected boolean rethrowException;

    public ActionComponent(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
    }

    
    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }
    
    @Inject
    public void setValueStackFactory(ValueStackFactory valueStackFactory) {
        this.valueStackFactory = valueStackFactory;
    }

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    public boolean end(Writer writer, String body) {
        boolean end = super.end(writer, "", false);
        try {
            if (flush) {
                try {
                    writer.flush();
                } catch (IOException e) {
                	LOG.warn("error while trying to flush writer ", e);
                }
            }
            executeAction();

            if ((getVar() != null) && (proxy != null)) {
                getStack().setValue("#attr['" + getVar() + "']", proxy.getAction());
            }
        } finally {
            popComponentStack();
        }
        return end;
    }

    protected Map createExtraContext() {
        Map newParams = createParametersForContext();

        ActionContext ctx = new ActionContext(stack.getContext());
        PageContext pageContext = (PageContext) ctx.get(ServletActionContext.PAGE_CONTEXT);
        Map session = ctx.getSession();
        Map application = ctx.getApplication();

        Dispatcher du = Dispatcher.getInstance();
        Map<String, Object> extraContext = du.createContextMap(new RequestMap(req),
                newParams,
                session,
                application,
                req,
                res);

        ValueStack newStack = valueStackFactory.createValueStack(stack);
        extraContext.put(ActionContext.VALUE_STACK, newStack);

        
        extraContext.put(ServletActionContext.PAGE_CONTEXT, pageContext);

        return extraContext;
    }

    
    protected Map<String,String[]> createParametersForContext() {
        Map parentParams = null;

        if (!ignoreContextParams) {
            parentParams = new ActionContext(getStack().getContext()).getParameters();
        }

        Map<String, String[]> newParams = (parentParams != null)
                ? new HashMap<String, String[]>(parentParams)
                : new HashMap<String, String[]>();

        if (parameters != null) {
            Map<String, String[]> params = new HashMap<>();
            for (Object o : parameters.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                Object val = entry.getValue();
                if (val.getClass().isArray() && String.class == val.getClass().getComponentType()) {
                    params.put(key, (String[])val);
                } else {
                    params.put(key, new String[]{val.toString()});
                }
            }
            newParams.putAll(params);
        }
        return newParams;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    
    protected void executeAction() {
        String actualName = findString(name, "name", "Action name is required. Example: updatePerson");

        if (actualName == null) {
            throw new StrutsException("Unable to find value for name " + name);
        }

        
        final String actionName;
        final String methodName;

        ActionMapping mapping = actionMapper.getMappingFromActionName(actualName);
        actionName = mapping.getName();
        methodName = mapping.getMethod();

        String namespace;

        if (this.namespace == null) {
            namespace = TagUtils.buildNamespace(actionMapper, getStack(), req);
        } else {
            namespace = findString(this.namespace);
        }

        
        ValueStack stack = getStack();
        
        ActionInvocation inv = ActionContext.getContext().getActionInvocation();
        try {

            proxy = actionProxyFactory.createActionProxy(namespace, actionName, methodName, createExtraContext(), executeResult, true);
            
            req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, proxy.getInvocation().getStack());
            req.setAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION, Boolean.TRUE);
            proxy.execute();

        } catch (Exception e) {
            String message = "Could not execute action: " + namespace + "/" + actualName;
            LOG.error(message, e);
            if (rethrowException) {
                throw new StrutsException(message, e);
            }
        } finally {
            req.removeAttribute(StrutsStatics.STRUTS_ACTION_TAG_INVOCATION);
            
            req.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);
            if (inv != null) {
                ActionContext.getContext().setActionInvocation(inv);
            }
        }

        if ((getVar() != null) && (proxy != null)) {
            putInContext(proxy.getAction());
        }
    }

    @StrutsTagAttribute(required=true,description="Name of the action to be executed (without the extension suffix eg. .action)")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Namespace for action to call", defaultValue="namespace from where tag is used")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @StrutsTagAttribute(description="Whether the result of this action (probably a view) should be executed/rendered", type="Boolean", defaultValue="false")
    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    @StrutsTagAttribute(description="Whether the request parameters are to be included when the action is invoked", type="Boolean", defaultValue="false")
    public void setIgnoreContextParams(boolean ignoreContextParams) {
        this.ignoreContextParams = ignoreContextParams;
    }

    @StrutsTagAttribute(description="Whether the writer should be flush upon end of action component tag, default to true", type="Boolean", defaultValue="true")
    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    @StrutsTagAttribute(description="Whether an exception should be rethrown, if the target action throws an exception", type="Boolean", defaultValue="false")
    public void setRethrowException(boolean rethrowException) {
        this.rethrowException = rethrowException;
    }
}
