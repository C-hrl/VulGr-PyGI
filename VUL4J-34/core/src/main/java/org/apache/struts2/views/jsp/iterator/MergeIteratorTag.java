

package org.apache.struts2.views.jsp.iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.MergeIterator;
import org.apache.struts2.views.jsp.ContextBeanTag;

import com.opensymphony.xwork2.util.ValueStack;



public class MergeIteratorTag extends ContextBeanTag {

    private static final long serialVersionUID = 4999729472466011218L;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new MergeIterator(stack);
    }

}
