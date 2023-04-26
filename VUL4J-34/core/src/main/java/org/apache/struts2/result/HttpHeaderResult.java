

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;



public class HttpHeaderResult implements Result {

    private static final long serialVersionUID = 195648957144219214L;
    private static final Logger LOG = LogManager.getLogger(HttpHeaderResult.class);

    
    public static final String DEFAULT_PARAM = null;

    private boolean parse = true;
    private Map<String, String> headers;
    private int status = -1;
    private String error = null;
    private String errorMessage;

    public HttpHeaderResult() {
        super();
        headers = new HashMap<>();
    }

    public HttpHeaderResult(int status) {
        this();
        this.status = status;
        this.parse = false;
    }

    
    public void setError(String error) {
        this.error = error;
    }

    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    
    public Map<String, String> getHeaders() {
        return headers;
    }

    
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    
    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    
    public void execute(ActionInvocation invocation) throws Exception {
        HttpServletResponse response = ServletActionContext.getResponse();
        ValueStack stack = ActionContext.getContext().getValueStack();

        if (status != -1) {
            response.setStatus(status);
        }

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                String value = entry.getValue();
                String finalValue = parse ? TextParseUtil.translateVariables(value, stack) : value;
                response.addHeader(entry.getKey(), finalValue);
            }
        }

        if (status == -1 && error != null) {
            int errorCode = -1;
            try {
                errorCode = Integer.parseInt(parse ? TextParseUtil.translateVariables(error, stack) : error);
            } catch (Exception e) {
                LOG.error("Cannot parse errorCode [{}] value as Integer!", error, e);
            }
            if (errorCode != -1) {
                if (errorMessage != null) {
                    String finalMessage = parse ? TextParseUtil.translateVariables(errorMessage, stack) : errorMessage;
                    response.sendError(errorCode, finalMessage);
                } else {
                    response.sendError(errorCode);
                }
            }
        }
    }
}
