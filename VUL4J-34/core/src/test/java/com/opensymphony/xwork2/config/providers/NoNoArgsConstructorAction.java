package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.Action;


public class NoNoArgsConstructorAction implements Action {

    private int foo;

    public NoNoArgsConstructorAction(int foo) {
        this.foo = foo;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }

}
