
package org.apache.struts2.showcase.person;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Result(location = "list-people.ftl", type = "freemarker")
public class ListPeopleAction extends ActionSupport {

	private static final long serialVersionUID = 3608017189783645371L;

	@Autowired
	private PersonManager personManager;

	private List<Person> people = new ArrayList<Person>();

	public String execute() {
		people.addAll(personManager.getPeople());

		return SUCCESS;
	}

	public List<Person> getPeople() {
		return people;
	}

	public int getPeopleCount() {
		return people.size();
	}

}
