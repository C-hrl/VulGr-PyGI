
package com.opensymphony.xwork2;



public class ModelDrivenAction extends ActionSupport implements ModelDriven {

    private String foo;
    private TestBean model = new TestBean();


    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    
    public Object getModel() {
        return model;
    }
}
