

package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.util.ComponentUtils;
import org.apache.struts2.util.FastByteArrayOutputStream;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.TagUtils;
import org.apache.struts2.views.util.UrlHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class Component {

    private static final Logger LOG = LogManager.getLogger(Component.class);

    public static final String COMPONENT_STACK = "__component_stack";

    
    protected static ConcurrentMap<Class<?>, Collection<String>> standardAttributesMap = new ConcurrentHashMap<>();

    protected boolean devMode = false;
    protected ValueStack stack;
    protected Map parameters;
    protected ActionMapper actionMapper;
    protected boolean throwExceptionOnELFailure;
    private UrlHelper urlHelper;

    
    public Component(ValueStack stack) {
        this.stack = stack;
        this.parameters = new LinkedHashMap<>();
        getComponentStack().push(this);
    }

    
    private String getComponentName() {
        Class c = getClass();
        String name = c.getName();
        int dot = name.lastIndexOf('.');

        return name.substring(dot + 1).toLowerCase();
    }

    @Inject(value = StrutsConstants.STRUTS_DEVMODE, required = false)
    public void setDevMode(String devMode) {
        this.devMode = BooleanUtils.toBoolean(devMode);
    }

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @Inject(StrutsConstants.STRUTS_EL_THROW_EXCEPTION)
    public void setThrowExceptionsOnELFailure(String throwException) {
        this.throwExceptionOnELFailure = BooleanUtils.toBoolean(throwException);
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }
    
    public ValueStack getStack() {
        return stack;
    }

    
    public Stack<Component> getComponentStack() {
        Stack<Component> componentStack = (Stack<Component>) stack.getContext().get(COMPONENT_STACK);
        if (componentStack == null) {
            componentStack = new Stack<>();
            stack.getContext().put(COMPONENT_STACK, componentStack);
        }
        return componentStack;
    }

    
    public boolean start(Writer writer) {
        return true;
    }

    
    public boolean end(Writer writer, String body) {
        return end(writer, body, true);
    }

    
    protected boolean end(Writer writer, String body, boolean popComponentStack) {
        assert(body != null);

        try {
            writer.write(body);
        } catch (IOException e) {
            throw new StrutsException("IOError while writing the body: " + e.getMessage(), e);
        }
        if (popComponentStack) {
            popComponentStack();
        }
        return false;
    }

    
    protected void popComponentStack() {
        getComponentStack().pop();
    }

    
    protected Component findAncestor(Class clazz) {
        Stack componentStack = getComponentStack();
        int currPosition = componentStack.search(this);
        if (currPosition >= 0) {
            int start = componentStack.size() - currPosition - 1;

            
            for (int i = start; i >=0; i--) {
                Component component = (Component) componentStack.get(i);
                if (clazz.isAssignableFrom(component.getClass()) && component != this) {
                    return component;
                }
            }
        }

        return null;
    }

    
    protected String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    
    protected String findString(String expr, String field, String errorMsg) {
        if (expr == null) {
            throw fieldError(field, errorMsg, null);
        } else {
            return findString(expr);
        }
    }

    
    protected StrutsException fieldError(String field, String errorMsg, Exception e) {
        String msg = "tag '" + getComponentName() + "', field '" + field +
                ( parameters != null && parameters.containsKey("name")?"', name '" + parameters.get("name"):"") +
                "': " + errorMsg;
        throw new StrutsException(msg, e);
    }

    
    protected Object findValue(String expr) {
        if (expr == null) {
            return null;
        }

        expr = stripExpressionIfAltSyntax(expr);

        return getStack().findValue(expr, throwExceptionOnELFailure);
    }

    
	protected String stripExpressionIfAltSyntax(String expr) {
		return ComponentUtils.stripExpressionIfAltSyntax(stack, expr);
	}

    
    public boolean altSyntax() {
        return ComponentUtils.altSyntax(stack);
    }

    
	protected String completeExpressionIfAltSyntax(String expr) {
		if (altSyntax()) {
			return "%{" + expr + "}";
		}
		return expr;
	}

    
	protected String findStringIfAltSyntax(String expr) {
		if (altSyntax()) {
		    return findString(expr);
		}
		return expr;
	}

    
    protected Object findValue(String expr, String field, String errorMsg) {
        if (expr == null) {
            throw fieldError(field, errorMsg, null);
        } else {
            Object value = null;
            Exception problem = null;
            try {
                value = findValue(expr);
            } catch (Exception e) {
                problem = e;
            }

            if (value == null) {
                throw fieldError(field, errorMsg, problem);
            }

            return value;
        }
    }

    
    protected Object findValue(String expr, Class toType) {
        if (altSyntax() && toType == String.class) {
            if (ComponentUtils.containsExpression(expr)) {
                return TextParseUtil.translateVariables('%', expr, stack);
            } else {
                return expr;
            }
        } else {
            expr = stripExpressionIfAltSyntax(expr);

            return getStack().findValue(expr, toType, throwExceptionOnELFailure);
        }
    }

    
    protected String determineActionURL(String action, String namespace, String method,
                                        HttpServletRequest req, HttpServletResponse res, Map parameters, String scheme,
                                        boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort,
                                        boolean escapeAmp) {
        String finalAction = findString(action);
        String finalMethod = method != null ? findString(method) : null;
        String finalNamespace = determineNamespace(namespace, getStack(), req);
        ActionMapping mapping = new ActionMapping(finalAction, finalNamespace, finalMethod, parameters);
        String uri = actionMapper.getUriFromActionMapping(mapping);
        return urlHelper.buildUrl(uri, req, res, parameters, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, escapeAmp);
    }

    
    protected String determineNamespace(String namespace, ValueStack stack, HttpServletRequest req) {
        String result;

        if (namespace == null) {
            result = TagUtils.buildNamespace(actionMapper, stack, req);
        } else {
            result = findString(namespace);
        }

        if (result == null) {
            result = "";
        }

        return result;
    }

    
    public void copyParams(Map params) {
        stack.push(parameters);
        stack.push(this);
        try {
            for (Object o : params.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();

                if (key.indexOf('-') >= 0) {
                    
                    
                    
                    parameters.put(key, entry.getValue());
                } else {
                    stack.setValue(key, entry.getValue());
                }
            }
        } finally {
            stack.pop();
            stack.pop();
        }
    }

    
    protected String toString(Throwable t) {
        try (FastByteArrayOutputStream bout = new FastByteArrayOutputStream();
                PrintWriter wrt = new PrintWriter(bout)) {
            t.printStackTrace(wrt);
            return bout.toString();
        }
    }

    
    public Map getParameters() {
        return parameters;
    }

    
    public void addAllParameters(Map params) {
        parameters.putAll(params);
    }

    
    public void addParameter(String key, Object value) {
        if (key != null) {
            Map params = getParameters();

            if (value == null) {
                params.remove(key);
            } else {
                params.put(key, value);
            }
        }
    }

    
    public boolean usesBody() {
        return false;
    }

    
    public boolean isValidTagAttribute(String attrName) {
        return getStandardAttributes().contains(attrName);
    }

    
    protected Collection<String> getStandardAttributes() {
        Class clz = getClass();
        Collection<String> standardAttributes = standardAttributesMap.get(clz);
        if (standardAttributes == null) {
            Collection<Method> methods = AnnotationUtils.getAnnotatedMethods(clz, StrutsTagAttribute.class);
            standardAttributes = new HashSet<>(methods.size());
            for(Method m : methods) {
                standardAttributes.add(StringUtils.uncapitalize(m.getName().substring(3)));
            }
            standardAttributesMap.putIfAbsent(clz, standardAttributes);
        }
        return standardAttributes;
    }

}
