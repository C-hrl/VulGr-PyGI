

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class CheckboxInterceptor extends AbstractInterceptor {

    
    private static final long serialVersionUID = -586878104807229585L;

    private String uncheckedValue = Boolean.FALSE.toString();

    private static final Logger LOG = LogManager.getLogger(CheckboxInterceptor.class);

    public String intercept(ActionInvocation ai) throws Exception {
        Map<String, Object> parameters = ai.getInvocationContext().getParameters();
        Map<String, String[]> newParams = new HashMap<>();
        Set<Map.Entry<String, Object>> entries = parameters.entrySet();

        for (Iterator<Map.Entry<String, Object>> iterator = entries.iterator(); iterator.hasNext();) {
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();

            if (key.startsWith("__checkbox_")) {
                String name = key.substring("__checkbox_".length());

                Object values = entry.getValue();
                iterator.remove();
                if (values != null && values instanceof String[] && ((String[])values).length > 1) {
              	    LOG.debug("Bypassing automatic checkbox detection due to multiple checkboxes of the same name: {}", name);
                    continue;
                }

                
                if (!parameters.containsKey(name)) {
                    
                    newParams.put(name, new String[]{uncheckedValue});
                }
            }
        }

        parameters.putAll(newParams);

        return ai.invoke();
    }

    
    public void setUncheckedValue(String uncheckedValue) {
        this.uncheckedValue = uncheckedValue;
    }
}
