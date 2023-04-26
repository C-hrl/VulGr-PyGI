
package com.opensymphony.xwork2.validator;


public interface FieldValidator extends Validator {

    
    void setFieldName(String fieldName);

    
    String getFieldName();

}
