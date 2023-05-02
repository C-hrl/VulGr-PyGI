
package org.apache.struts2.showcase.chat;

import java.io.Serializable;
import java.util.Date;


public class User implements Serializable {

	private static final long serialVersionUID = -1434958919516089297L;

	private String name;
	private Date creationDate;


	public User(String name) {
		this.name = name;
		this.creationDate = new Date(System.currentTimeMillis());
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getName() {
		return name;
	}
}
