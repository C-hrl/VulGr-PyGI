

package ${package}.example;


public class HelloWorld extends ExampleSupport {

    public String execute() throws Exception {
        setMessage(getText(MESSAGE));
        return SUCCESS;
    }

    
    public static final String MESSAGE = "HelloWorld.message";

    
    private String message;

    
    public String getMessage() {
        return message;
    }

    
    public void setMessage(String message) {
        this.message = message;
    }
}
