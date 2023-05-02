
package com.opensymphony.xwork2.util.location;


public abstract class Located implements Locatable {
    
    protected Location location;
    
    
    public Location getLocation() {
        return location;
    }
    
    
    public void setLocation(Location loc) {
        this.location = loc;
    }
}
