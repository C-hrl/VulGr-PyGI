
package com.opensymphony.xwork2;

import java.io.Serializable;



public interface Result extends Serializable {

    
    public void execute(ActionInvocation invocation) throws Exception;

}
