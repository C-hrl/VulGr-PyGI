
package org.apache.struts2.portlet.example.spring;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.opensymphony.xwork2.ActionSupport;


public class SpringAction extends ActionSupport {

    private ThingManager thingManager = null;
    private String thing = null;

    public void setThingManager(ThingManager thingManager) {
        this.thingManager = thingManager;
    }

    public List getThings() {
        return thingManager.getThings();
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }

    public String execute() {
        if(StringUtils.isNotEmpty(thing)) {
            thingManager.addThing(thing);
        }
        return SUCCESS;
    }
}
