

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.IOException;
import java.io.Writer;


@StrutsTag(name="property", tldBodyContent="empty", tldTagClass="org.apache.struts2.views.jsp.PropertyTag",
    description="Print out expression which evaluates against the stack")
public class Property extends Component {
    private static final Logger LOG = LogManager.getLogger(Property.class);

    public Property(ValueStack stack) {
        super(stack);
    }

    private String defaultValue;
    private String value;
    private boolean escapeHtml = true;
    private boolean escapeJavaScript = false;
    private boolean escapeXml = false;
    private boolean escapeCsv = false;

    @StrutsTagAttribute(description="The default value to be used if <u>value</u> attribute is null")
    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @StrutsTagAttribute(description="Whether to escape HTML", type="Boolean", defaultValue="true")
    public void setEscapeHtml(boolean escape) {
        this.escapeHtml = escape;
    }

    @StrutsTagAttribute(description="Whether to escape Javascript", type="Boolean", defaultValue="false")
    public void setEscapeJavaScript(boolean escapeJavaScript) {
        this.escapeJavaScript = escapeJavaScript;
    }

    @StrutsTagAttribute(description="Value to be displayed", type="Object", defaultValue="&lt;top of stack&gt;")
    public void setValue(String value) {
        this.value = value;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @StrutsTagAttribute(description="Whether to escape CSV (useful to escape a value for a column)", type="Boolean", defaultValue="false")
    public void setEscapeCsv(boolean escapeCsv) {
        this.escapeCsv = escapeCsv;
    }

    @StrutsTagAttribute(description="Whether to escape XML", type="Boolean", defaultValue="false")
    public void setEscapeXml(boolean escapeXml) {
        this.escapeXml = escapeXml;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        String actualValue = null;

        if (value == null) {
            value = "top";
        }
        else {
        	value = stripExpressionIfAltSyntax(value);
        }

        
        
        
        actualValue = (String) getStack().findValue(value, String.class, throwExceptionOnELFailure);

        try {
            if (actualValue != null) {
                writer.write(prepare(actualValue));
            } else if (defaultValue != null) {
                writer.write(prepare(defaultValue));
            }
        } catch (IOException e) {
            LOG.info("Could not print out value '{}'", value, e);
        }

        return result;
    }

    private String prepare(String value) {
    	String result = value;
        if (escapeHtml) {
        	result = StringEscapeUtils.escapeHtml4(result);
        }
        if (escapeJavaScript) {
        	result = StringEscapeUtils.escapeEcmaScript(result);
        }
        if (escapeXml) {
        	result = StringEscapeUtils.escapeXml(result);
        }
        if (escapeCsv) {
            result = StringEscapeUtils.escapeCsv(result);
        }

        return result;
    }
}
