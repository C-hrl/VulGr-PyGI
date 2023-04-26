

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsStatics;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;



public abstract class StrutsResultSupport implements Result, StrutsStatics {

    private static final Logger LOG = LogManager.getLogger(StrutsResultSupport.class);

    
    public static final String DEFAULT_PARAM = "location";

    
    public static final String DEFAULT_URL_ENCODING = "UTF-8";

    private boolean parse;
    private boolean encode;
    private String location;
    private String lastFinalLocation;

    public StrutsResultSupport() {
        this(null, true, false);
    }

    public StrutsResultSupport(String location) {
        this(location, true, false);
    }

    public StrutsResultSupport(String location, boolean parse, boolean encode) {
        this.location = location;
        this.parse = parse;
        this.encode = encode;
    }

    
    public void setLocation(String location) {
        this.location = location;
    }
    
    
    public String getLocation() {
        return location;
    }

    
    public String getLastFinalLocation() {
        return lastFinalLocation;
    }

    
    public void setParse(boolean parse) {
        this.parse = parse;
    }

    
    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    
    public void execute(ActionInvocation invocation) throws Exception {
        lastFinalLocation = conditionalParse(location, invocation);
        doExecute(lastFinalLocation, invocation);
    }

    
    protected String conditionalParse(String param, ActionInvocation invocation) {
        if (parse && param != null && invocation != null) {
            return TextParseUtil.translateVariables(
                param, 
                invocation.getStack(),
                new EncodingParsedValueEvaluator());
        } else {
            return param;
        }
    }

    
    protected Collection<String> conditionalParseCollection(String param, ActionInvocation invocation, boolean excludeEmptyElements) {
        if (parse && param != null && invocation != null) {
            return TextParseUtil.translateVariablesCollection(
                param, 
                invocation.getStack(),
                excludeEmptyElements,
                new EncodingParsedValueEvaluator());
        } else {
            Collection<String> collection = new ArrayList<>(1);
            collection.add(param);
            return collection;
        }
    }

    
    private final class EncodingParsedValueEvaluator implements TextParseUtil.ParsedValueEvaluator {
        public Object evaluate(String parsedValue) {
            if (encode) {
                if (parsedValue != null) {
                    try {
                        return URLEncoder.encode(parsedValue, DEFAULT_URL_ENCODING);
                    }
                    catch(UnsupportedEncodingException e) {
                        LOG.warn("error while trying to encode [{}]", parsedValue, e);
                    }
                }
            }
            return parsedValue;
        }
    }

    
    protected abstract void doExecute(String finalLocation, ActionInvocation invocation) throws Exception;
}
