

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.util.ContainUtil;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;


public abstract class ListUIBean extends UIBean {
    protected Object list;
    protected String listKey;
    protected String listValueKey;
    protected String listValue;
    protected String listLabelKey;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;

    
    protected boolean throwExceptionOnNullValueAttribute = false;

    protected ListUIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateExtraParams() {
        Object value = null;

        if (list == null) {
            list = parameters.get("list");
        }

        if (list instanceof String) {
            value = findValue((String) list);
        } else if (list instanceof Collection) {
            value = list;
        } else if (MakeIterator.isIterable(list)) {
            value = MakeIterator.convert(list);
        }
        if (value == null) {
            if (throwExceptionOnNullValueAttribute) {
                
                value = findValue((list == null) ? (String) list : list.toString(), "list",
                        "The requested list key '" + list + "' could not be resolved as a collection/array/map/enumeration/iterator type. " +
                                "Example: people or people.{name}");
            } else {
                
                
                value = findValue((list == null) ? (String) list : list.toString());
            }
        }

        if (value instanceof Collection) {
            addParameter("list", value);
        } else {
            addParameter("list", MakeIterator.convert(value));
        }

        if (value instanceof Collection) {
            addParameter("listSize", ((Collection) value).size());
        } else if (value instanceof Map) {
            addParameter("listSize", ((Map) value).size());
        } else if (value != null && value.getClass().isArray()) {
            addParameter("listSize", Array.getLength(value));
        }

        if (listKey != null) {
            listKey = stripExpressionIfAltSyntax(listKey);
            addParameter("listKey", listKey);
        } else if (value instanceof Map) {
            addParameter("listKey", "key");
        }

        if (listValueKey != null) {
            listValueKey = stripExpressionIfAltSyntax(listValueKey);
            addParameter("listValueKey", listValueKey);
        }

        if (listValue != null) {
            listValue = stripExpressionIfAltSyntax(listValue);
            addParameter("listValue", listValue);
        } else if (value instanceof Map) {
            addParameter("listValue", "value");
        }

        if (listLabelKey != null) {
            listLabelKey = stripExpressionIfAltSyntax(listLabelKey);
            addParameter("listLabelKey", listLabelKey);
        }

        if (StringUtils.isNotBlank(listCssClass)) {
            addParameter("listCssClass", listCssClass);
        }

        if (StringUtils.isNotBlank(listCssStyle)) {
            addParameter("listCssStyle", listCssStyle);
        }

        if (StringUtils.isNotBlank(listTitle)) {
            addParameter("listTitle", listTitle);
        }
    }

    public boolean contains(Object obj1, Object obj2) {
        return ContainUtil.contains(obj1, obj2);
    }

    protected Class getValueClassType() {
        return null; 
    }

    @StrutsTagAttribute(description = "Iterable source to populate from. If the list is a Map (key, value), the Map key will become the option 'value'" +
            " parameter and the Map value will become the option body.", required = true)
    public void setList(Object list) {
        this.list = list;
    }

    @StrutsTagAttribute(description = "Property of list objects to get field value from")
    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    @StrutsTagAttribute(description = "Property of list objects to get field value label from")
    public void setListValueKey(String listValueKey) {
        this.listValueKey = listValueKey;
    }

    @StrutsTagAttribute(description = "Property of list objects to get field content from")
    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    @StrutsTagAttribute(description = "Property of list objects to be used to lookup for localised version of field label")
    public void setListLabelKey(String listLabelKey) {
        this.listLabelKey = listLabelKey;
    }

    @StrutsTagAttribute(description = "Property of list objects to get css class from")
    public void setListCssClass(String listCssClass) {
        this.listCssClass = listCssClass;
    }

    @StrutsTagAttribute(description = "Property of list objects to get css style from")
    public void setListCssStyle(String listCssStyle) {
        this.listCssStyle = listCssStyle;
    }

    @StrutsTagAttribute(description = "Property of list objects to get title from")
    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }


    public void setThrowExceptionOnNullValueAttribute(boolean throwExceptionOnNullValueAttribute) {
        this.throwExceptionOnNullValueAttribute = throwExceptionOnNullValueAttribute;
    }
}
