
package org.apache.struts2.portlet.example.eventing;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.portlet.interceptor.PortletResponseAware;

import javax.portlet.ActionResponse;
import javax.portlet.PortletResponse;
import javax.xml.namespace.QName;

public class PublishAction extends ActionSupport implements PortletResponseAware {

    private PortletResponse response;
    private String name;

    public String execute() throws Exception {

        if (name != null) {
            ((ActionResponse) response).setEvent(new QName("http:

            addActionMessage("Publishing Event with Parameter name : " + name);
        }

        return SUCCESS;
    }

    public void setPortletResponse(PortletResponse response) {
        this.response = response;

    }

    public void setName(String name) {
        this.name = name;
    }
}
