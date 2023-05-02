

package org.apache.struts2.interceptor;

import java.util.Map;


public interface CookiesAware {
    
    void setCookiesMap(Map<String, String> cookies);
}