

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.File;

import com.opensymphony.xwork2.util.ValueStack;



public class FileTag extends AbstractUITag {

    private static final long serialVersionUID = -2154950640215144864L;

    protected String accept;
    protected String size;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new File(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        File file = ((File) component);
        file.setAccept(accept);
        file.setSize(size);
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
