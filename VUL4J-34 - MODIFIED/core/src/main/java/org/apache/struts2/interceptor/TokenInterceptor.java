

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.TokenHelper;

import javax.servlet.http.HttpSession;


public class TokenInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = -6680894220590585506L;

    public static final String INVALID_TOKEN_CODE = "invalid.token";

    private static final String INVALID_TOKEN_MESSAGE_KEY = "struts.messages.invalid.token";
    private static final String DEFAULT_ERROR_MESSAGE = "The form has already been processed or no token was supplied, please try again.";

    private TextProvider textProvider;

    @Inject
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        log.debug("Intercepting invocation to check for valid transaction token.");
        return handleToken(invocation);
    }

    protected String handleToken(ActionInvocation invocation) throws Exception {
        
        
        HttpSession session = ServletActionContext.getRequest().getSession(true);
        synchronized (session) {
            if (!TokenHelper.validToken()) {
                return handleInvalidToken(invocation);
            }
        }
        return handleValidToken(invocation);
    }

    
    protected String handleInvalidToken(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        String errorMessage = getErrorMessage(invocation);

        if (action instanceof ValidationAware) {
            ((ValidationAware) action).addActionError(errorMessage);
        } else {
            log.warn(errorMessage);
        }

        return INVALID_TOKEN_CODE;
    }

    protected String getErrorMessage(ActionInvocation invocation) {
        Object action = invocation.getAction();
        if (action instanceof TextProvider) {
            return ((TextProvider) action).getText(INVALID_TOKEN_MESSAGE_KEY, DEFAULT_ERROR_MESSAGE);
        }
        return textProvider.getText(INVALID_TOKEN_MESSAGE_KEY, DEFAULT_ERROR_MESSAGE);
    }

    
    protected String handleValidToken(ActionInvocation invocation) throws Exception {
        return invocation.invoke();
    }

}
