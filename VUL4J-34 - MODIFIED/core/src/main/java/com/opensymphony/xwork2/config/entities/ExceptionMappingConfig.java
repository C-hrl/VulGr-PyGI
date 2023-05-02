

package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class ExceptionMappingConfig extends Located implements Serializable {

    protected String name;
    protected String exceptionClassName;
    protected String result;
    protected Map<String,String> params;

    protected ExceptionMappingConfig(String name, String exceptionClassName, String result) {
        this.name = name;
        this.exceptionClassName = exceptionClassName;
        this.result = result;
        this.params = new LinkedHashMap<>();
    }

    protected ExceptionMappingConfig(ExceptionMappingConfig target) {
        this.name = target.name;
        this.exceptionClassName = target.exceptionClassName;
        this.result = target.result;
        this.params = new LinkedHashMap<>(target.params);
        this.location = target.location;
    }

    public String getName() {
        return name;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public String getResult() {
        return result;
    }

    public Map<String,String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ExceptionMappingConfig)) {
            return false;
        }

        final ExceptionMappingConfig exceptionMappingConfig = (ExceptionMappingConfig) o;

        if ((name != null) ? (!name.equals(exceptionMappingConfig.name)) : (exceptionMappingConfig.name != null)) {
            return false;
        }

        if ((exceptionClassName != null) ? (!exceptionClassName.equals(exceptionMappingConfig.exceptionClassName)) : (exceptionMappingConfig.exceptionClassName != null))
        {
            return false;
        }

        if ((result != null) ? (!result.equals(exceptionMappingConfig.result)) : (exceptionMappingConfig.result != null))
        {
            return false;
        }

        if ((params != null) ? (!params.equals(exceptionMappingConfig.params)) : (exceptionMappingConfig.params != null))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode;
        hashCode = ((name != null) ? name.hashCode() : 0);
        hashCode = (29 * hashCode) + ((exceptionClassName != null) ? exceptionClassName.hashCode() : 0);
        hashCode = (29 * hashCode) + ((result != null) ? result.hashCode() : 0);
        hashCode = (29 * hashCode) + ((params != null) ? params.hashCode() : 0);

        return hashCode;
    }

    @Override
    public String toString() {
        return "ExceptionMappingConfig: [" + name + "] handle ["
                + exceptionClassName + "] to result [" + result + "] with params " + params;
    }

    
    public static class Builder{

        protected ExceptionMappingConfig target;

        public Builder(ExceptionMappingConfig toClone) {
            target = new ExceptionMappingConfig(toClone);
        }

        public Builder(String name, String exceptionClassName, String result) {
            target = new ExceptionMappingConfig(name, exceptionClassName, result);
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        public Builder exceptionClassName(String name) {
            target.exceptionClassName = name;
            return this;
        }

        public Builder result(String result) {
            target.result = result;
            return this;
        }

        public Builder addParam(String name, String value) {
            target.params.put(name, value);
            return this;
        }

        public Builder addParams(Map<String,String> params) {
            target.params.putAll(params);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public ExceptionMappingConfig build() {
            embalmTarget();
            ExceptionMappingConfig result = target;
            target = new ExceptionMappingConfig(target);
            return result;
        }

        protected void embalmTarget() {
            target.params = Collections.unmodifiableMap(target.params);
        }
    }

}
