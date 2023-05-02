
package com.opensymphony.xwork2;



public class SimpleFooAction implements Action {

    private Integer id;

    public String execute() throws Exception {
        return SUCCESS;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
