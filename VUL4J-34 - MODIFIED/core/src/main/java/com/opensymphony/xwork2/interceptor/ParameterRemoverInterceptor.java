
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class ParameterRemoverInterceptor extends AbstractInterceptor {

	private static final Logger LOG = LogManager.getLogger(ParameterRemoverInterceptor.class);

	private static final long serialVersionUID = 1;

	private Set<String> paramNames = Collections.emptySet();
	private Set<String> paramValues = Collections.emptySet();

	
	
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if (!(invocation.getAction() instanceof NoParameters)
				&& (null != this.paramNames)) {
			ActionContext ac = invocation.getInvocationContext();
			final Map<String, Object> parameters = ac.getParameters();

			if (parameters != null) {
                for (String removeName : paramNames) {
                    
                    if (parameters.containsKey(removeName)) {

                        try {
							String[] values = (String[]) parameters.get(removeName);
							String value = values[0];
							if (null != value && this.paramValues.contains(value)) {
                                parameters.remove(removeName);
                            }
                        } catch (Exception e) {
							LOG.error("Failed to convert parameter to string", e);
						}
					}
                }
			}
		}
		return invocation.invoke();
	}

	
	public void setParamNames(String paramNames) {
		this.paramNames = TextParseUtil.commaDelimitedStringToSet(paramNames);
	}


	
	public void setParamValues(String paramValues) {
		this.paramValues = TextParseUtil.commaDelimitedStringToSet(paramValues);
	}
}

