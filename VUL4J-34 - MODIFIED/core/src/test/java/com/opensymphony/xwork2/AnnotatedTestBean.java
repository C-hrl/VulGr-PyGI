
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.validator.annotations.IntRangeFieldValidator;
import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

import java.util.Date;



public class AnnotatedTestBean {
    

    private Date birth;
    private String name;
    private int count;

    

    public AnnotatedTestBean() {
    }

    

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getBirth() {
        return birth;
    }

    @Validations(
            intRangeFields = {
                @IntRangeFieldValidator(shortCircuit = true, min = "1", max="100", key="invalid.count", message = "Invalid Count!"),
                @IntRangeFieldValidator(shortCircuit = true, min = "20", max="28", key="invalid.count.bad", message = "Smaller Invalid Count: ${count}")
            }

    )
    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @RequiredStringValidator(message = "You must enter a name.")
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
