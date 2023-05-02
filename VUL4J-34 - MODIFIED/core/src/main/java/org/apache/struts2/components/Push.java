

package org.apache.struts2.components;

import java.io.Writer;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(name="push", tldTagClass="org.apache.struts2.views.jsp.PushTag", description="Push value on stack for simplified usage.")
public class Push extends Component {
    protected String value;
    protected boolean pushed;

    public Push(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        ValueStack stack = getStack();

        if (stack != null) {
            stack.push(findValue(value, "value", "You must specify a value to push on the stack. Example: person"));
            pushed = true;
        } else {
            pushed = false; 
        }

        return result;
    }

    public boolean end(Writer writer, String body) {
        ValueStack stack = getStack();

        if (pushed && (stack != null)) {
            stack.pop();
        }

        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="Value to push on stack", required=true)
    public void setValue(String value) {
        this.value = value;
    }
    
}
