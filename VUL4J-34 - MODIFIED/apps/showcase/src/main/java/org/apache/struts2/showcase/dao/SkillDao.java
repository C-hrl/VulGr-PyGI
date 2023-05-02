
package org.apache.struts2.showcase.dao;

import org.apache.struts2.showcase.model.Skill;
import org.springframework.stereotype.Repository;



@Repository
public class SkillDao extends AbstractDao {

	private static final long serialVersionUID = -8160406514074630866L;

	public Class getFeaturedClass() {
		return Skill.class;
	}

	public Skill getSkill(String name) {
		return (Skill) get(name);
	}
}
