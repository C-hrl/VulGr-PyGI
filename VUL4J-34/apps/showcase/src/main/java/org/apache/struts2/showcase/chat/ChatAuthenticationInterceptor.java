
package org.apache.struts2.showcase.chat;


import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.dispatcher.SessionMap;

public class ChatAuthenticationInterceptor implements Interceptor {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LogManager.getLogger(ChatAuthenticationInterceptor.class);
	public static final String USER_SESSION_KEY = "chatUserSessionKey";

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {

		LOG.debug("Authenticating chat user");

		SessionMap session = (SessionMap) ActionContext.getContext().get(ActionContext.SESSION);
		User user = (User) session.get(USER_SESSION_KEY);

		if (user == null) {
			return Action.LOGIN;
		}
		return invocation.invoke();
	}

}
