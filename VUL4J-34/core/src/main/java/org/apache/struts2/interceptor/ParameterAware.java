

package org.apache.struts2.interceptor;

import java.util.Map;



public interface ParameterAware {

    
    public void setParameters(Map<String, String[]> parameters);
}
