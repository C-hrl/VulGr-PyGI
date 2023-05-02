
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionChainResult;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.Unchainable;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.util.*;



public class ChainingInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(ChainingInterceptor.class);

    private static final String ACTION_ERRORS = "actionErrors";
    private static final String FIELD_ERRORS = "fieldErrors";
    private static final String ACTION_MESSAGES = "actionMessages";

    private boolean copyMessages = false;
    private boolean copyErrors = false;
    private boolean copyFieldErrors = false;

    protected Collection<String> excludes;

    protected Collection<String> includes;
    protected ReflectionProvider reflectionProvider;

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Inject(value = "struts.xwork.chaining.copyErrors", required = false)
    public void setCopyErrors(String copyErrors) {
        this.copyErrors = "true".equalsIgnoreCase(copyErrors);
    }

    @Inject(value = "struts.xwork.chaining.copyFieldErrors", required = false)
    public void setCopyFieldErrors(String copyFieldErrors) {
        this.copyFieldErrors = "true".equalsIgnoreCase(copyFieldErrors);
    }

    @Inject(value = "struts.xwork.chaining.copyMessages", required = false)
    public void setCopyMessages(String copyMessages) {
        this.copyMessages = "true".equalsIgnoreCase(copyMessages);
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ValueStack stack = invocation.getStack();
        CompoundRoot root = stack.getRoot();
        if (shouldCopyStack(invocation, root)) {
            copyStack(invocation, root);
        }
        return invocation.invoke();
    }

    private void copyStack(ActionInvocation invocation, CompoundRoot root) {
        List list = prepareList(root);
        Map<String, Object> ctxMap = invocation.getInvocationContext().getContextMap();
        for (Object object : list) {
            if (shouldCopy(object)) {
                reflectionProvider.copy(object, invocation.getAction(), ctxMap, prepareExcludes(), includes);
            }
        }
    }

    private Collection<String> prepareExcludes() {
        Collection<String> localExcludes = excludes;
        if (!copyErrors || !copyMessages ||!copyFieldErrors) {
            if (localExcludes == null) {
                localExcludes = new HashSet<String>();
                if (!copyErrors) {
                    localExcludes.add(ACTION_ERRORS);
                }
                if (!copyMessages) {
                    localExcludes.add(ACTION_MESSAGES);
                }
                if (!copyFieldErrors) {
                    localExcludes.add(FIELD_ERRORS);
                }
            }
        }
        return localExcludes;
    }

    private boolean shouldCopy(Object o) {
        return o != null && !(o instanceof Unchainable);
    }

    @SuppressWarnings("unchecked")
    private List prepareList(CompoundRoot root) {
        List list = new ArrayList(root);
        list.remove(0);
        Collections.reverse(list);
        return list;
    }

    private boolean shouldCopyStack(ActionInvocation invocation, CompoundRoot root) throws Exception {
        Result result = invocation.getResult();
        return root.size() > 1 && (result == null || ActionChainResult.class.isAssignableFrom(result.getClass()));
    }

    
    public Collection<String> getExcludes() {
        return excludes;
    }

    
    public void setExcludes(Collection<String> excludes) {
        this.excludes = excludes;
    }

    
    public Collection<String> getIncludes() {
        return includes;
    }

    
    public void setIncludes(Collection<String> includes) {
        this.includes = includes;
    }

}
