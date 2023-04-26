

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.interceptor.ParametersInterceptor;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;


public class ActionMappingParametersInteceptor extends ParametersInterceptor {

    
    @Override
    protected Map<String, Object> retrieveParameters(ActionContext ac) {
        ActionMapping mapping = (ActionMapping) ac.get(ServletActionContext.ACTION_MAPPING);
        if (mapping != null) {
            return mapping.getParams();
        } else {
            return Collections.emptyMap();
        }
    }

    
    @Override
    protected void addParametersToContext(ActionContext ac, Map newParams) {
        Map previousParams = ac.getParameters();
        Map combinedParams;
        if (previousParams != null) {
            combinedParams = new TreeMap(previousParams);
        } else {
            combinedParams = new TreeMap();
        }
        combinedParams.putAll(newParams);

        ac.setParameters(combinedParams);
    }
}
