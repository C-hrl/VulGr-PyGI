
package com.opensymphony.xwork2.interceptor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.opensymphony.xwork2.Action;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InputConfig {
    String methodName() default "";
    String resultName() default Action.INPUT;
}
