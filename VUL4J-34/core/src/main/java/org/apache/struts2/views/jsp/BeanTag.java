

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Bean;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;



public class BeanTag extends ContextBeanTag {

    private static final long serialVersionUID = -3863152522071209267L;

    protected static Logger LOG = LogManager.getLogger(BeanTag.class);

    protected String name;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Bean(stack);
    }

    protected void populateParams() {
        super.populateParams();

        ((Bean) component).setName(name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
