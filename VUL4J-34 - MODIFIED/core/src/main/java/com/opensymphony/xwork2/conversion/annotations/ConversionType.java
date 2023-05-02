
package com.opensymphony.xwork2.conversion.annotations;


public enum ConversionType {


    APPLICATION, CLASS;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }

}
