
package org.apache.struts2.showcase.model;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.Serializable;



public class Skill implements IdEntity {

	private static final long serialVersionUID = -4150317722693212439L;

	private String name;
	private String description;

	public Skill() {
	}

	public Skill(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeHtml4(name));
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeHtml4(description));
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Serializable getId() {
		return getName();
	}

	public void setId(Serializable id) {
		setName((String) id);
	}

	public String toString() {
		return getName();
	}
}
