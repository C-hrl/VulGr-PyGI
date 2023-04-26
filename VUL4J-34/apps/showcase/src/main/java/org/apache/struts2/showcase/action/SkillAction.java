
package org.apache.struts2.showcase.action;

import com.opensymphony.xwork2.Preparable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.showcase.dao.Dao;
import org.apache.struts2.showcase.dao.SkillDao;
import org.apache.struts2.showcase.model.Skill;
import org.springframework.beans.factory.annotation.Autowired;



public class SkillAction extends AbstractCRUDAction implements Preparable {

	private static final Logger log = LogManager.getLogger(SkillAction.class);

	@Autowired
	private SkillDao skillDao;

	private String skillName;
	private Skill currentSkill;

	
	public void prepare() throws Exception {
		Skill preFetched = (Skill) fetch(getSkillName(), getCurrentSkill());
		if (preFetched != null) {
			setCurrentSkill(preFetched);
		}
	}

	public String save() throws Exception {
		if (getCurrentSkill() != null) {
			setSkillName((String) skillDao.merge(getCurrentSkill()));
		}
		return SUCCESS;
	}

	public String getSkillName() {
		return skillName;
	}

	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}

	protected Dao getDao() {
		return skillDao;
	}

	public Skill getCurrentSkill() {
		return currentSkill;
	}

	public void setCurrentSkill(Skill currentSkill) {
		this.currentSkill = currentSkill;
	}

}
