

package com.opensymphony.xwork2;

public class ExternalReferenceAction implements Action {

    private Foo foo;


    
    public void setFoo(Foo foo) {
        this.foo = foo;
    }

    
    public Foo getFoo() {
        return foo;
    }

    public String execute() throws Exception {
        return SUCCESS;
    }
}
