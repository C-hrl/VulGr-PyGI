
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.Action;


public class PrivateConstructorAction implements Action {

    private int foo;

    private PrivateConstructorAction() {
        
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

    public void setFoo(int foo) {
        this.foo = foo;
    }

}
