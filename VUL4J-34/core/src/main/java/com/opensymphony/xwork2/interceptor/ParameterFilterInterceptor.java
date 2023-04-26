
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;


public class ParameterFilterInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LogManager.getLogger(ParameterFilterInterceptor.class);

    private Collection<String> allowed;
    private Collection<String> blocked;
    private Map<String, Boolean> includesExcludesMap;
    private boolean defaultBlock = false;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {

        Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
        HashSet<String> paramsToRemove = new HashSet<>();

        Map<String, Boolean> includesExcludesMap = getIncludesExcludesMap();

        for (String param : parameters.keySet()) {
            boolean currentAllowed = !isDefaultBlock();

            for (String currRule : includesExcludesMap.keySet()) {
                if (param.startsWith(currRule)
                        && (param.length() == currRule.length()
                        || isPropertySeparator(param.charAt(currRule.length())))) {
                    currentAllowed = includesExcludesMap.get(currRule).booleanValue();
                }
            }
            if (!currentAllowed) {
                paramsToRemove.add(param);
            }
        }

        LOG.debug("Params to remove: {}", paramsToRemove);

        for (Object aParamsToRemove : paramsToRemove) {
            parameters.remove(aParamsToRemove);
        }

        return invocation.invoke();
    }

    
    private static boolean isPropertySeparator(char c) {
        return c == '.' || c == '(' || c == '[';
    }

    private Map<String, Boolean> getIncludesExcludesMap() {
        if (this.includesExcludesMap == null) {
            this.includesExcludesMap = new TreeMap<>();

            if (getAllowedCollection() != null) {
                for (String e : getAllowedCollection()) {
                    this.includesExcludesMap.put(e, Boolean.TRUE);
                }
            }
            if (getBlockedCollection() != null) {
                for (String b : getBlockedCollection()) {
                    this.includesExcludesMap.put(b, Boolean.FALSE);
                }
            }
        }

        return this.includesExcludesMap;
    }

    
    public boolean isDefaultBlock() {
        return defaultBlock;
    }

    
    public void setDefaultBlock(boolean defaultExclude) {
        this.defaultBlock = defaultExclude;
    }

    
    public Collection<String> getBlockedCollection() {
        return blocked;
    }

    
    public void setBlockedCollection(Collection<String> blocked) {
        this.blocked = blocked;
    }

    
    public void setBlocked(String blocked) {
        setBlockedCollection(asCollection(blocked));
    }

    
    public Collection<String> getAllowedCollection() {
        return allowed;
    }

    
    public void setAllowedCollection(Collection<String> allowed) {
        this.allowed = allowed;
    }

    
    public void setAllowed(String allowed) {
        setAllowedCollection(asCollection(allowed));
    }

    
    private Collection<String> asCollection(String commaDelim) {
        if (StringUtils.isBlank(commaDelim)) {
            return null;
        }
        return TextParseUtil.commaDelimitedStringToSet(commaDelim);
    }

}
