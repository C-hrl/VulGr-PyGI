

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldExpressionValidator {

    
    String expression();

    
    String message() default "";

    
    String key() default "";

    
    String[] messageParams() default {};

    
    String fieldName() default "";

    
    boolean shortCircuit() default false;
}