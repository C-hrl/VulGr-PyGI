
package org.apache.struts2.showcase.validation;

import com.opensymphony.xwork2.ActionSupport;


public class SubmitApplication extends ActionSupport {

	private String name;
	private Integer age;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getAge() {
		return age;
	}

	public String submitApplication() throws Exception {
		return SUCCESS;
	}

	public String applicationOk() throws Exception {
		addActionMessage("Your application looks ok.");
		return SUCCESS;
	}

	public String cancelApplication() throws Exception {
		addActionMessage("So you have decided to cancel the application");
		return SUCCESS;
	}
}
