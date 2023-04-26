

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.TextProviderHelper;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@StrutsTag(
    name="text",
    tldTagClass="org.apache.struts2.views.jsp.TextTag",
    description="Render a I18n text message")
public class Text extends ContextBean implements Param.UnnamedParametric {
    private static final Logger LOG = LogManager.getLogger(Text.class);

    protected List values = Collections.EMPTY_LIST;
    protected String actualName;
    protected String name;
    protected String searchStack;

    public Text(ValueStack stack) {
        super(stack);
    }

    @StrutsTagAttribute(description = "Name of resource property to fetch", required = true)
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="Search the stack if property is not found on resources", type = "Boolean", defaultValue = "true")
    public void setSearchValueStack(String searchStack) {
        this.searchStack = searchStack;
    }

    public boolean usesBody() {
        
        
        
        return true;
    }

    public boolean end(Writer writer, String body) {
        actualName = findString(name, "name", "You must specify the i18n key. Example: welcome.header");
        String defaultMessage;
        if (StringUtils.isNotEmpty(body)) {
            defaultMessage = body;
        } else {
            defaultMessage = actualName;
        }

        Boolean doSearchStack = searchStack != null ? (Boolean) findValue(searchStack, Boolean.class) : true;
        String msg = TextProviderHelper.getText(actualName, defaultMessage, values, getStack(), doSearchStack == null || doSearchStack);

        if (msg != null) {
            try {
                if (getVar() == null) {
                    writer.write(msg);
                } else {
                    putInContext(msg);
                }
            } catch (IOException e) {
                LOG.error("Could not write out Text tag", e);
            }
        }

        return super.end(writer, "");
    }

    public void addParameter(String key, Object value) {
        addParameter(value);
    }

    public void addParameter(Object value) {
        if (values.isEmpty()) {
            values = new ArrayList(4);
        }

        values.add(value);
    }
}
