

package org.apache.struts2.components;

import java.io.Writer;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(name="if", tldTagClass="org.apache.struts2.views.jsp.IfTag", description="If tag")
public class If extends Component {
    public static final String ANSWER = "struts.if.answer";

    Boolean answer;
    String test;

    @StrutsTagAttribute(description="Expression to determine if body of tag is to be displayed", type="Boolean", required=true)
    public void setTest(String test) {
        this.test = test;
    }

    public If(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        answer = (Boolean) findValue(test, Boolean.class);

        if (answer == null) {
            answer = Boolean.FALSE;
        }
        stack.getContext().put(ANSWER, answer);
        return answer.booleanValue();
    }

    public boolean end(Writer writer, String body) {
        stack.getContext().put(ANSWER, answer);
        return super.end(writer, body);
    }
}
