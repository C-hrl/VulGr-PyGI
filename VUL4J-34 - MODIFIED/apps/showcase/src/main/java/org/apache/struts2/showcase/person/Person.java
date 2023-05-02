
package org.apache.struts2.showcase.person;

public class Person {
	private Long id;
	private String name;
	private String lastName;

	public Person() {
	}

	public Person(Long id, String name, String lastName) {
		this.id = id;
		this.name = name;
		this.lastName = lastName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Person person = (Person) o;

		if (id != null ? !id.equals(person.id) : person.id != null) return false;

		return true;
	}

	public int hashCode() {
		return (id != null ? id.hashCode() : 0);
	}


	public String toString() {
		return "Person{" +
				"id=" + id +
				", name='" + name + '\'' +
				", lastName='" + lastName + '\'' +
				'}';
	}
}
