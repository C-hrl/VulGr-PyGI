

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface ExpressionValidator {

    
    String expression();

    
    String message() default "";

    
    String key() default "";

    
    String[] messageParams() default {};

    
    boolean shortCircuit() default false;

}
