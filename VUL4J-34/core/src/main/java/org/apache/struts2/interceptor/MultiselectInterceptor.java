
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class MultiselectInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 1L;

    
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        Map<String, Object> parameters = actionInvocation.getInvocationContext().getParameters();
        Map<String, Object> newParams = new HashMap<>();
        Set<String> keys = parameters.keySet();

        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
            String key = iterator.next();

            if (key.startsWith("__multiselect_")) {
                String name = key.substring("__multiselect_".length());

                iterator.remove();

                
                if (!parameters.containsKey(name)) {

                    
                    newParams.put(name, new String[0]);
                }
            }
        }

        parameters.putAll(newParams);

        return actionInvocation.invoke();
    }

}