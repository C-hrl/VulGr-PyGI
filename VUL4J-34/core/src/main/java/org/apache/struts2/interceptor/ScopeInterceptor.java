

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.SessionMap;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;


public class ScopeInterceptor extends AbstractInterceptor implements PreResultListener {

    private static final long serialVersionUID = 9120762699600054395L;

    private static final Logger LOG = LogManager.getLogger(ScopeInterceptor.class);

    private String[] application = null;
    private String[] session = null;
    private String key;
    private String type = null;
    private boolean autoCreateSession = true;
    private String sessionReset = "session.reset";
    private boolean reset = false;

    
    public void setApplication(String s) {
        if (s != null) {
            application = s.split(" *, *");
        }
    }

    
    public void setSession(String s) {
        if (s != null) {
            session = s.split(" *, *");
        }
    }

    
    public void setAutoCreateSession(String value) {
        if (StringUtils.isNotBlank(value)) {
            this.autoCreateSession = BooleanUtils.toBoolean(value);
        }
    }

    private String getKey(ActionInvocation invocation) {
        ActionProxy proxy = invocation.getProxy();
        if (key == null || "CLASS".equals(key)) {
            return "struts.ScopeInterceptor:" + proxy.getAction().getClass();
        } else if ("ACTION".equals(key)) {
            return "struts.ScopeInterceptor:" + proxy.getNamespace() + ":" + proxy.getActionName();
        }
        return key;
    }

    
    public ScopeInterceptor() {
        super();
    }

    
    private static class NULLClass implements Serializable {
      public String toString() {
        return "NULL";
      }
      public int hashCode() {
        return 1; 
      }
      public boolean equals(Object obj) {
        return obj == null || (obj instanceof NULLClass);
      }
    }

    private static final Object NULL = new NULLClass();

    private static Object nullConvert(Object o) {
        if (o == null) {
            return NULL;
        }

        if (o == NULL || NULL.equals(o)) {
            return null;
        }

        return o;
    }

    private static Map locks = new IdentityHashMap();

    static void lock(Object o, ActionInvocation invocation) throws Exception {
        synchronized (o) {
            int count = 3;
            Object previous;
            while ((previous = locks.get(o)) != null) {
                if (previous == invocation) {
                    return;
                }
                if (count-- <= 0) {
                    locks.remove(o);
                    o.notify();

                    throw new StrutsException("Deadlock in session lock");
                }
                o.wait(10000);
            }
            locks.put(o, invocation);
        }
    }

    static void unlock(Object o) {
        synchronized (o) {
            locks.remove(o);
            o.notify();
        }
    }

    protected void after(ActionInvocation invocation, String result) throws Exception {
        Map ses = ActionContext.getContext().getSession();
        if ( ses != null) {
            unlock(ses);
        }
    }


    protected void before(ActionInvocation invocation) throws Exception {
        invocation.addPreResultListener(this);
        Map ses = ActionContext.getContext().getSession();
        if (ses == null && autoCreateSession) {
            ses = new SessionMap(ServletActionContext.getRequest());
            ActionContext.getContext().setSession(ses);
        }

        if ( ses != null) {
            lock(ses, invocation);
        }

        String key = getKey(invocation);
        Map app = ActionContext.getContext().getApplication();
        final ValueStack stack = ActionContext.getContext().getValueStack();

        LOG.debug("scope interceptor before");

        if (application != null)
            for (String string : application) {
                Object attribute = app.get(key + string);
                if (attribute != null) {
                    LOG.debug("Application scoped variable set {} = {}", string, String.valueOf(attribute));
                    stack.setValue(string, nullConvert(attribute));
                }
            }

        if (ActionContext.getContext().getParameters().get(sessionReset) != null) {
            return;
        }

        if (reset) {
            return;
        }

        if (ses == null) {
            LOG.debug("No HttpSession created... Cannot set session scoped variables");
            return;
        }

        if (session != null && (!"start".equals(type))) {
            for (String string : session) {
                Object attribute = ses.get(key + string);
                if (attribute != null) {
                    LOG.debug("Session scoped variable set {} = {}", string, String.valueOf(attribute));
                    stack.setValue(string, nullConvert(attribute));
                }
            }
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        String key = getKey(invocation);
        Map app = ActionContext.getContext().getApplication();
        final ValueStack stack = ActionContext.getContext().getValueStack();

        if (application != null)
            for (String string : application) {
                Object value = stack.findValue(string);
                LOG.debug("Application scoped variable saved {} = {}", string, String.valueOf(value));

                
                app.put(key + string, nullConvert(value));
            }

        boolean ends = "end".equals(type);

        Map ses = ActionContext.getContext().getSession();
        if (ses != null) {

            if (session != null) {
                for (String string : session) {
                    if (ends) {
                        ses.remove(key + string);
                    } else {
                        Object value = stack.findValue(string);
                        LOG.debug("Session scoped variable saved {} = {}", string, String.valueOf(value));

                        
                        
                        ses.put(key + string, nullConvert(value));
                    }
                }
            }
            unlock(ses);
        } else {
            LOG.debug("No HttpSession created... Cannot save session scoped variables.");
        }
        LOG.debug("scope interceptor after (before result)");
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        type = type.toLowerCase();
        if ("start".equals(type) || "end".equals(type)) {
            this.type = type;
        } else {
            throw new IllegalArgumentException("Only start or end are allowed arguments for type");
        }
    }

    
    public String getSessionReset() {
        return sessionReset;
    }

    
    public void setSessionReset(String sessionReset) {
        this.sessionReset = sessionReset;
    }

    
    public String intercept(ActionInvocation invocation) throws Exception {
        String result = null;
        Map ses = ActionContext.getContext().getSession();
        before(invocation);
        try {
            result = invocation.invoke();
            after(invocation, result);
        } finally {
            if (ses != null) {
                unlock(ses);
            }
        }

        return result;
    }

    
    public boolean isReset() {
        return reset;
    }

    
    public void setReset(boolean reset) {
        this.reset = reset;
    }
}
