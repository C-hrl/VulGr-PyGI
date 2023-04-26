
package org.apache.struts2.showcase.chat;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

public class ChatLogoutAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 1L;

	private ChatService chatService;

	private Map session;


	public ChatLogoutAction(ChatService chatService) {
		this.chatService = chatService;
	}

	public String execute() throws Exception {

		User user = (User) session.get(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		if (user != null) {
			chatService.logout(user.getName());
			session.remove(ChatAuthenticationInterceptor.USER_SESSION_KEY);
		}

		return SUCCESS;
	}


	
	public void setSession(Map session) {
		this.session = session;
	}
}
