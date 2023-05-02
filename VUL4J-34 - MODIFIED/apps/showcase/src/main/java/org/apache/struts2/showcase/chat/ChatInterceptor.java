
package org.apache.struts2.showcase.chat;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpSession;


public class ChatInterceptor implements Interceptor {

	private static final Logger LOG = LogManager.getLogger(ChatInterceptor.class);

	private static final long serialVersionUID = 1L;

	public static final String CHAT_USER_SESSION_KEY = "ChatUserSessionKey";

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		HttpSession session = (HttpSession) ActionContext.getContext().get(ActionContext.SESSION);
		User chatUser = (User) session.getAttribute(CHAT_USER_SESSION_KEY);
		if (chatUser == null) {
			LOG.debug("Chat user not logged in");
			return Action.LOGIN;
		}
		return invocation.invoke();
	}
}


