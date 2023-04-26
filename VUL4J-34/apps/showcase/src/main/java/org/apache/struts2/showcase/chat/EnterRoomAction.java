
package org.apache.struts2.showcase.chat;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class EnterRoomAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;

	private ChatService chatService;
	private Map session;
	private String roomName;

	public String getRoomName() {
		return this.roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public EnterRoomAction(ChatService chatService) {
		this.chatService = chatService;
	}

	public String execute() throws Exception {

		User user = (User) session.get(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		try {
			chatService.enterRoom(user, roomName);
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return SUCCESS;
	}


	
	public void setSession(Map session) {
		this.session = session;
	}

}
