

package org.apache.struts2;

import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.location.Locatable;



public class StrutsException extends XWorkException implements Locatable {

    private static final long serialVersionUID = 888724366243600135L;


    
    public StrutsException() {
    }

    
    public StrutsException(String s) {
        this(s, null, null);
    }

    
    public StrutsException(String s, Object target) {
        this(s, (Throwable) null, target);
    }

    
    public StrutsException(Throwable cause) {
        this(null, cause, null);
    }

    
    public StrutsException(Throwable cause, Object target) {
        this(null, cause, target);
    }

    
    public StrutsException(String s, Throwable cause) {
        this(s, cause, null);
    }


     
    public StrutsException(String s, Throwable cause, Object target) {
        super(s, cause, target);
    }
}