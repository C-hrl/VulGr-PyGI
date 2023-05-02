

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;


@StrutsTag(name="param", tldTagClass="org.apache.struts2.views.jsp.ParamTag", description="Parametrize other tags")
public class Param extends Component {

    protected String name;
    protected String value;
    protected boolean suppressEmptyParameters;

    public Param(ValueStack stack) {
        super(stack);
    }

    public boolean end(Writer writer, String body) {
        Component component = findAncestor(Component.class);
        if (value != null) {
            if (component instanceof UnnamedParametric) {
                ((UnnamedParametric) component).addParameter(findValue(value));
            } else {
                String name = findString(this.name);

                if (name == null) {
                    throw new StrutsException("No name found for following expression: " + this.name);
                }

                Object value = findValue(this.value);
                if (suppressEmptyParameters) {
                    if (value != null && StringUtils.isNotBlank(value.toString())) {
                        component.addParameter(name, value);
                    }
                } else {
                    component.addParameter(name, value);
                }
            }
        } else {
            if (component instanceof UnnamedParametric) {
                ((UnnamedParametric) component).addParameter(body);
            } else {
                component.addParameter(findString(name), body);
            }
        }

        return super.end(writer, "");
    }
    
    public boolean usesBody() {
        return true;
    }

    @StrutsTagAttribute(description="Name of Parameter to set")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Value expression for Parameter to set", defaultValue="The value of evaluating provided name against stack")
    public void setValue(String value) {
        this.value = value;
    }
    
    @StrutsTagAttribute(description="Whether to suppress empty parameters", type="Boolean", defaultValue="false")
    public void setSuppressEmptyParameters(boolean suppressEmptyParameters) {
        this.suppressEmptyParameters = suppressEmptyParameters;
    }
    
    
    public interface UnnamedParametric {

        
        public void addParameter(Object value);
    }

}
