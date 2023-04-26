

package org.apache.struts2.views.jsp;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DateTag extends ContextBeanTag {

    private static final long serialVersionUID = -6216963123295613440L;

    protected String name;
    protected String format;
    protected boolean nice;
    protected String timezone;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Date(stack);
    }

    protected void populateParams() {
        super.populateParams();
        Date d = (Date)component;
        d.setName(name);
        d.setFormat(format);
        d.setNice(nice);
        d.setTimezone(timezone);
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setNice(boolean nice) {
        this.nice = nice;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
