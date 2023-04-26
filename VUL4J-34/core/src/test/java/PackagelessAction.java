

import com.opensymphony.xwork2.ActionSupport;



public class PackagelessAction extends ActionSupport {

    
    public PackagelessAction() {
    }


    @Override
    public String execute() {
        
        System.out.println(getText("actionProperty"));

        
        System.out.println(getText("foo.range"));

        
        System.out.println(getText("non.existant"));

        return NONE;
    }
}
