

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;
import java.util.Map;


public class I18nInterceptor extends AbstractInterceptor {
    private static final long serialVersionUID = 2496830135246700300L;

    protected static final Logger LOG = LogManager.getLogger(I18nInterceptor.class);

    public static final String DEFAULT_SESSION_ATTRIBUTE = "WW_TRANS_I18N_LOCALE";
    public static final String DEFAULT_PARAMETER = "request_locale";
    public static final String DEFAULT_REQUESTONLY_PARAMETER = "request_only_locale";

    protected String parameterName = DEFAULT_PARAMETER;
    protected String requestOnlyParameterName = DEFAULT_REQUESTONLY_PARAMETER;
    protected String attributeName = DEFAULT_SESSION_ATTRIBUTE;

    
    protected enum Storage { SESSION, NONE }

    public I18nInterceptor() {
        LOG.debug("new I18nInterceptor()");
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setRequestOnlyParameterName(String requestOnlyParameterName) {
        this.requestOnlyParameterName = requestOnlyParameterName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Intercept '{}/{}' {", invocation.getProxy().getNamespace(), invocation.getProxy().getActionName());
        }

        LocaleFinder localeFinder = new LocaleFinder(invocation);
        Locale locale = getLocaleFromParam(localeFinder.getRequestedLocale());
        locale = storeLocale(invocation, locale, localeFinder.getStorage());
        saveLocale(invocation, locale);

        if (LOG.isDebugEnabled()) {
            LOG.debug("before Locale: {}", invocation.getStack().findValue("locale"));
        }

        final String result = invocation.invoke();

        if (LOG.isDebugEnabled()) {
            LOG.debug("after Locale {}", invocation.getStack().findValue("locale"));
            LOG.debug("intercept } ");
        }

        return result;
    }

    
    protected Locale storeLocale(ActionInvocation invocation, Locale locale, String storage) {
        
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        if (session != null) {
            synchronized (session) {
                if (locale == null) {
                    storage = Storage.NONE.toString();
                    locale = readStoredLocale(invocation, session);
                }

                if (Storage.SESSION.toString().equals(storage)) {
                    session.put(attributeName, locale);
                }
            }
        }
        return locale;
    }

    protected class LocaleFinder {
        protected String storage = Storage.SESSION.toString();
        protected Object requestedLocale = null;

        protected ActionInvocation actionInvocation = null;

        protected LocaleFinder(ActionInvocation invocation) {
            actionInvocation = invocation;
            find();
        }

        protected void find() {
            
            Map<String, Object> params = actionInvocation.getInvocationContext().getParameters();

            storage = Storage.SESSION.toString();

            requestedLocale = findLocaleParameter(params, parameterName);
            if (requestedLocale != null) {
                return;
            }

            requestedLocale = findLocaleParameter(params, requestOnlyParameterName);
            if (requestedLocale != null) {
                storage = Storage.NONE.toString();
            }
        }

        public String getStorage() {
            return storage;
        }

        public Object getRequestedLocale() {
            return requestedLocale;
        }
    }

    
    protected Locale getLocaleFromParam(Object requestedLocale) {
        Locale locale = null;
        if (requestedLocale != null) {
            locale = (requestedLocale instanceof Locale) ?
                    (Locale) requestedLocale :
                    LocalizedTextUtil.localeFromString(requestedLocale.toString(), null);
            if (locale != null) {
                LOG.debug("Applied request locale: {}", locale);
            }
        }

        return locale;
    }

    
    protected Locale readStoredLocale(ActionInvocation invocation, Map<String, Object> session) {
        Locale locale = this.readStoredLocalFromSession(invocation, session);

        if (locale != null) {
            return locale;
        }

        return this.readStoredLocalFromCurrentInvocation(invocation);
    }

    protected Locale readStoredLocalFromSession(ActionInvocation invocation, Map<String, Object> session) {
         
        Object sessionLocale = session.get(attributeName);
        if (sessionLocale != null && sessionLocale instanceof Locale) {
            Locale locale = (Locale) sessionLocale;
            LOG.debug("Applied session locale: {}", locale);
            return locale;
        }
        return null;
    }

    protected Locale readStoredLocalFromCurrentInvocation(ActionInvocation invocation) {
        
        Locale locale = invocation.getInvocationContext().getLocale();
        if (locale != null) {
            LOG.debug("Applied invocation context locale: {}", locale);
        }
        return locale;
    }

    protected Object findLocaleParameter(Map<String, Object> params, String parameterName) {
        Object requestedLocale = params.remove(parameterName);
        if (requestedLocale != null && requestedLocale.getClass().isArray()
                && ((Object[]) requestedLocale).length > 0) {
            requestedLocale = ((Object[]) requestedLocale)[0];

            LOG.debug("Requested locale: {}", requestedLocale);
        }
        return requestedLocale;
    }

    
    protected void saveLocale(ActionInvocation invocation, Locale locale) {
        invocation.getInvocationContext().setLocale(locale);
    }

}
