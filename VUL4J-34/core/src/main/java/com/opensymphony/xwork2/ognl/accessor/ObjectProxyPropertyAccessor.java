

package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ognl.ObjectProxy;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;
import ognl.OgnlContext;

import java.util.Map;


public class ObjectProxyPropertyAccessor implements PropertyAccessor {

    
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return null;  
    }

    
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return null;  
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        return OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).getProperty(context, target, name);

    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).setProperty(context, target, name, value);
    }

    
    private void setupContext(Map context, ObjectProxy proxy) {
        ReflectionContextState.setLastBeanClassAccessed(context, proxy.getLastClassAccessed());
        ReflectionContextState.setLastBeanPropertyAccessed(context, proxy.getLastPropertyAccessed());
    }
}