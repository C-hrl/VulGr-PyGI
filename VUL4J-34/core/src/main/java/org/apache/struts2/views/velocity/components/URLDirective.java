

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.URL;

import com.opensymphony.xwork2.util.ValueStack;


public class URLDirective extends AbstractDirective {
    public String getBeanName() {
        return "url";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new URL(stack, req, res);
    }
}
