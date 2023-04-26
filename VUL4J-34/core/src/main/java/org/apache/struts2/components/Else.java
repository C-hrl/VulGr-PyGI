

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.annotations.StrutsTag;

import java.io.Writer;
import java.util.Map;


@StrutsTag(name="else", tldTagClass="org.apache.struts2.views.jsp.ElseTag", description="Else tag")
public class Else extends Component {
    public Else(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        Map context = stack.getContext();
        Boolean ifResult = (Boolean) context.get(If.ANSWER);

        context.remove(If.ANSWER);

        return !((ifResult == null) || (ifResult));
    }
}
