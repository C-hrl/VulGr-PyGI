
package ${package};

import com.opensymphony.xwork2.ActionSupport;
import java.util.Date;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;


@Conversion()
public class IndexAction extends ActionSupport {
    
    private Date now = new Date(System.currentTimeMillis());
    
    @TypeConversion(converter = "${package}.DateConverter")
    public Date getDateNow() { return now; }
    
    public String execute() throws Exception {
        now = new Date(System.currentTimeMillis());
        return SUCCESS;
    }
}
