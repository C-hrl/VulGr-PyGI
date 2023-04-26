package com.opensymphony.xwork2.conversion.impl;


public class ParentClass {

    public enum NestedEnum {
        TEST,
        TEST2,
        TEST3
    }


    private NestedEnum value;

    public void setValue(NestedEnum value) {
        this.value = value;
    }

    public NestedEnum getValue() {
        return value;
    }
}
