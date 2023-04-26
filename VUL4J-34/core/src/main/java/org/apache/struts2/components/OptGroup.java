

package org.apache.struts2.components;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


@StrutsTag(
    name="optgroup",
    tldTagClass="org.apache.struts2.views.jsp.ui.OptGroupTag",
    description="Renders a Select Tag's OptGroup Tag")
public class OptGroup extends Component {

    public static final String INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY = "optGroupInternalListUiBeanList";

    private static Logger LOG = LogManager.getLogger(OptGroup.class);

    protected HttpServletRequest req;
    protected HttpServletResponse res;

    protected ListUIBean internalUiBean;

    public OptGroup(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        this.req = req;
        this.res = res;
        internalUiBean = new ListUIBean(stack, req, res) {
            protected String getDefaultTemplate() {
                return "empty";
            }
        };
    }
    
    @Inject
    public void setContainer(Container container) {
        container.inject(internalUiBean);
    }

    public boolean end(Writer writer, String body) {
        Select select = (Select) findAncestor(Select.class);
        if (select == null) {
            LOG.error("incorrect use of OptGroup component, this component must be used within a Select component",
                    new IllegalStateException("incorrect use of OptGroup component, this component must be used within a Select component"));
            return false;
        }
        internalUiBean.start(writer);
        internalUiBean.end(writer, body);

        List listUiBeans = (List) select.getParameters().get(INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY);
        if (listUiBeans == null) {
            listUiBeans = new ArrayList();
        }
        listUiBeans.add(internalUiBean);
        select.addParameter(INTERNAL_LIST_UI_BEAN_LIST_PARAMETER_KEY, listUiBeans);

        return false;
    }

    @StrutsTagAttribute(description="Set the label attribute")
    public void setLabel(String label) {
        internalUiBean.setLabel(label);
    }

    @StrutsTagAttribute(description="Set the disable attribute.")
    public void setDisabled(String disabled) {
        internalUiBean.setDisabled(disabled);
    }

    @StrutsTagAttribute(description="Set the list attribute.")
    public void setList(String list) {
        internalUiBean.setList(list);
    }

    @StrutsTagAttribute(description="Set the listKey attribute.")
    public void setListKey(String listKey) {
        internalUiBean.setListKey(listKey);
    }

    @StrutsTagAttribute(description="Set the listValue attribute.")
    public void setListValue(String listValue) {
        internalUiBean.setListValue(listValue);
    }
}
