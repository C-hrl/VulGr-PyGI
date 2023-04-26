

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.annotations.StrutsTagAttribute;


public abstract class ContextBean extends Component {
    protected String var;
    
    public ContextBean(ValueStack stack) {
        super(stack);
    }

    protected void putInContext(Object value) {
        if (StringUtils.isNotBlank(var)) {
            stack.getContext().put(var, value);
        }
    }
    
    @StrutsTagAttribute(description="Name used to reference the value pushed into the Value Stack")
    public void setVar(String var) {
        if (var != null) {
            this.var = findString(var);
        }
    }
    
    protected String getVar() {
        return this.var;
    }
}
