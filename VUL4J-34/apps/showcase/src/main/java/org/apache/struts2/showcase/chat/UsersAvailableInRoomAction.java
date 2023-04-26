
package org.apache.struts2.showcase.chat;

import com.opensymphony.xwork2.ActionSupport;

import java.util.ArrayList;
import java.util.List;

public class UsersAvailableInRoomAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private ChatService chatService;
	private List<User> usersAvailableInRoom = new ArrayList<User>();

	private String roomName;

	public UsersAvailableInRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}


	public String getRoomName() {
		return this.roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public List<User> getUsersAvailableInRoom() {
		return usersAvailableInRoom;
	}

	public String execute() throws Exception {
		try {
			usersAvailableInRoom = chatService.getUsersAvailableInRoom(roomName);
		} catch (ChatException e) {
			addActionError(e.getMessage());
		}
		return SUCCESS;
	}

}
