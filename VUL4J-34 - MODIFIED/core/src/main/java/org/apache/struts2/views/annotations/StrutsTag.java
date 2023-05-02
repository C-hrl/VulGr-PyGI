

package org.apache.struts2.views.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StrutsTag {
    String name();

    String tldBodyContent() default "JSP";

    String tldTagClass();

    String description();

    boolean allowDynamicAttributes() default false;
}
