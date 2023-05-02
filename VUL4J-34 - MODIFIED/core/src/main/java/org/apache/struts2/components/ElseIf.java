

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;


@StrutsTag(name="elseif", tldTagClass="org.apache.struts2.views.jsp.ElseIfTag", description="Elseif tag")
public class ElseIf extends Component {
    public ElseIf(ValueStack stack) {
        super(stack);
    }

    protected Boolean answer;
    protected String test;

    public boolean start(Writer writer) {
        Boolean ifResult = (Boolean) stack.getContext().get(If.ANSWER);

        if ((ifResult == null) || (ifResult)) {
            return false;
        }

        
        answer = (Boolean) findValue(test, Boolean.class);

        if (answer == null) {
            answer = Boolean.FALSE;
        }
        if (answer) {
            stack.getContext().put(If.ANSWER, answer);
        }
        return answer;
    }

    public boolean end(Writer writer, String body) {
        if (answer == null) {
            answer = Boolean.FALSE;
        }
        if (answer) {
            stack.getContext().put(If.ANSWER, answer);
        }
        return super.end(writer, "");
    }

    @StrutsTagAttribute(description="Expression to determine if body of tag is to be displayed", type="Boolean", required=true)
    public void setTest(String test) {
        this.test = test;
    }
}
