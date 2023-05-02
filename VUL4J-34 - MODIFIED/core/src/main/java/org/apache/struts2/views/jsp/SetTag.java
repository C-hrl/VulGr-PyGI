

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Set;

import com.opensymphony.xwork2.util.ValueStack;



public class SetTag extends ContextBeanTag {

    private static final long serialVersionUID = -5074213926790716974L;

    protected String scope;
    protected String value;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Set(stack);
    }

    protected void populateParams() {
        super.populateParams();

        Set set = (Set) component;
        set.setScope(scope);
        set.setValue(value);
    }

    public void setName(String name) {
       setVar(name);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
