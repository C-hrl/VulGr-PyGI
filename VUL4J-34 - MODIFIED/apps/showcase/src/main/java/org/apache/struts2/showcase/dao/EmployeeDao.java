
package org.apache.struts2.showcase.dao;

import org.apache.struts2.showcase.model.Employee;
import org.apache.struts2.showcase.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class EmployeeDao extends AbstractDao {

	private static final long serialVersionUID = -6615310540042830594L;

	@Autowired
	protected SkillDao skillDao;

	public void setSkillDao(SkillDao skillDao) {
		this.skillDao = skillDao;
	}

	public Class getFeaturedClass() {
		return Employee.class;
	}

	public Employee getEmployee(Long id) {
		return (Employee) get(id);
	}

	public Employee setSkills(Employee employee, List<String> skillNames) {
		if (employee != null && skillNames != null) {
			employee.setOtherSkills(new ArrayList());
			for (int i = 0, j = skillNames.size(); i < j; i++) {
				Skill skill = (Skill) skillDao.get(skillNames.get(i));
				employee.getOtherSkills().add(skill);
			}
		}
		return employee;
	}

	public Employee setSkills(Long empId, List skillNames) {
		return setSkills((Employee) get(empId), skillNames);
	}

}
