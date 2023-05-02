
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;


public class InterceptorForTestPurpose implements Interceptor {

	private String paramOne;
	private String paramTwo;
	
	public String getParamOne() { return paramOne; }
	public void setParamOne(String paramOne) { this.paramOne = paramOne; }
	
	public String getParamTwo() { return paramTwo; }
	public void setParamTwo(String paramTwo) { this.paramTwo = paramTwo; }
	
	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		return invocation.invoke();
	}

}
