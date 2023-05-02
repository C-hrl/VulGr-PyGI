

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.reflection.ReflectionExceptionHandler;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.Arrays;
import java.util.List;


public class ServletActionRedirectResult extends ServletRedirectResult implements ReflectionExceptionHandler {

    private static final long serialVersionUID = -9042425229314584066L;

    
    public static final String DEFAULT_PARAM = "actionName";

    protected String actionName;
    protected String namespace;
    protected String method;

    public ServletActionRedirectResult() {
        super();
    }

    public ServletActionRedirectResult(String actionName) {
        this(null, actionName, null, null);
    }

    public ServletActionRedirectResult(String actionName, String method) {
        this(null, actionName, method, null);
    }

    public ServletActionRedirectResult(String namespace, String actionName, String method) {
        this(namespace, actionName, method, null);
    }

    public ServletActionRedirectResult(String namespace, String actionName, String method, String anchor) {
        super(null, anchor);
        this.namespace = namespace;
        this.actionName = actionName;
        this.method = method;
    }

    
    public void execute(ActionInvocation invocation) throws Exception {
        actionName = conditionalParse(actionName, invocation);
        if (namespace == null) {
            namespace = invocation.getProxy().getNamespace();
        } else {
            namespace = conditionalParse(namespace, invocation);
        }
        if (method == null) {
            method = "";
        } else {
            method = conditionalParse(method, invocation);
        }

        String tmpLocation = actionMapper.getUriFromActionMapping(new ActionMapping(actionName, namespace, method, null));

        setLocation(tmpLocation);

        super.execute(invocation);
    }

    
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    
    public void setMethod(String method) {
        this.method = method;
    }

    protected List<String> getProhibitedResultParams() {
        return Arrays.asList(
                DEFAULT_PARAM,
                "namespace",
                "method",
                "encode",
                "parse",
                "location",
                "prependServletContext",
                "suppressEmptyParameters",
                "anchor",
                "statusCode"
        );
    }

}
