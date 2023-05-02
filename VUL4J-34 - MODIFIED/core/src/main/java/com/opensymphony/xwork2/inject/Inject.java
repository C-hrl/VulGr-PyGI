

package com.opensymphony.xwork2.inject;

import static com.opensymphony.xwork2.inject.Container.DEFAULT_NAME;

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;


@Target({METHOD, CONSTRUCTOR, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Inject {

  
  String value() default DEFAULT_NAME;

  
  boolean required() default true;
}
