

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ParamTag extends ComponentTagSupport {

    private static final long serialVersionUID = -968332732207156408L;

    protected String name;
    protected String value;
    protected boolean suppressEmptyParameters;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Param(stack);
    }

    protected void populateParams() {
        super.populateParams();

        Param param = (Param) component;
        param.setName(name);
        param.setValue(value);
        param.setSuppressEmptyParameters(suppressEmptyParameters);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
    }
}
