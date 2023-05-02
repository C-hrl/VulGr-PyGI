
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ModelDriven;


public interface ScopedModelDriven<T> extends ModelDriven<T> {

    
    void setModel(T model);
    
    
    void setScopeKey(String key);
    
    
    String getScopeKey();
}
