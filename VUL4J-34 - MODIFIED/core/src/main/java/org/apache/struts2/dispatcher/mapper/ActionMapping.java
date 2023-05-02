

package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.Result;

import java.util.Map;


public class ActionMapping {

    private String name;
    private String namespace;
    private String method;
    private String extension;
    private Map<String, Object> params;
    private Result result;

    
    public ActionMapping() {}

    
    public ActionMapping(Result result) {
        this.result = result;
    }

    
    public ActionMapping(String name, String namespace, String method, Map<String, Object> params) {
        this.name = name;
        this.namespace = namespace;
        this.method = method;
        this.params = params;
    }

    
    public String getName() {
        return name;
    }

    
    public String getNamespace() {
        return namespace;
    }

    
    public Map<String, Object> getParams() {
        return params;
    }

    
    public String getMethod() {
        if (null != method && "".equals(method)) {
            return null;
        } else {
            return method;
        }
    }

    
    public Result getResult() {
        return result;
    }
    
    
    public String getExtension() {
        return extension;
    }

    
    public void setResult(Result result) {
        this.result = result;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    
    public void setMethod(String method) {
        this.method = method;
    }

    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    
    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "ActionMapping{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", method='" + method + '\'' +
                ", extension='" + extension + '\'' +
                ", params=" + params +
                ", result=" + (result != null ? result.getClass().getName() : "null") +
                '}';
    }

}
