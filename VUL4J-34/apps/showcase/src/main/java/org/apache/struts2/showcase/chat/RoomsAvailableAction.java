
package org.apache.struts2.showcase.chat;

import com.opensymphony.xwork2.ActionSupport;

import java.util.ArrayList;
import java.util.List;

public class RoomsAvailableAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

	private List<Room> availableRooms = new ArrayList<Room>();

	private ChatService chatService;

	public RoomsAvailableAction(ChatService chatService) {
		this.chatService = chatService;
	}

	public String execute() throws Exception {
		availableRooms = chatService.getAvailableRooms();
		return SUCCESS;
	}

	public List<Room> getAvailableRooms() {
		return availableRooms;
	}
}
