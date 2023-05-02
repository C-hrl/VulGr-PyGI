
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;



public class InterceptorStackConfig extends Located implements Serializable {

    private static final long serialVersionUID = 2897260918170270343L;

    
    protected List<InterceptorMapping> interceptors;
    protected String name;

    
    protected InterceptorStackConfig() {
        this.interceptors = new ArrayList<>();
    }

    
    protected InterceptorStackConfig(InterceptorStackConfig orig) {
        this.name = orig.name;
        this.interceptors = new ArrayList<>(orig.interceptors);
        this.location = orig.location;
    }


    
    public Collection<InterceptorMapping> getInterceptors() {
        return interceptors;
    }

    
    public String getName() {
        return name;
    }

    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InterceptorStackConfig)) {
            return false;
        }

        final InterceptorStackConfig interceptorStackConfig = (InterceptorStackConfig) o;

        if ((interceptors != null) ? (!interceptors.equals(interceptorStackConfig.interceptors)) : (interceptorStackConfig.interceptors != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(interceptorStackConfig.name)) : (interceptorStackConfig.name != null)) {
            return false;
        }

        return true;
    }

    
    @Override
    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((interceptors != null) ? interceptors.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return "InterceptorStackConfig: [" + name + "] contains " + interceptors;
    }

    
    public static class Builder implements InterceptorListHolder {
        protected InterceptorStackConfig target;

        public Builder(String name) {
            target = new InterceptorStackConfig();
            target.name = name;
        }

        public Builder name(String name) {
            target.name = name;
            return this;
        }

        
        public Builder addInterceptor(InterceptorMapping interceptor) {
            target.interceptors.add(interceptor);
            return this;
        }

        
        public Builder addInterceptors(List<InterceptorMapping> interceptors) {
            target.interceptors.addAll(interceptors);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public InterceptorStackConfig build() {
            embalmTarget();
            InterceptorStackConfig result = target;
            target = new InterceptorStackConfig(target);
            return result;
        }

        protected void embalmTarget() {
            target.interceptors = Collections.unmodifiableList(target.interceptors);
        }
    }
}
