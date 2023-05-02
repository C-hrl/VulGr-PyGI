

package com.opensymphony.xwork2.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface Scoped {

  
  Scope value();
}
