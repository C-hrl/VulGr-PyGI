

package com.opensymphony.xwork2.inject;

import java.lang.reflect.Member;


public interface Context {

  
  Container getContainer();

  
  Scope.Strategy getScopeStrategy();

  
  Member getMember();

  
  Class<?> getType();

  
  String getName();
}
