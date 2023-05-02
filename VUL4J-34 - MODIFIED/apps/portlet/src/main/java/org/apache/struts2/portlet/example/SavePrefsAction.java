
package org.apache.struts2.portlet.example;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;

import org.apache.struts2.portlet.context.PortletActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class SavePrefsAction extends ActionSupport {
    private String preferenceOne = null;
    private String preferenceTwo = null;
    public String getPreferenceOne() {
        return preferenceOne;
    }
    public void setPreferenceOne(String preferenceOne) {
        this.preferenceOne = preferenceOne;
    }
    public String getPreferenceTwo() {
        return preferenceTwo;
    }
    public void setPreferenceTwo(String preferenceTwo) {
        this.preferenceTwo = preferenceTwo;
    }

    public String execute() throws Exception {
        ActionRequest req = PortletActionContext.getActionRequest();
        PortletPreferences prefs = req.getPreferences();
        prefs.setValue("preferenceOne", preferenceOne);
        prefs.setValue("preferenceTwo", preferenceTwo);
        prefs.store();
        return SUCCESS;
    }

    public String showForm() throws Exception {
        PortletPreferences prefs = PortletActionContext.getRequest().getPreferences();
        preferenceOne = prefs.getValue("preferenceOne", "not set");
        preferenceTwo = prefs.getValue("preferenceTwo", "not set");
        return SUCCESS;
    }
}
