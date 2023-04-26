

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Text;

import com.opensymphony.xwork2.util.ValueStack;



public class TextTag extends ContextBeanTag {

    private static final long serialVersionUID = -3075088084198264581L;

    protected String name;
    protected String searchValueStack;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Text(stack);
    }

    protected void populateParams() {
        super.populateParams();

        Text text = (Text) component;
        text.setName(name);
        text.setSearchValueStack(searchValueStack);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSearchValueStack(String searchStack) {
        this.searchValueStack = searchStack;
    }
}
