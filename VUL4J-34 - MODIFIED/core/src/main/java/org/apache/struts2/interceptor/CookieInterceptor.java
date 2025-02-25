

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.security.AcceptedPatternsChecker;
import com.opensymphony.xwork2.security.ExcludedPatternsChecker;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.Cookie;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class CookieInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 4153142432948747305L;

    private static final Logger LOG = LogManager.getLogger(CookieInterceptor.class);

    private static final String ACCEPTED_PATTERN = "[a-zA-Z0-9\\.\\]\\[_'\\s]+";

    private Set<String> cookiesNameSet = Collections.emptySet();
    private Set<String> cookiesValueSet = Collections.emptySet();

    private ExcludedPatternsChecker excludedPatternsChecker;
    private AcceptedPatternsChecker acceptedPatternsChecker;

    @Inject
    public void setExcludedPatternsChecker(ExcludedPatternsChecker excludedPatternsChecker) {
        this.excludedPatternsChecker = excludedPatternsChecker;
    }

    @Inject
    public void setAcceptedPatternsChecker(AcceptedPatternsChecker acceptedPatternsChecker) {
        this.acceptedPatternsChecker = acceptedPatternsChecker;
        this.acceptedPatternsChecker.setAcceptedPatterns(ACCEPTED_PATTERN);
    }

    
    public void setCookiesName(String cookiesName) {
        if (cookiesName != null) {
            this.cookiesNameSet = TextParseUtil.commaDelimitedStringToSet(cookiesName);
        }
    }

    
    public void setCookiesValue(String cookiesValue) {
        if (cookiesValue != null) {
            this.cookiesValueSet = TextParseUtil.commaDelimitedStringToSet(cookiesValue);
        }
    }

    
    public void setAcceptCookieNames(String commaDelimitedPattern) {
        acceptedPatternsChecker.setAcceptedPatterns(commaDelimitedPattern);
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.debug("start interception");

        
        final Map<String, String> cookiesMap = new LinkedHashMap<>();

        Cookie[] cookies = ServletActionContext.getRequest().getCookies();
        if (cookies != null) {
            final ValueStack stack = ActionContext.getContext().getValueStack();

            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();

                if (isAcceptableName(name)) {
                    if (cookiesNameSet.contains("*")) {
                        LOG.debug("Contains cookie name [*] in configured cookies name set, cookie with name [{}] with value [{}] will be injected", name, value);
                        populateCookieValueIntoStack(name, value, cookiesMap, stack);
                    } else if (cookiesNameSet.contains(cookie.getName())) {
                        populateCookieValueIntoStack(name, value, cookiesMap, stack);
                    }
                } else {
                    LOG.warn("Cookie name [{}] with value [{}] was rejected!", name, value);
                }
            }
        }

        
        injectIntoCookiesAwareAction(invocation.getAction(), cookiesMap);

        return invocation.invoke();
    }

    
    protected boolean isAcceptableName(String name) {
        return !isExcluded(name) && isAccepted(name);
    }

    
    protected boolean isAccepted(String name) {
        AcceptedPatternsChecker.IsAccepted accepted = acceptedPatternsChecker.isAccepted(name);
        if (accepted.isAccepted()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Cookie [{}] matches acceptedPattern [{}]", name, accepted.getAcceptedPattern());
            }
            return true;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Cookie [{}] doesn't match acceptedPattern [{}]", name, accepted.getAcceptedPattern());
        }
        return false;
    }

    
    protected boolean isExcluded(String name) {
        ExcludedPatternsChecker.IsExcluded excluded = excludedPatternsChecker.isExcluded(name);
        if (excluded.isExcluded()) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Cookie [{}] matches excludedPattern [{}]", name, excluded.getExcludedPattern());
            }
            return true;
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("Cookie [{}] doesn't match excludedPattern [{}]", name, excluded.getExcludedPattern());
        }
        return false;
    }

    
    protected void populateCookieValueIntoStack(String cookieName, String cookieValue, Map<String, String> cookiesMap, ValueStack stack) {
        if (cookiesValueSet.isEmpty() || cookiesValueSet.contains("*")) {
            
            
            
            
            if (LOG.isDebugEnabled()) {
                if (cookiesValueSet.isEmpty())
                    LOG.debug("no cookie value is configured, cookie with name [{}] with value [{}] will be injected", cookieName, cookieValue);
                else if (cookiesValueSet.contains("*"))
                    LOG.debug("interceptor is configured to accept any value, cookie with name [{}] with value [{}] will be injected", cookieName, cookieValue);
            }
            cookiesMap.put(cookieName, cookieValue);
            stack.setValue(cookieName, cookieValue);
        }
        else {
            
            
            if (cookiesValueSet.contains(cookieValue)) {
                LOG.debug("both configured cookie name and value matched, cookie [{}] with value [{}] will be injected", cookieName, cookieValue);

                cookiesMap.put(cookieName, cookieValue);
                stack.setValue(cookieName, cookieValue);
            }
        }
    }

    
    protected void injectIntoCookiesAwareAction(Object action, Map<String, String> cookiesMap) {
        if (action instanceof CookiesAware) {
            LOG.debug("Action [{}] implements CookiesAware, injecting cookies map [{}]", action, cookiesMap);
            ((CookiesAware)action).setCookiesMap(cookiesMap);
        }
    }
}
