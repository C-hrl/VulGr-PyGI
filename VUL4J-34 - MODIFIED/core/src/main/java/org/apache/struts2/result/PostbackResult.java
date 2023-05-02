
package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;


public class PostbackResult extends StrutsResultSupport {

    private static final long serialVersionUID = -2283504349296877429L;
    
    private String actionName;
    private String namespace;
    private String method;
    private boolean prependServletContext = true;
    private boolean cache = true;

    protected ActionMapper actionMapper;

    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse response = (HttpServletResponse) ctx.get(ServletActionContext.HTTP_RESPONSE);

        
        if (!cache) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
            response.setHeader("Pragma", "no-cache"); 
            response.setDateHeader("Expires", 0); 
        }

        
        response.setContentType("text/html");
        
        
        PrintWriter pw = new PrintWriter(response.getOutputStream());
        pw.write("<!DOCTYPE html><html><body><form action=\"" + finalLocation + "\" method=\"POST\">");
        writeFormElements(request, pw);
        writePrologueScript(pw);
        pw.write("</html>");
        pw.flush();
    }

    @Override
    public void execute(ActionInvocation invocation) throws Exception {
        String postbackUri = makePostbackUri(invocation);
        setLocation(postbackUri);
        super.execute(invocation);
    }

    
    protected boolean isElementIncluded(String name, String[] values) {
        return !name.startsWith("action:");
    }

    protected String makePostbackUri(ActionInvocation invocation) {
        ActionContext ctx = invocation.getInvocationContext();
        HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
        String postbackUri;

        if (actionName != null) {
            actionName = conditionalParse(actionName, invocation);
            if (namespace == null) {
                namespace = invocation.getProxy().getNamespace();
            } else {
                namespace = conditionalParse(namespace, invocation);
            }
            if (method == null) {
                method = "";
            } else {
                method = conditionalParse(method, invocation);
            }
            postbackUri = request.getContextPath() + actionMapper.getUriFromActionMapping(new ActionMapping(actionName, namespace, method, null));
        } else {
            String location = getLocation();
            
            if (!location.matches("^([a-zA-z]+:)?
                
                if (prependServletContext && (request.getContextPath() != null) && (request.getContextPath().length() > 0)) {
                    location = request.getContextPath() + location;
                }
            }
            postbackUri = location;
        }

        return postbackUri;
    }

    @Inject
    public final void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    
    public final void setActionName(String actionName) {
        this.actionName = actionName;
    }

    
    public final void setCache(boolean cache) {
        this.cache = cache;
    }

    
    public final void setMethod(String method) {
        this.method = method;
    }

    
    public final void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public final void setPrependServletContext(boolean prependServletContext) {
        this.prependServletContext = prependServletContext;
    }

    protected void writeFormElement(PrintWriter pw, String name, String[] values) throws UnsupportedEncodingException {
        for (String value : values) {
            String encName = URLEncoder.encode(name, "UTF-8");
            String encValue = URLEncoder.encode(value, "UTF-8");
            pw.write("<input type=\"hidden\" name=\"" + encName + "\" value=\"" + encValue + "\"/>");
        }
    }

    private void writeFormElements(HttpServletRequest request, PrintWriter pw) throws UnsupportedEncodingException {
        Map<String, String[]> params = request.getParameterMap();
        for (String name : params.keySet()) {
            String[] values = params.get(name);
            if (isElementIncluded(name, values)) {
                writeFormElement(pw, name, values);
            }
        }
    }

    
    protected void writePrologueScript(PrintWriter pw) {
        pw.write("<script>");
        pw.write("setTimeout(function(){document.forms[0].submit();},0);");
        pw.write("</script>");
    }

}
