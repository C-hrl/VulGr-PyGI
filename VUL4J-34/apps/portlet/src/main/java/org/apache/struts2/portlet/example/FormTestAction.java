
package org.apache.struts2.portlet.example;

import com.opensymphony.xwork2.ActionSupport;


public class FormTestAction extends ActionSupport {

    private String name = null;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
