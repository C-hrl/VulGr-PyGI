

package org.apache.struts2.views.jsp.iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.AppendIterator;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ContextBeanTag;

import com.opensymphony.xwork2.util.ValueStack;



public class AppendIteratorTag extends ContextBeanTag {

    private static final long serialVersionUID = -6017337859763283691L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new AppendIterator(stack);
    }

}
