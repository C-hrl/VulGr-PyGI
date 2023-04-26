

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitorFieldValidator {

    
    String context() default "";

    
    boolean appendPrefix() default true;

    
    String message() default "";

    
    String key() default "";

    
    String fieldName() default "";

    
    String[] messageParams() default {};

    
    boolean shortCircuit() default false;
}
