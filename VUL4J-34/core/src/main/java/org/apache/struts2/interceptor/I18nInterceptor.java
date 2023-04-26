
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;


public class I18nInterceptor extends com.opensymphony.xwork2.interceptor.I18nInterceptor {
    private static final long serialVersionUID = 4587460933182760358L;

    public static final String DEFAULT_COOKIE_ATTRIBUTE = DEFAULT_SESSION_ATTRIBUTE;

    public static final String COOKIE_STORAGE = "cookie";

    public static final String DEFAULT_COOKIE_PARAMETER = "request_cookie_locale";
    protected String requestCookieParameterName = DEFAULT_COOKIE_PARAMETER;

    protected class CookieLocaleFinder extends LocaleFinder {
        protected CookieLocaleFinder(ActionInvocation invocation) {
            super(invocation);
        }

        @Override
        protected void find() {
            
            Map<String, Object> params = actionInvocation.getInvocationContext().getParameters();
            storage = Storage.SESSION.toString();

            requestedLocale = findLocaleParameter(params, parameterName);

            if (requestedLocale != null) {
                return;
            }

            requestedLocale = findLocaleParameter(params, requestCookieParameterName);
            if (requestedLocale != null) {
                storage = COOKIE_STORAGE;
                return;
            }

            requestedLocale = findLocaleParameter(params, requestOnlyParameterName);
            if (requestedLocale != null) {
                storage = Storage.NONE.toString();
            }

        }
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("intercept '{}/{}' {",
                invocation.getProxy().getNamespace(), invocation.getProxy().getActionName());
        }

        LocaleFinder localeFinder = new CookieLocaleFinder(invocation);
        Locale locale = getLocaleFromParam(localeFinder.getRequestedLocale());
        locale = storeLocale(invocation, locale, localeFinder.getStorage());
        saveLocale(invocation, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("before Locale={}", invocation.getStack().findValue("locale"));
        }

        final String result = invocation.invoke();

        if (LOG.isDebugEnabled()) {
            LOG.debug("after Locale={}", invocation.getStack().findValue("locale"));
            LOG.debug("intercept } ");
        }

        return result;
    }

    @Override
    protected Locale storeLocale(ActionInvocation invocation, Locale locale, String storage) {
        if (COOKIE_STORAGE.equals(storage)) {
            ActionContext ac = invocation.getInvocationContext();
            HttpServletResponse response = (HttpServletResponse) ac.get(StrutsStatics.HTTP_RESPONSE);

            Cookie cookie = new Cookie(DEFAULT_COOKIE_ATTRIBUTE, locale.toString());
            cookie.setMaxAge(1209600); 
            response.addCookie(cookie);

            storage = Storage.SESSION.toString();
        }

        return super.storeLocale(invocation, locale, storage);
    }

    @Override
    protected Locale readStoredLocale(ActionInvocation invocation, Map<String, Object> session) {
        Locale locale = this.readStoredLocalFromSession(invocation, session);

        if (locale != null) {
            return locale;
        }

        Cookie[] cookies = ServletActionContext.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (DEFAULT_COOKIE_ATTRIBUTE.equals(cookie.getName())) {
                    return getLocaleFromParam(cookie.getValue());
                }
            }
        }

        return this.readStoredLocalFromCurrentInvocation(invocation);
    }

    public void setRequestCookieParameterName(String requestCookieParameterName) {
        this.requestCookieParameterName = requestCookieParameterName;
    }
}
