
package org.apache.struts2.portlet.example;

import org.apache.struts2.dispatcher.DefaultActionSupport;

import com.opensymphony.xwork2.ActionSupport;


public class FormExample extends DefaultActionSupport {

    String firstName = null;
    String lastName = null;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String displayResult() {
    	return "displayResult";
    }
}
