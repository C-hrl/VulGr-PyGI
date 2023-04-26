

package org.apache.struts2.views.jsp;

import org.apache.struts2.components.ContextBean;


public abstract class ContextBeanTag extends ComponentTagSupport {
    private String var;

    protected void populateParams() {
        super.populateParams();
        
        ContextBean bean = (ContextBean) component;
        bean.setVar(var);
    }

    public void setVar(String var) {
        this.var = var;
    }
}
