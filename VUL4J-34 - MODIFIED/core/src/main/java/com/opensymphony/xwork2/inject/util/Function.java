

package com.opensymphony.xwork2.inject.util;


public interface Function<F,T> {

  
  T apply(F from);
}
