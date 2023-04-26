package com.opensymphony.xwork2.interceptor.annotations;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.ParameterFilterInterceptor;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import com.opensymphony.xwork2.util.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class AnnotationParameterFilterIntereptor extends AbstractInterceptor {

    
    @Override public String intercept(ActionInvocation invocation) throws Exception {

        final Object action = invocation.getAction();
        Map<String, Object> parameters = invocation.getInvocationContext().getParameters();

        Object model = invocation.getStack().peek();
        if (model == action) {
            model = null;
        }

        boolean blockByDefault = action.getClass().isAnnotationPresent(BlockByDefault.class);
        List<Field> annotatedFields = new ArrayList<>();
        HashSet<String> paramsToRemove = new HashSet<>();

        if (blockByDefault) {
            AnnotationUtils.addAllFields(Allowed.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Allowed.class, model.getClass(), annotatedFields);
            }

            for (String paramName : parameters.keySet()) {
                boolean allowed = false;

                for (Field field : annotatedFields) {
                    
                    
                    if (field.getName().equals(paramName)) {
                        allowed = true;
                        break;
                    }
                }

                if (!allowed) {
                    paramsToRemove.add(paramName);
                }
            }
        } else {
            AnnotationUtils.addAllFields(Blocked.class, action.getClass(), annotatedFields);
            if (model != null) {
                AnnotationUtils.addAllFields(Blocked.class, model.getClass(), annotatedFields);
            }

            for (String paramName : parameters.keySet()) {
                for (Field field : annotatedFields) {
                    
                    
                    if (field.getName().equals(paramName)) {
                        paramsToRemove.add(paramName);
                    }
                }
            }
        }

        for (String aParamsToRemove : paramsToRemove) {
            parameters.remove(aParamsToRemove);
        }

        return invocation.invoke();
    }

}
