package ${package}.actions;

import com.opensymphony.xwork2.ActionSupport;


public class HelloAction extends ActionSupport {

    public String execute() throws Exception {
        setMessage(getText(MESSAGE));
        return SUCCESS;
    }

    
    public static final String MESSAGE = "hello.message";

    
    private String message;

    
    public String getMessage() {
        return message;
    }

    
    public void setMessage(String message) {
        this.message = message;
    }
}
