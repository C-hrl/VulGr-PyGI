

package org.apache.struts2.interceptor.debugging;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.FreemarkerResult;

import javax.servlet.http.HttpServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;


public class DebuggingInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -3097324155953078783L;

    private final static Logger LOG = LogManager.getLogger(DebuggingInterceptor.class);

    private String[] ignorePrefixes = new String[]{"org.apache.struts.",
            "com.opensymphony.xwork2.", "xwork."};
    private String[] _ignoreKeys = new String[]{"application", "session",
            "parameters", "request"};
    private HashSet<String> ignoreKeys = new HashSet<>(Arrays.asList(_ignoreKeys));

    private final static String XML_MODE = "xml";
    private final static String CONSOLE_MODE = "console";
    private final static String COMMAND_MODE = "command";
    private final static String BROWSER_MODE = "browser";

    private final static String SESSION_KEY = "org.apache.struts2.interceptor.debugging.VALUE_STACK";

    private final static String DEBUG_PARAM = "debug";
    private final static String OBJECT_PARAM = "object";
    private final static String EXPRESSION_PARAM = "expression";
    private final static String DECORATE_PARAM = "decorate";

    private boolean enableXmlWithConsole = false;
    
    private boolean devMode;
    private FreemarkerManager freemarkerManager;
    
    private boolean consoleEnabled = false;
    private ReflectionProvider reflectionProvider;

    @Inject(StrutsConstants.STRUTS_DEVMODE)
    public void setDevMode(String mode) {
        this.devMode = "true".equals(mode);
    }
    
    @Inject
    public void setFreemarkerManager(FreemarkerManager mgr) {
        this.freemarkerManager = mgr;
    }
    
    @Inject
    public void setReflectionProvider(ReflectionProvider reflectionProvider) {
        this.reflectionProvider = reflectionProvider;
    }

    
    public String intercept(ActionInvocation inv) throws Exception {
        boolean actionOnly = false;
        boolean cont = true;
        Boolean devModeOverride = PrepareOperations.getDevModeOverride();
        boolean devMode = devModeOverride != null ? devModeOverride : this.devMode;
        if (devMode) {
            final ActionContext ctx = ActionContext.getContext();
            String type = getParameter(DEBUG_PARAM);
            ctx.getParameters().remove(DEBUG_PARAM);
            if (XML_MODE.equals(type)) {
                inv.addPreResultListener(
                        new PreResultListener() {
                            public void beforeResult(ActionInvocation inv, String result) {
                                printContext();
                            }
                        });
            } else if (CONSOLE_MODE.equals(type)) {
                consoleEnabled = true;
                inv.addPreResultListener(
                        new PreResultListener() {
                            public void beforeResult(ActionInvocation inv, String actionResult) {
                                String xml = "";
                                if (enableXmlWithConsole) {
                                    StringWriter writer = new StringWriter();
                                    printContext(new PrettyPrintWriter(writer));
                                    xml = writer.toString();
                                    xml = xml.replaceAll("&", "&amp;");
                                    xml = xml.replaceAll(">", "&gt;");
                                    xml = xml.replaceAll("<", "&lt;");
                                }
                                ActionContext.getContext().put("debugXML", xml);

                                FreemarkerResult result = new FreemarkerResult();
                                result.setFreemarkerManager(freemarkerManager);
                                result.setContentType("text/html");
                                result.setLocation("/org/apache/struts2/interceptor/debugging/console.ftl");
                                result.setParse(false);
                                try {
                                    result.execute(inv);
                                } catch (Exception ex) {
                                    LOG.error("Unable to create debugging console", ex);
                                }

                            }
                        });
            } else if (COMMAND_MODE.equals(type)) {
                ValueStack stack = (ValueStack) ctx.getSession().get(SESSION_KEY);
                if (stack == null) {
                    
                    stack = (ValueStack) ctx.get(ActionContext.VALUE_STACK);
                    ctx.getSession().put(SESSION_KEY, stack);
                }
                String cmd = getParameter(EXPRESSION_PARAM);

                ServletActionContext.getRequest().setAttribute("decorator", "none");
                HttpServletResponse res = ServletActionContext.getResponse();
                res.setContentType("text/plain");

                try (PrintWriter writer =
                            ServletActionContext.getResponse().getWriter()) {
                    writer.print(stack.findValue(cmd));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                cont = false;
            } else if (BROWSER_MODE.equals(type)) {
                actionOnly = true;
                inv.addPreResultListener(
                    new PreResultListener() {
                        public void beforeResult(ActionInvocation inv, String actionResult) {
                            String rootObjectExpression = getParameter(OBJECT_PARAM);
                            if (rootObjectExpression == null)
                                rootObjectExpression = "#context";
                            String decorate = getParameter(DECORATE_PARAM);
                            ValueStack stack = (ValueStack) ctx.get(ActionContext.VALUE_STACK);
                            Object rootObject = stack.findValue(rootObjectExpression);
                            
                            try (StringWriter writer = new StringWriter()) {
                                ObjectToHTMLWriter htmlWriter = new ObjectToHTMLWriter(writer);
                                htmlWriter.write(reflectionProvider, rootObject, rootObjectExpression);
                                String html = writer.toString();
                                writer.close();
                                
                                stack.set("debugHtml", html);
                                
                                
                                
                                if ("false".equals(decorate))
                                    ServletActionContext.getRequest().setAttribute("decorator", "none");
                                
                                FreemarkerResult result = new FreemarkerResult();
                                result.setFreemarkerManager(freemarkerManager);
                                result.setContentType("text/html");
                                result.setLocation("/org/apache/struts2/interceptor/debugging/browser.ftl");
                                result.execute(inv);
                            } catch (Exception ex) {
                                LOG.error("Unable to create debugging console", ex);
                            }

                        }
                    });
            }
        } 
        if (cont) {
            try {
                if (actionOnly) {
                    inv.invokeActionOnly();
                    return null;
                } else {
                    return inv.invoke();
                }
            } finally {
                if (devMode && consoleEnabled) {
                    final ActionContext ctx = ActionContext.getContext();
                    ctx.getSession().put(SESSION_KEY, ctx.get(ActionContext.VALUE_STACK));
                }
            }
        } else {
            return null;
        }
    }

    
    private String getParameter(String key) {
        String[] arr = (String[]) ActionContext.getContext().getParameters().get(key);
        if (arr != null && arr.length > 0) {
            return arr[0];
        }
        return null;
    }

    
    protected void printContext() {
        HttpServletResponse res = ServletActionContext.getResponse();
        res.setContentType("text/xml");

        try {
            PrettyPrintWriter writer = new PrettyPrintWriter(
                    ServletActionContext.getResponse().getWriter());
            printContext(writer);
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    protected void printContext(PrettyPrintWriter writer) {
        ActionContext ctx = ActionContext.getContext();
        writer.startNode(DEBUG_PARAM);
        serializeIt(ctx.getParameters(), "parameters", writer, new ArrayList<>());
        writer.startNode("context");
        String key;
        Map ctxMap = ctx.getContextMap();
        for (Object o : ctxMap.keySet()) {
            key = o.toString();
            boolean print = !ignoreKeys.contains(key);

            for (String ignorePrefixe : ignorePrefixes) {
                if (key.startsWith(ignorePrefixe)) {
                    print = false;
                    break;
                }
            }
            if (print) {
                serializeIt(ctxMap.get(key), key, writer, new ArrayList<>());
            }
        }
        writer.endNode();
        Map requestMap = (Map) ctx.get("request");
        serializeIt(requestMap, "request", writer, filterValueStack(requestMap));
        serializeIt(ctx.getSession(), "session", writer, new ArrayList<>());

        ValueStack stack = (ValueStack) ctx.get(ActionContext.VALUE_STACK);
        serializeIt(stack.getRoot(), "valueStack", writer, new ArrayList<>());
        writer.endNode();
    }

    
    protected void serializeIt(Object bean, String name,
                               PrettyPrintWriter writer, List<Object> stack) {
        writer.flush();
        
        if ((bean != null) && (stack.contains(bean))) {
            LOG.info("Circular reference detected, not serializing object: {}", name);
            return;
        } else if (bean != null) {
            
            
            stack.add(bean);
        }
        if (bean == null) {
            return;
        }
        String clsName = bean.getClass().getName();

        writer.startNode(name);

        
        if (bean instanceof Collection) {
            Collection col = (Collection) bean;

            
            
            for (Object aCol : col) {
                serializeIt(aCol, "value", writer, stack);
            }
        } else if (bean instanceof Map) {

            Map<Object, Object> map = (Map) bean;

            
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object objValue = entry.getValue();
                serializeIt(objValue, entry.getKey().toString(), writer, stack);
            }
        } else if (bean.getClass().isArray()) {
            
            for (int i = 0; i < Array.getLength(bean); i++) {
                serializeIt(Array.get(bean, i), "arrayitem", writer, stack);
            }
        } else {
            if (clsName.startsWith("java.lang")) {
                writer.setValue(bean.toString());
            } else {
                
                
                try {
                    BeanInfo info = Introspector.getBeanInfo(bean.getClass());
                    PropertyDescriptor[] props = info.getPropertyDescriptors();

                    for (PropertyDescriptor prop : props) {
                        String n = prop.getName();
                        Method m = prop.getReadMethod();

                        
                        
                        if (m != null) {
                            serializeIt(m.invoke(bean), n, writer, stack);
                        }
                    }
                } catch (Exception e) {
                    LOG.error(e.toString(), e);
                }
            }
        }

        writer.endNode();

        
        stack.remove(bean);
    }

    
    public void setEnableXmlWithConsole(boolean enableXmlWithConsole) {
        this.enableXmlWithConsole = enableXmlWithConsole;
    }

    private List<Object> filterValueStack(Map requestMap) {
        List<Object> filter = new ArrayList<>();
        Object valueStack = requestMap.get("struts.valueStack");
    	if(valueStack != null) {
    		filter.add(valueStack);
    	}
    	return filter;
    }
}


