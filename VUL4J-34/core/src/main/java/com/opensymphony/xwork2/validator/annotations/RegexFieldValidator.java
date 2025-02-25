

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegexFieldValidator {

    
    String message() default "";

    
    String[] messageParams() default {};

    
    String key() default "";

    
    String fieldName() default "";

    
    String regex() default "";

    
    String regexExpression() default "";

    
    boolean trim() default true;

    
    String trimExpression() default "";

    
    boolean caseSensitive() default true;

    
    String caseSensitiveExpression() default "";

    
    boolean shortCircuit() default false;

    
    ValidatorType type() default ValidatorType.FIELD;

}
