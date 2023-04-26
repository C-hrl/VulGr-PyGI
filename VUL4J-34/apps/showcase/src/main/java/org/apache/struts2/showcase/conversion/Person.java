
package org.apache.struts2.showcase.conversion;

import java.io.Serializable;

public class Person implements Serializable {
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
		return this.age;
	}
}
