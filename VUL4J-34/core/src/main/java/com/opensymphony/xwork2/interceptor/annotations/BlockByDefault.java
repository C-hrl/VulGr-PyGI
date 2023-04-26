package com.opensymphony.xwork2.interceptor.annotations;

import com.opensymphony.xwork2.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BlockByDefault {

}
