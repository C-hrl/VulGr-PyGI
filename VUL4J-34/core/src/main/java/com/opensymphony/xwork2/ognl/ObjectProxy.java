

package com.opensymphony.xwork2.ognl;


public class ObjectProxy {
    private Object value;
    private Class lastClassAccessed;
    private String lastPropertyAccessed;

    public Class getLastClassAccessed() {
        return lastClassAccessed;
    }

    public void setLastClassAccessed(Class lastClassAccessed) {
        this.lastClassAccessed = lastClassAccessed;
    }

    public String getLastPropertyAccessed() {
        return lastPropertyAccessed;
    }

    public void setLastPropertyAccessed(String lastPropertyAccessed) {
        this.lastPropertyAccessed = lastPropertyAccessed;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
