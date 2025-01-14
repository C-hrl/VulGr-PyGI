
package com.opensymphony.xwork2.conversion;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.annotations.ConversionType;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

import java.util.HashMap;
import java.util.List;


@Conversion()
public class ConversionTestAction implements Action {



    private String convertInt;

    private String convertDouble;

    private List users = null;


    private HashMap keyValues = null;


    public String getConvertInt() {
        return convertInt;
    }

    @TypeConversion(type = ConversionType.APPLICATION, converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
    public void setConvertInt( String convertInt ) {
        this.convertInt = convertInt;
    }

    public String getConvertDouble() {
        return convertDouble;
    }

    @TypeConversion(converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
    public void setConvertDouble( String convertDouble ) {
        this.convertDouble = convertDouble;
    }

    public List getUsers() {
        return users;
    }

    @TypeConversion(rule = ConversionRule.COLLECTION, converter = "java.lang.String")
    public void setUsers( List users ) {
        this.users = users;
    }

    public HashMap getKeyValues() {
        return keyValues;
    }

    @TypeConversion(rule = ConversionRule.MAP, converter = "java.math.BigInteger")
    public void setKeyValues( HashMap keyValues ) {
        this.keyValues = keyValues;
    }

    
    @TypeConversion(type = ConversionType.APPLICATION, key = "java.util.Date", converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
    public String execute() throws Exception {
        return SUCCESS;
    }
}
