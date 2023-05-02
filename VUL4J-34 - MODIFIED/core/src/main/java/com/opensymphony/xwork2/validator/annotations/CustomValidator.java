

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomValidator {

    String type();

    
    String fieldName() default "";

    
    String message() default "";

    String key() default "";

    
    String[] messageParams() default {};

    public ValidationParameter[] parameters() default {};

    boolean shortCircuit() default false;

}
