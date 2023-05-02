

package com.opensymphony.xwork2.validator.annotations;


public enum ValidatorType {

    FIELD, SIMPLE;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
    
}
