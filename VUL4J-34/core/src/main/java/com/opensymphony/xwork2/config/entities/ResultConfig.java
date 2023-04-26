
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;



public class ResultConfig extends Located implements Serializable {

    protected Map<String,String> params;
    protected String className;
    protected String name;

    protected ResultConfig(String name, String className) {
        this.name = name;
        this.className = className;
        params = new LinkedHashMap<>();
    }

    protected ResultConfig(ResultConfig orig) {
        this.params = orig.params;
        this.name = orig.name;
        this.className = orig.className;
        this.location = orig.location;
    }

    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    public Map<String,String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ResultConfig)) {
            return false;
        }

        final ResultConfig resultConfig = (ResultConfig) o;

        if ((className != null) ? (!className.equals(resultConfig.className)) : (resultConfig.className != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(resultConfig.name)) : (resultConfig.name != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(resultConfig.params)) : (resultConfig.params != null)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((className != null) ? className.hashCode() : 0);
        result = (29 * result) + ((params != null) ? params.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "ResultConfig: [" + name + "] => [" + className + "] with params " + params;
    }

    
    public static final class Builder {
        protected ResultConfig target;

        public Builder(String name, String className) {
            target = new ResultConfig(name, className);
        }

        public Builder(ResultConfig orig) {
            target = new ResultConfig(orig);
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        public Builder className(String name) {
            target.className = name;
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

        public ResultConfig build() {
            embalmTarget();
            ResultConfig result = target;
            target = new ResultConfig(target);
            return result;
        }

        protected void embalmTarget() {
            target.params = Collections.unmodifiableMap(target.params);
        }
    }
}
