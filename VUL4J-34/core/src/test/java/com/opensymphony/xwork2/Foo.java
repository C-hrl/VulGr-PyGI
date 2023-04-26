

package com.opensymphony.xwork2;

public class Foo {

    String name = null;


    public Foo() {
        name = "not set";
    }

    public Foo(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }
}
