
package com.opensymphony.xwork2.conversion.annotations;


public enum ConversionRule {

    PROPERTY, COLLECTION, MAP, KEY, KEY_PROPERTY, ELEMENT, CREATE_IF_NULL;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}

