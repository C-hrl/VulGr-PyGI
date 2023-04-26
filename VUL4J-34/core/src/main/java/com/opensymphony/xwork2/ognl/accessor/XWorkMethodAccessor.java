
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;



public class XWorkMethodAccessor extends ObjectMethodAccessor {
	
	private static final Logger LOG = LogManager.getLogger(XWorkMethodAccessor.class);

    @Override
    public Object callMethod(Map context, Object object, String string, Object[] objects) throws MethodFailedException {

        
        
        
        

        if (objects.length == 1 && context instanceof OgnlContext) {
            try {
              OgnlContext ogContext=(OgnlContext)context;
              if (OgnlRuntime.hasSetProperty(ogContext, object, string))  {
                  	PropertyDescriptor descriptor=OgnlRuntime.getPropertyDescriptor(object.getClass(), string);
                  	Class propertyType=descriptor.getPropertyType();
                  	if ((Collection.class).isAssignableFrom(propertyType)) {
                  	    
                  	    
                  	    
                  	    
                  	    
                  	    Object propVal=OgnlRuntime.getProperty(ogContext, object, string);
                  	    
                  	    
                  	    PropertyAccessor accessor=OgnlRuntime.getPropertyAccessor(Collection.class);
                  	    ReflectionContextState.setGettingByKeyProperty(ogContext,true);
                  	    return accessor.getProperty(ogContext,propVal,objects[0]);
                  	}
              }
            }	catch (Exception oe) {
                
                
            	LOG.error("An unexpected exception occurred", oe);
            }

        }

        
        if ((objects.length == 2 && string.startsWith("set")) || (objects.length == 1 && string.startsWith("get"))) {
            Boolean exec = (Boolean) context.get(ReflectionContextState.DENY_INDEXED_ACCESS_EXECUTION);
            boolean e = ((exec == null) ? false : exec.booleanValue());
            if (!e) {
                return callMethodWithDebugInfo(context, object, string, objects);
            }
        }
        Boolean exec = (Boolean) context.get(ReflectionContextState.DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return callMethodWithDebugInfo(context, object, string, objects);
        } else {
            return null;
        }
    }

    private Object callMethodWithDebugInfo(Map context, Object object, String methodName, Object[] objects) throws MethodFailedException {
        try {
            return super.callMethod(context, object, methodName, objects);
		}
		catch(MethodFailedException e) {
			if (LOG.isDebugEnabled()) {
				if (!(e.getReason() instanceof NoSuchMethodException)) {
					
                    LOG.debug("Error calling method through OGNL: object: [{}] method: [{}] args: [{}]", e.getReason(), object.toString(), methodName, Arrays.toString(objects));
                }
            }
			throw e;
		}
	}

    @Override
    public Object callStaticMethod(Map context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        Boolean exec = (Boolean) context.get(ReflectionContextState.DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return callStaticMethodWithDebugInfo(context, aClass, string, objects);
        } else {
            return null;
        }
    }

	private Object callStaticMethodWithDebugInfo(Map context, Class aClass, String methodName,
			Object[] objects) throws MethodFailedException {
		try {
			return super.callStaticMethod(context, aClass, methodName, objects);
		}
		catch(MethodFailedException e) {
			if (LOG.isDebugEnabled()) {
				if (!(e.getReason() instanceof NoSuchMethodException)) {
					
					LOG.debug("Error calling method through OGNL, class: [{}] method: [{}] args: [{}]", e.getReason(), aClass.getName(), methodName, Arrays.toString(objects));
				}
			}
			throw e;
		}
	}
}
