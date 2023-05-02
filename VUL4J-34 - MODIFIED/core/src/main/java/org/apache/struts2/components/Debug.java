

package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.StrutsException;

@StrutsTag(name="debug", tldTagClass="org.apache.struts2.views.jsp.ui.DebugTag",
        description="Prints debugging information")
public class Debug extends UIBean {
    public static final String TEMPLATE = "debug";
    
    protected ReflectionProvider reflectionProvider;

    

    public Debug(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }
    
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        ValueStack stack = getStack();
        Iterator iter = stack.getRoot().iterator();
        List stackValues = new ArrayList(stack.getRoot().size());
        while (iter.hasNext()) {
            Object o = iter.next();
            Map values;
            try {
                values = reflectionProvider.getBeanMap(o);
            } catch (Exception e) {
                throw new StrutsException("Caught an exception while getting the property values of " + o, e);
            }
            stackValues.add(new DebugMapEntry(o.getClass().getName(), values));
        }

        addParameter("stackValues", stackValues);

        return result;
    }

    private static class DebugMapEntry implements Map.Entry {
        private Object key;
        private Object value;

        DebugMapEntry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public Object setValue(Object newVal) {
            Object oldVal = value;
            value = newVal;
            return oldVal;
        }
    }

}
