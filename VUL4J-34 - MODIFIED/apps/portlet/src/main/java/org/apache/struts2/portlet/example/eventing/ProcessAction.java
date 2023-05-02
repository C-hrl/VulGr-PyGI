
package org.apache.struts2.portlet.example.eventing;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.portlet.interceptor.PortletRequestAware;
import org.apache.struts2.portlet.interceptor.PortletResponseAware;

import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

public class ProcessAction extends ActionSupport implements PortletRequestAware, PortletResponseAware {

    private PortletRequest request;
    private PortletResponse response;
    private String name;

    public String execute() throws Exception {

        if (request instanceof EventRequest) {
            EventRequest req = (EventRequest) request;
            EventResponse res = (EventResponse) response;
            res.setRenderParameter("eventName", (String) req.getEvent().getValue());
            return "forward";
        } else {
            name = request.getParameter("eventName");
        }

        return SUCCESS;
    }

    public void setPortletRequest(PortletRequest request) {
        this.request = request;
    }

    public void setPortletResponse(PortletResponse response) {
        this.response = response;
    }

    public String getName() {
        return this.name;
    }
}
