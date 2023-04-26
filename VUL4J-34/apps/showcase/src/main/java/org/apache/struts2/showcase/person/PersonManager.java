
package org.apache.struts2.showcase.person;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PersonManager {
	private static Set<Person> people = new HashSet<Person>(5);
	private static long COUNT = 5;

	static {
		
		Person p1 = new Person(new Long(1), "Patrick", "Lightbuddie");
		Person p2 = new Person(new Long(2), "Jason", "Carrora");
		Person p3 = new Person(new Long(3), "Alexandru", "Papesco");
		Person p4 = new Person(new Long(4), "Jay", "Boss");
		Person p5 = new Person(new Long(5), "Rainer", "Hermanos");
		people.add(p1);
		people.add(p2);
		people.add(p3);
		people.add(p4);
		people.add(p5);
	}

	public void createPerson(Person person) {
		person.setId(new Long(++COUNT));
		people.add(person);
	}

	public void updatePerson(Person person) {
		people.add(person);
	}

	public Set<Person> getPeople() {
		return people;
	}
}
