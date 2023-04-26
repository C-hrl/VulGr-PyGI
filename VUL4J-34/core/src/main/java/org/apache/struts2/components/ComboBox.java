

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


@StrutsTag(name="combobox", tldTagClass="org.apache.struts2.views.jsp.ui.ComboBoxTag", description="Widget that fills a text box from a select")
public class ComboBox extends TextField {
    final public static String TEMPLATE = "combobox";

    protected String list;
    protected String listKey;
    protected String listValue;
    protected String headerKey;
    protected String headerValue;
    protected String emptyOption;


    public ComboBox(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        Object value = findListValue();

        if (headerKey != null) {
            addParameter("headerKey", findString(headerKey));
        }
        if (headerValue != null) {
            addParameter("headerValue", findString(headerValue));
        }
        if (emptyOption != null) {
            addParameter("emptyOption", findValue(emptyOption, Boolean.class));
        }

        if (value != null) {
            if (value instanceof Collection) {
                Collection tmp = (Collection) value;
                addParameter("list", tmp);
                if (listKey != null) {
                    addParameter("listKey", listKey);
                }
                if (listValue != null) {
                    addParameter("listValue", listValue);
                }
            } else if (value instanceof Map) {
                Map tmp = (Map) value;
                addParameter("list", MakeIterator.convert(tmp));
                addParameter("listKey", "key");
                addParameter("listValue", "value");                
            } else { 
                Iterator i = MakeIterator.convert(value);
                addParameter("list", i);
                if (listKey != null) {
                    addParameter("listKey", listKey);
                }
                if (listValue != null) {
                    addParameter("listValue", listValue);
                }
            }
        }
    }

    protected Object findListValue() {
        return findValue(list, "list",
                "You must specify a collection/array/map/enumeration/iterator. " +
                "Example: people or people.{name}");
    }

    @StrutsTagAttribute(description = "Iterable source to populate from. " +
                "If this is missing, the select widget is simply not displayed.", required=true)
    public void setList(String list) {
        this.list = list;
    }

    @StrutsTagAttribute(description="Decide if an empty option is to be inserted. Default false.")
    public void setEmptyOption(String emptyOption) {
        this.emptyOption = emptyOption;
    }

    @StrutsTagAttribute(description="Set the header key for the header option.")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    @StrutsTagAttribute(description="Set the header value for the header option.")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    @StrutsTagAttribute(description = "Set the key used to retrieve the option key.")
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    @StrutsTagAttribute(description = "Set the value used to retrieve the option value.")
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }


}
