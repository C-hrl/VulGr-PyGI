

package org.apache.struts2.dispatcher;


import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;


public class DefaultActionSupport extends ActionSupport {

    private static final long serialVersionUID = -2426166391283746095L;

    private String successResultValue;


    
    public DefaultActionSupport() {
        super();
    }

    
    public String execute() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String requestedUrl = request.getPathInfo();
        if (successResultValue == null) successResultValue = requestedUrl;
        return SUCCESS;
    }

    
    public String getSuccessResultValue() {
        return successResultValue;
    }

    
    public void setSuccessResultValue(String successResultValue) {
        this.successResultValue = successResultValue;
    }


}
