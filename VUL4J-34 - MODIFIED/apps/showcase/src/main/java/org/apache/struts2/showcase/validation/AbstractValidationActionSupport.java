
package org.apache.struts2.showcase.validation;

import com.opensymphony.xwork2.ActionSupport;


public abstract class AbstractValidationActionSupport extends ActionSupport {

	public String submit() throws Exception {
		return "success";
	}

	public String input() throws Exception {
		return "input";
	}
}
