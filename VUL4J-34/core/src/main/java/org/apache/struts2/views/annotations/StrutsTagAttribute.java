

package org.apache.struts2.views.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrutsTagAttribute {
    String name() default "";

    boolean required() default false;

    boolean rtexprvalue() default false;

    String description();

    String defaultValue() default "";

    String type() default "String";
}
