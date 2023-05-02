
package org.apache.struts2.showcase.person;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;


@Results({
		@Result(name = "list", location = "list-people.action", type = "redirect"),
		@Result(name = "input", location = "new-person.ftl", type = "freemarker")
})
public class NewPersonAction extends ActionSupport {

	private static final long serialVersionUID = 200410824352645515L;

	@Autowired
	private PersonManager personManager;
	private Person person;

	public String execute() {
		personManager.createPerson(person);

		return "list";
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
