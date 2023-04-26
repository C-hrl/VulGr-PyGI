
package org.apache.struts2.showcase.conversion;

import com.opensymphony.xwork2.ActionSupport;

import java.util.List;

public class PersonAction extends ActionSupport {

	private List<Person> persons;

	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		return SUCCESS;
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}

}
