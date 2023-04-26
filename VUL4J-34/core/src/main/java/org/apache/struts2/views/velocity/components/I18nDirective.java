

package org.apache.struts2.views.velocity.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.I18n;

import com.opensymphony.xwork2.util.ValueStack;


public class I18nDirective extends AbstractDirective {
    public String getBeanName() {
        return "i18n";
    }

    protected Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new I18n(stack);
    }
}
