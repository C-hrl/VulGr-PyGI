
package org.apache.struts2.portlet.example.spring;

import java.util.ArrayList;
import java.util.List;


public class ThingManager {
    private List things = new ArrayList();

    public void addThing(String thing) {
        things.add(thing);
    }

    public List getThings() {
        return things;
    }
}
