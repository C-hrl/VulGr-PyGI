

package org.apache.struts2.interceptor.validation;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.validator.ValidationInterceptor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.struts2.StrutsConstants;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;


public class AnnotationValidationInterceptor extends ValidationInterceptor {

    
    private static final long serialVersionUID = 1813272797367431184L;

    protected String doIntercept(ActionInvocation invocation) throws Exception {

        Object action = invocation.getAction();
        if (action != null) {
            Method method = getActionMethod(action.getClass(), invocation.getProxy().getMethod());
            Collection<Method> annotatedMethods = AnnotationUtils.getAnnotatedMethods(action.getClass(), SkipValidation.class);
            if (annotatedMethods.contains(method))
                return invocation.invoke();

            
            Class clazz = action.getClass().getSuperclass();
            while (clazz != null) {
                annotatedMethods = AnnotationUtils.getAnnotatedMethods(clazz, SkipValidation.class);
                if (annotatedMethods != null) {
                    for (Method annotatedMethod : annotatedMethods) {
                        if (annotatedMethod.getName().equals(method.getName())
                                && Arrays.equals(annotatedMethod.getParameterTypes(), method.getParameterTypes())
                                && Arrays.equals(annotatedMethod.getExceptionTypes(), method.getExceptionTypes()))
                            return invocation.invoke();
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }

        return super.doIntercept(invocation);
    }

    
    protected Method getActionMethod(Class<?> actionClass, String methodName) throws NoSuchMethodException {
        return actionClass.getMethod(methodName);
    }

}
