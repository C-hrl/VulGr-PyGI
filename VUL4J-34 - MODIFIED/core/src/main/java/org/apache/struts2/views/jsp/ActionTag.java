

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ActionComponent;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ActionTag extends ContextBeanTag {

    private static final long serialVersionUID = -5384167073331678855L;

    protected String name;
    protected String namespace;
    protected boolean executeResult;
    protected boolean ignoreContextParams;
    protected boolean flush = true;
    protected boolean rethrowException;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new ActionComponent(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ActionComponent action = (ActionComponent) component;
        action.setName(name);
        action.setNamespace(namespace);
        action.setExecuteResult(executeResult);
        action.setIgnoreContextParams(ignoreContextParams);
        action.setFlush(flush);
        action.setRethrowException(rethrowException);
    }

    protected void addParameter(String name, Object value) {
        ActionComponent ac = (ActionComponent) component;
        ac.addParameter(name, value);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    public void setIgnoreContextParams(boolean ignoreContextParams) {
        this.ignoreContextParams = ignoreContextParams;
    }

    public void setFlush(boolean flush) {
        this.flush = flush;
    }

    public boolean getFlush() {
        return this.flush;
    }

    public void setRethrowException(boolean rethrowException) {
        this.rethrowException = rethrowException;
    }

}
