
package com.opensymphony.xwork2.util.profiling;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class ObjectProfiler {

    
    public static Object getProfiledObject(Class interfaceClazz, Object o) {
        
        if (!UtilTimerStack.isActive()) {
            return o;
        }

        
        if (interfaceClazz.isInterface()) {
            InvocationHandler timerHandler = new TimerInvocationHandler(o);
            return Proxy.newProxyInstance(interfaceClazz.getClassLoader(),
                    new Class[]{interfaceClazz}, timerHandler);
        } else {
            return o;
        }
    }

    
    public static Object profiledInvoke(Method target, Object value, Object[] args) throws IllegalAccessException, InvocationTargetException {
        
        if (!UtilTimerStack.isActive()) {
            return target.invoke(value, args);
        }

        String logLine = getTrimmedClassName(target) + "." + target.getName() + "()";

        UtilTimerStack.push(logLine);
        try {
            Object returnValue = target.invoke(value, args);

            
            if (returnValue != null && target.getReturnType().isInterface()) {
                InvocationHandler timerHandler = new TimerInvocationHandler(returnValue);
                return Proxy.newProxyInstance(returnValue.getClass().getClassLoader(),
                        new Class[]{target.getReturnType()}, timerHandler);
            } else {
                return returnValue;
            }
        } finally {
            UtilTimerStack.pop(logLine);
        }
    }

    
    public static String getTrimmedClassName(Method method) {
        String classname = method.getDeclaringClass().getName();
        return classname.substring(classname.lastIndexOf('.') + 1);
    }

}

class TimerInvocationHandler implements InvocationHandler {
    protected Object target;

    public TimerInvocationHandler(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("Target Object passed to timer cannot be null");
        }
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return ObjectProfiler.profiledInvoke(method, target, args);
    }

}
