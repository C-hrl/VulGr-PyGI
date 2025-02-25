
package org.apache.struts2.showcase.chat;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class ChatSessionListener implements HttpSessionListener {

	private static final Logger LOG = LogManager.getLogger(ChatSessionListener.class);

	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(session.getServletContext());
		if (context != null) {
			User user = (User) session.getAttribute(ChatInterceptor.CHAT_USER_SESSION_KEY);
			if (user != null) {
				ChatService service = (ChatService) context.getBean("chatService");
				service.logout(user.getName());

				LOG.info("session expired, logged user [" + user.getName() + "] out");
			}
		}
	}

}
