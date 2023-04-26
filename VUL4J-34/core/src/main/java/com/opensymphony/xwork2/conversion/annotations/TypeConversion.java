
package com.opensymphony.xwork2.conversion.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeConversion {

    
    String key() default "";

    
    ConversionType type() default ConversionType.CLASS;

    
    ConversionRule rule() default ConversionRule.PROPERTY;

    
    String converter() default "";

    
    String value() default "";

}
