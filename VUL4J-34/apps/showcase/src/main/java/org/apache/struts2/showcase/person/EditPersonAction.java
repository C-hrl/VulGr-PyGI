
package org.apache.struts2.showcase.person;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Results({
		@Result(name = "list", location = "list-people.action", type = "redirect"),
		@Result(name = "input", location = "edit-person.jsp")
})
public class EditPersonAction extends ActionSupport {

	private static final long serialVersionUID = 7699491775215130850L;

	@Autowired
	private PersonManager personManager;
	private List<Person> persons = new ArrayList<Person>();

	
	public String execute() throws Exception {
		persons.addAll(personManager.getPeople());
		return INPUT;
	}

	
	public String save() throws Exception {

		for (Person p : persons) {
			personManager.getPeople().remove(p);
			personManager.getPeople().add(p);
		}
		return "list";
	}

	public List<Person> getPersons() {
		return persons;
	}

	public void setPersons(List<Person> persons) {
		this.persons = persons;
	}
}
