
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.location.Locatable;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;



public class XWorkException extends RuntimeException implements Locatable {

    private Location location;


    
    public XWorkException() {
    }

    
    public XWorkException(String s) {
        this(s, null, null);
    }
    
    
    public XWorkException(String s, Object target) {
        this(s, null, target);
    }

    
    public XWorkException(Throwable cause) {
        this(null, cause, null);
    }
    
    
    public XWorkException(Throwable cause, Object target) {
        this(null, cause, target);
    }

    
    public XWorkException(String s, Throwable cause) {
        this(s, cause, null);
    }
    
    
     
    public XWorkException(String s, Throwable cause, Object target) {
        super(s, cause);
        
        this.location = LocationUtils.getLocation(target);
        if (this.location == Location.UNKNOWN) {
            this.location = LocationUtils.getLocation(cause);
        }
    }

    
    public Location getLocation() {
        return this.location;
    }
    
    
    
    @Override
    public String toString() {
        String msg = getMessage();
        if (msg == null && getCause() != null) {
            msg = getCause().getMessage();
        }

        if (location != null) {
            if (msg != null) {
                return msg + " - " + location.toString();
            } else {
                return location.toString();
            }
        } else {
            return msg;
        }
    }
}
