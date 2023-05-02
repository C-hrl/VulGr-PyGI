
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class PrefixMethodInvocationUtil {
	
	private static final Logger LOG = LogManager.getLogger(PrefixMethodInvocationUtil.class);

    private static final String DEFAULT_INVOCATION_METHODNAME = "execute";

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    
	public static void invokePrefixMethod(ActionInvocation actionInvocation, String[] prefixes) throws InvocationTargetException, IllegalAccessException {
		Object action = actionInvocation.getAction();
		
		String methodName = actionInvocation.getProxy().getMethod();
		
		if (methodName == null) {
			
	        methodName = DEFAULT_INVOCATION_METHODNAME;
		}
		
		Method method = getPrefixedMethod(prefixes, methodName, action);
		if (method != null) {
			method.invoke(action, new Object[0]);
		}
	}
	
	
	
	public static Method getPrefixedMethod(String[] prefixes, String methodName, Object action) {
		assert(prefixes != null);
		String capitalizedMethodName = capitalizeMethodName(methodName);
        for (String prefixe : prefixes) {
            String prefixedMethodName = prefixe + capitalizedMethodName;
            try {
                return action.getClass().getMethod(prefixedMethodName, EMPTY_CLASS_ARRAY);
            }
            catch (NoSuchMethodException e) {
                
                LOG.debug("Cannot find method [{}] in action [{}]", prefixedMethodName, action);
            }
        }
		return null;
	}
	
	
	public static String capitalizeMethodName(String methodName) {
		assert(methodName != null);
		return methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
	}

}
