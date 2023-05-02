package com.opensymphony.xwork2.result;


public interface ParamNameAwareResult {

    boolean acceptableParameterName(String name, String value);

}
