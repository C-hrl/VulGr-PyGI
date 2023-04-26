
package org.apache.struts2.showcase.chat;

import com.opensymphony.xwork2.ActionSupport;

import java.util.ArrayList;
import java.util.List;

public class UsersAvailableAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private List<User> availableUsers = new ArrayList<User>();
	private ChatService chatService;

	public UsersAvailableAction(ChatService chatService) {
		this.chatService = chatService;
	}

	public String execute() throws Exception {

		availableUsers = chatService.getAvailableUsers();

		return SUCCESS;
	}

	public List<User> getAvailableUsers() {
		return availableUsers;
	}
}
