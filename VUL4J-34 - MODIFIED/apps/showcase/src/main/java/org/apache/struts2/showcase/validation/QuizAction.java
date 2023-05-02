
package org.apache.struts2.showcase.validation;

import com.opensymphony.xwork2.ActionSupport;





public class QuizAction extends ActionSupport {

	private static final long serialVersionUID = -7505437345373234225L;

	String name;
	int age;
	String answer;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
}



