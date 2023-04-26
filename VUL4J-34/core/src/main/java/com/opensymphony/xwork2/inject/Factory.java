

package com.opensymphony.xwork2.inject;


public interface Factory<T> {

  
  T create(Context context) throws Exception;
}
