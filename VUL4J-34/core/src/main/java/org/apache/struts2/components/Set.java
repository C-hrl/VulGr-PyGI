

package org.apache.struts2.components;

import java.io.Writer;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(name="set", tldBodyContent="JSP", tldTagClass="org.apache.struts2.views.jsp.SetTag", description="Assigns a value to a variable in a specified scope")
public class Set extends ContextBean {
    protected String scope;
    protected String value;

    public Set(ValueStack stack) {
        super(stack);
    }

    public boolean end(Writer writer, String body) {
        ValueStack stack = getStack();

        Object o;
        if (value == null) {
            if (body != null && !body.equals("")) {
                o = body;
            } else {
                o = findValue("top");
            }
        } else {
            o = findValue(value);
        }

        body="";

        if ("application".equalsIgnoreCase(scope)) {
            stack.setValue("#application['" + getVar() + "']", o);
        } else if ("session".equalsIgnoreCase(scope)) {
            stack.setValue("#session['" + getVar() + "']", o);
        } else if ("request".equalsIgnoreCase(scope)) {
            stack.setValue("#request['" + getVar() + "']", o);
        } else if ("page".equalsIgnoreCase(scope)) {
            stack.setValue("#attr['" + getVar() + "']", o, false);
        } else {
            stack.getContext().put(getVar(), o);
            stack.setValue("#attr['" + getVar() + "']", o, false);
        }

        return super.end(writer, body);
    }

    @StrutsTagAttribute(required=true, description="Name used to reference the value pushed into the Value Stack")
    public void setVar(String var) {
       super.setVar(var);
    }

    @StrutsTagAttribute(description="The scope in which to assign the variable. Can be <b>application</b>" +
                ", <b>session</b>, <b>request</b>, <b>page</b>, or <b>action</b>.", defaultValue="action")
    public void setScope(String scope) {
        this.scope = scope;
    }

    @StrutsTagAttribute(description="The value that is assigned to the variable named <i>name</i>")
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean usesBody() {
        return true;
    }
}
