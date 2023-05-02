
package com.opensymphony.xwork2;


public class ModelDrivenAnnotationAction extends ActionSupport implements ModelDriven {

    private String foo;
    private AnnotatedTestBean model = new AnnotatedTestBean();


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
