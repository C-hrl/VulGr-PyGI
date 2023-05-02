
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.accessor.CompoundRootAccessor;
import com.opensymphony.xwork2.util.ClearableValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.MemberAccessValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


public class OgnlValueStack implements Serializable, ValueStack, ClearableValueStack, MemberAccessValueStack {

    public static final String THROW_EXCEPTION_ON_FAILURE = OgnlValueStack.class.getName() + ".throwExceptionOnFailure";

    private static final long serialVersionUID = 370737852934925530L;

    private static final String MAP_IDENTIFIER_KEY = "com.opensymphony.xwork2.util.OgnlValueStack.MAP_IDENTIFIER_KEY";
    private static final Logger LOG = LogManager.getLogger(OgnlValueStack.class);

    CompoundRoot root;
    transient Map<String, Object> context;
    Class defaultType;
    Map<Object, Object> overrides;
    transient OgnlUtil ognlUtil;
    transient SecurityMemberAccess securityMemberAccess;
    private transient XWorkConverter converter;

    private boolean devMode;
    private boolean logMissingProperties;

    protected OgnlValueStack(XWorkConverter xworkConverter, CompoundRootAccessor accessor, TextProvider prov, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(), allowStaticAccess);
        push(prov);
    }

    protected OgnlValueStack(ValueStack vs, XWorkConverter xworkConverter, CompoundRootAccessor accessor, boolean allowStaticAccess) {
        setRoot(xworkConverter, accessor, new CompoundRoot(vs.getRoot()), allowStaticAccess);
    }

    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
        securityMemberAccess.setExcludedClasses(ognlUtil.getExcludedClasses());
        securityMemberAccess.setExcludedPackageNamePatterns(ognlUtil.getExcludedPackageNamePatterns());
        securityMemberAccess.setExcludedPackageNames(ognlUtil.getExcludedPackageNames());
    }

    protected void setRoot(XWorkConverter xworkConverter, CompoundRootAccessor accessor, CompoundRoot compoundRoot,
                           boolean allowStaticMethodAccess) {
        this.root = compoundRoot;
        this.securityMemberAccess = new SecurityMemberAccess(allowStaticMethodAccess);
        this.context = Ognl.createDefaultContext(this.root, accessor, new OgnlTypeConverterWrapper(xworkConverter), securityMemberAccess);
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
        ((OgnlContext) context).setTraceEvaluations(false);
        ((OgnlContext) context).setKeepLastEvaluation(false);
    }

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    @Inject(value = "logMissingProperties", required = false)
    public void setLogMissingProperties(String logMissingProperties) {
        this.logMissingProperties = BooleanUtils.toBoolean(logMissingProperties);
    }

    
    public Map<String, Object> getContext() {
        return context;
    }

    
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    
    public void setExprOverrides(Map<Object, Object> overrides) {
        if (this.overrides == null) {
            this.overrides = overrides;
        } else {
            this.overrides.putAll(overrides);
        }
    }

    
    public Map<Object, Object> getExprOverrides() {
        return this.overrides;
    }

    
    public CompoundRoot getRoot() {
        return root;
    }

    
    public void setParameter(String expr, Object value) {
        setValue(expr, value, devMode);
    }

    
    public void setValue(String expr, Object value) {
        setValue(expr, value, devMode);
    }

    
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map<String, Object> context = getContext();
        try {
            trySetValue(expr, value, throwExceptionOnFailure, context);
        } catch (OgnlException e) {
            handleOgnlException(expr, value, throwExceptionOnFailure, e);
        } catch (RuntimeException re) { 
            handleRuntimeException(expr, value, throwExceptionOnFailure, re);
        } finally {
            cleanUpContext(context);
        }
    }

    private void trySetValue(String expr, Object value, boolean throwExceptionOnFailure, Map<String, Object> context) throws OgnlException {
        context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
        context.put(REPORT_ERRORS_ON_NO_PROP, (throwExceptionOnFailure) ? Boolean.TRUE : Boolean.FALSE);
        ognlUtil.setValue(expr, context, root, value);
    }

    private void cleanUpContext(Map<String, Object> context) {
        ReflectionContextState.clear(context);
        context.remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
        context.remove(REPORT_ERRORS_ON_NO_PROP);
    }

    private void handleRuntimeException(String expr, Object value, boolean throwExceptionOnFailure, RuntimeException re) {
        if (throwExceptionOnFailure) {
            String message = ErrorMessageBuilder.create()
                    .errorSettingExpressionWithValue(expr, value)
                    .build();
            throw new XWorkException(message, re);
        } else {
            LOG.warn("Error setting value [{}] with expression [{}]", value, expr, re);
        }
    }

    private void handleOgnlException(String expr, Object value, boolean throwExceptionOnFailure, OgnlException e) {
    	boolean shouldLog = shouldLogMissingPropertyWarning(e);
    	String msg = null;
    	if (throwExceptionOnFailure || shouldLog) {
            msg = ErrorMessageBuilder.create().errorSettingExpressionWithValue(expr, value).build();
        }
        if (shouldLog) {
            LOG.warn(msg, e);
    	}
    	
        if (throwExceptionOnFailure) {
            throw new XWorkException(msg, e);
        }
    }

    
    public String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    public String findString(String expr, boolean throwExceptionOnFailure) {
        return (String) findValue(expr, String.class, throwExceptionOnFailure);
    }

    
    public Object findValue(String expr, boolean throwExceptionOnFailure) {
        try {
            setupExceptionOnFailure(throwExceptionOnFailure);
            return tryFindValueWhenExpressionIsNotNull(expr);
        } catch (OgnlException e) {
            return handleOgnlException(expr, throwExceptionOnFailure, e);
        } catch (Exception e) {
            return handleOtherException(expr, throwExceptionOnFailure, e);
        } finally {
            ReflectionContextState.clear(context);
        }
    }

    private void setupExceptionOnFailure(boolean throwExceptionOnFailure) {
        if (throwExceptionOnFailure) {
            context.put(THROW_EXCEPTION_ON_FAILURE, true);
        }
    }

    private Object tryFindValueWhenExpressionIsNotNull(String expr) throws OgnlException {
        if (expr == null) {
            return null;
        }
        return tryFindValue(expr);
    }

    private Object handleOtherException(String expr, boolean throwExceptionOnFailure, Exception e) {
        logLookupFailure(expr, e);

        if (throwExceptionOnFailure)
            throw new XWorkException(e);

        return findInContext(expr);
    }

    private Object tryFindValue(String expr) throws OgnlException {
        Object value;
        expr = lookupForOverrides(expr);
        if (defaultType != null) {
            value = findValue(expr, defaultType);
        } else {
            value = getValueUsingOgnl(expr);
            if (value == null) {
                value = findInContext(expr);
            }
        }
        return value;
    }

    private String lookupForOverrides(String expr) {
        if ((overrides != null) && overrides.containsKey(expr)) {
            expr = (String) overrides.get(expr);
        }
        return expr;
    }

    private Object getValueUsingOgnl(String expr) throws OgnlException {
        try {
            return ognlUtil.getValue(expr, context, root);
        } finally {
            context.remove(THROW_EXCEPTION_ON_FAILURE);
        }
    }

    public Object findValue(String expr) {
        return findValue(expr, false);
    }

    
    public Object findValue(String expr, Class asType, boolean throwExceptionOnFailure) {
        try {
            setupExceptionOnFailure(throwExceptionOnFailure);
            return tryFindValueWhenExpressionIsNotNull(expr, asType);
        } catch (OgnlException e) {
            final Object value = handleOgnlException(expr, throwExceptionOnFailure, e);
            return converter.convertValue(getContext(), value, asType);
        } catch (Exception e) {
            final Object value = handleOtherException(expr, throwExceptionOnFailure, e);
            return converter.convertValue(getContext(), value, asType);
        } finally {
            ReflectionContextState.clear(context);
        }
    }

    private Object tryFindValueWhenExpressionIsNotNull(String expr, Class asType) throws OgnlException {
        if (expr == null) {
            return null;
        }
        return tryFindValue(expr, asType);
    }

    private Object handleOgnlException(String expr, boolean throwExceptionOnFailure, OgnlException e) {
        Object ret = findInContext(expr);
        if (ret == null) {
            if (shouldLogMissingPropertyWarning(e)) {
                LOG.warn("Could not find property [{}]!", expr, e);
            }
            if (throwExceptionOnFailure) {
                throw new XWorkException(e);
            }
        }
        return ret;
    }

    private boolean shouldLogMissingPropertyWarning(OgnlException e) {
        return (e instanceof NoSuchPropertyException || e instanceof MethodFailedException)
        		&& devMode && logMissingProperties;
    }

    private Object tryFindValue(String expr, Class asType) throws OgnlException {
        Object value = null;
        try {
            expr = lookupForOverrides(expr);
            value = getValue(expr, asType);
            if (value == null) {
                value = findInContext(expr);
                return converter.convertValue(getContext(), value, asType);
            }
        } finally {
            context.remove(THROW_EXCEPTION_ON_FAILURE);
        }
        return value;
    }

    private Object getValue(String expr, Class asType) throws OgnlException {
        return ognlUtil.getValue(expr, context, root, asType);
    }

    private Object findInContext(String name) {
        return getContext().get(name);
    }

    public Object findValue(String expr, Class asType) {
        return findValue(expr, asType, false);
    }

    
    private void logLookupFailure(String expr, Exception e) {
        if (devMode && LOG.isWarnEnabled()) {
            LOG.warn("Caught an exception while evaluating expression '{}' against value stack", expr, e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else {
            LOG.debug("Caught an exception while evaluating expression '{}' against value stack", expr, e);
        }
    }

    
    public Object peek() {
        return root.peek();
    }

    
    public Object pop() {
        return root.pop();
    }

    
    public void push(Object o) {
        root.push(o);
    }

    
    public void set(String key, Object o) {
        
        Map setMap = retrieveSetMap();
        setMap.put(key, o);
    }

    private Map retrieveSetMap() {
        Map setMap;
        Object topObj = peek();
        if (shouldUseOldMap(topObj)) {
            setMap = (Map) topObj;
        } else {
            setMap = new HashMap();
            setMap.put(MAP_IDENTIFIER_KEY, "");
            push(setMap);
        }
        return setMap;
    }

    
    private boolean shouldUseOldMap(Object topObj) {
        return topObj instanceof Map && ((Map) topObj).get(MAP_IDENTIFIER_KEY) != null;
    }

    
    public int size() {
        return root.size();
    }

    private Object readResolve() {
        
        ActionContext ac = ActionContext.getContext();
        Container cont = ac.getContainer();
        XWorkConverter xworkConverter = cont.getInstance(XWorkConverter.class);
        CompoundRootAccessor accessor = (CompoundRootAccessor) cont.getInstance(PropertyAccessor.class, CompoundRoot.class.getName());
        TextProvider prov = cont.getInstance(TextProvider.class, "system");
        boolean allow = BooleanUtils.toBoolean(cont.getInstance(String.class, XWorkConstants.ALLOW_STATIC_METHOD_ACCESS));
        OgnlValueStack aStack = new OgnlValueStack(xworkConverter, accessor, prov, allow);
        aStack.setOgnlUtil(cont.getInstance(OgnlUtil.class));
        aStack.setRoot(xworkConverter, accessor, this.root, allow);

        return aStack;
    }


    public void clearContextValues() {
        
        
        ((OgnlContext) context).getValues().clear();
    }

    public void setAcceptProperties(Set<Pattern> acceptedProperties) {
        securityMemberAccess.setAcceptProperties(acceptedProperties);
    }

    public void setExcludeProperties(Set<Pattern> excludeProperties) {
        securityMemberAccess.setExcludeProperties(excludeProperties);
    }

    @Inject
    public void setXWorkConverter(final XWorkConverter converter) {
        this.converter = converter;
    }
}
