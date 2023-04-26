
package org.apache.struts2.portlet.example;

import java.util.Collection;
import java.util.Map;

import javax.portlet.RenderRequest;

import org.apache.struts2.portlet.context.PortletActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class FormResultAction extends ActionSupport {

    private String result = null;

    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }

    public Collection getRenderParams() {
        RenderRequest req = PortletActionContext.getRenderRequest();
        Map params = req.getParameterMap();
        return params.entrySet();
    }
}
