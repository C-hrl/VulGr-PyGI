

package org.apache.struts2.util;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.views.util.DefaultUrlHelper;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;



public class URLBean {

    HashMap<String, String> params;
    HttpServletRequest request;
    HttpServletResponse response;
    String page;

    private UrlHelper urlHelper;

    public URLBean setPage(String page) {
        this.page = page;
        return this;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
        urlHelper = ServletActionContext.getContext().getInstance(DefaultUrlHelper.class);
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getURL() {
        
        Map<String, Object> fullParams = null;

        if (params != null) {
            fullParams = new HashMap<String, Object>();
        }

        if (page == null) {
            
            
            if (fullParams != null) {
                fullParams.putAll(request.getParameterMap());
            } else {
                fullParams = request.getParameterMap();
            }
        }

        
        if (params != null) {
            fullParams.putAll(params);
        }

        return urlHelper.buildUrl(page, request, response, fullParams);
    }

    public URLBean addParameter(String name, Object value) {
        if (params == null) {
            params = new HashMap<String, String>();
        }

        if (value == null) {
            params.remove(name);
        } else {
            params.put(name, value.toString());
        }

        return this;
    }

    public String toString() {
        return getURL();
    }
}
