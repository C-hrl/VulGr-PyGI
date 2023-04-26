

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.File;

import com.opensymphony.xwork2.util.ValueStack;


public class FileDirective extends AbstractDirective {
    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new File(stack, req, res);
    }

    public String getBeanName() {
        return "file";
    }
}
