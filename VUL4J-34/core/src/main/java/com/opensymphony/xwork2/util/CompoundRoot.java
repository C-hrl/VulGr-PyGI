
package com.opensymphony.xwork2.util;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



public class CompoundRoot extends CopyOnWriteArrayList<Object> {

    private static final long serialVersionUID = 8563229069192473995L;

    public CompoundRoot() {
    }

    public CompoundRoot(List<?> list) {
        super(list);
    }


    public CompoundRoot cutStack(int index) {
        return new CompoundRoot(subList(index, size()));
    }

    public Object peek() {
        return get(0);
    }

    public Object pop() {
        return remove(0);
    }

    public void push(Object o) {
        add(0, o);
    }
}
