
package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AnnotationWorkflowInterceptor extends AbstractInterceptor implements PreResultListener {

    
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        invocation.addPreResultListener(this);
        List<Method> methods = new ArrayList<>(AnnotationUtils.getAnnotatedMethods(action.getClass(), Before.class));
        if (methods.size() > 0) {
            
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return comparePriorities(method1.getAnnotation(Before.class).priority(),
                                method2.getAnnotation(Before.class).priority());
                }
            });
            for (Method m : methods) {
                final String resultCode = (String) m.invoke(action, (Object[]) null);
                if (resultCode != null) {
                    
                    return resultCode;
                }
            }
        }

        String invocationResult = invocation.invoke();

        
        methods = new ArrayList<Method>(AnnotationUtils.getAnnotatedMethods(action.getClass(), After.class));

        if (methods.size() > 0) {
            
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return comparePriorities(method1.getAnnotation(After.class).priority(),
                                method2.getAnnotation(After.class).priority());
                }
            });
            for (Method m : methods) {
                m.invoke(action, (Object[]) null);
            }
        }

        return invocationResult;
    }

    protected static int comparePriorities(int val1, int val2) {
        if (val2 < val1) {
            return -1;
        } else if (val2 > val1) {
            return 1;
        } else {
            return 0;
        }
    }

    
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        Object action = invocation.getAction();
        List<Method> methods = new ArrayList<Method>(AnnotationUtils.getAnnotatedMethods(action.getClass(), BeforeResult.class));

        if (methods.size() > 0) {
            
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return comparePriorities(method1.getAnnotation(BeforeResult.class).priority(),
                                method2.getAnnotation(BeforeResult.class).priority());
                }
            });
            for (Method m : methods) {
                try {
                    m.invoke(action, (Object[]) null);
                } catch (Exception e) {
                    throw new XWorkException(e);
                }
            }
        }
    }

}
