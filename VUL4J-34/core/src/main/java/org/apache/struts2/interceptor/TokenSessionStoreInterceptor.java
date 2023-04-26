

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.InvocationSessionStore;
import org.apache.struts2.util.TokenHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;



public class TokenSessionStoreInterceptor extends TokenInterceptor {

    private static final long serialVersionUID = -9032347965469098195L;

    @Override
    protected String handleToken(ActionInvocation invocation) throws Exception {
        
        
        HttpSession session = ServletActionContext.getRequest().getSession(true);
        synchronized (session.getId().intern()) {
            if (!TokenHelper.validToken()) {
                return handleInvalidToken(invocation);
            }
            return handleValidToken(invocation);
        }
    }

    @Override
    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) ac.get(ServletActionContext.HTTP_RESPONSE);
        String tokenName = TokenHelper.getTokenName();
        String token = TokenHelper.getToken(tokenName);

        if ((tokenName != null) && (token != null)) {
            Map params = ac.getParameters();
            params.remove(tokenName);
            params.remove(TokenHelper.TOKEN_NAME_FIELD);

			String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(tokenName);
            ActionInvocation savedInvocation = InvocationSessionStore.loadInvocation(sessionTokenName, token);

            if (savedInvocation != null) {
                
                ValueStack stack = savedInvocation.getStack();
                request.setAttribute(ServletActionContext.STRUTS_VALUESTACK_KEY, stack);

                ActionContext savedContext = savedInvocation.getInvocationContext();
                savedContext.getContextMap().put(ServletActionContext.HTTP_REQUEST, request);
                savedContext.getContextMap().put(ServletActionContext.HTTP_RESPONSE, response);
                Result result = savedInvocation.getResult();

                if ((result != null) && (savedInvocation.getProxy().getExecuteResult())) {
                    result.execute(savedInvocation);
                }

                
                invocation.getProxy().setExecuteResult(false);

                return savedInvocation.getResultCode();
            }
        }

        return INVALID_TOKEN_CODE;
    }

    @Override
    protected String handleValidToken(ActionInvocation invocation) throws Exception {
        
        String key = TokenHelper.getTokenName();
        String token = TokenHelper.getToken(key);
		String sessionTokenName = TokenHelper.buildTokenSessionAttributeName(key);
		InvocationSessionStore.storeInvocation(sessionTokenName, token, invocation);

        return invocation.invoke();
    }

}
