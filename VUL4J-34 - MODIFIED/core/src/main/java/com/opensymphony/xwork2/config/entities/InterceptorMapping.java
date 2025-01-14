

package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.interceptor.Interceptor;

import java.io.Serializable;


public class InterceptorMapping implements Serializable {

    private String name;
    private Interceptor interceptor;

    public InterceptorMapping(String name, Interceptor interceptor) {
        this.name = name;
        this.interceptor = interceptor;
    }

    public String getName() {
        return name;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InterceptorMapping that = (InterceptorMapping) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InterceptorMapping: [" + name + "] => [" + interceptor.getClass().getName() + ']';
    }

}
