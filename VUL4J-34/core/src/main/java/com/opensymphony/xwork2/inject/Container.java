

package com.opensymphony.xwork2.inject;

import java.io.Serializable;
import java.util.Set;


public interface Container extends Serializable {

  
  String DEFAULT_NAME = "default";

  
  void inject(Object o);

  
  <T> T inject(Class<T> implementation);

  
  <T> T getInstance(Class<T> type, String name);

  
  <T> T getInstance(Class<T> type);
  
  
  Set<String> getInstanceNames(Class<?> type);

  
  void setScopeStrategy(Scope.Strategy scopeStrategy);

  
  void removeScopeStrategy();
}
