
package com.opensymphony.xwork2.config.entities;

import java.util.Map;


public interface Parameterizable {

    public void addParam(String name, String value);

    void setParams(Map<String, String> params);

    Map<String, String> getParams();
}
