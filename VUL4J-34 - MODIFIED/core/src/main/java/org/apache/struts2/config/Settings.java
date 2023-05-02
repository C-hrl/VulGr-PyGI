

package org.apache.struts2.config;

import com.opensymphony.xwork2.util.location.Location;

import java.util.Iterator;


interface Settings {

    
    String get(String name);

    
    Location getLocation(String name);

    
    Iterator list();

}
