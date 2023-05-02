

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.Password;

import com.opensymphony.xwork2.util.ValueStack;



public class PasswordTag extends TextFieldTag {

    private static final long serialVersionUID = 6802043323617377573L;

    protected String showPassword;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Password(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        ((Password) component).setShowPassword(showPassword);
    }

    public void setShow(String showPassword) {
        this.showPassword = showPassword;
    }

    public void setShowPassword(String showPassword) {
        this.showPassword = showPassword;
    }
}
