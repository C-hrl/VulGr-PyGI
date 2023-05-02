

package org.apache.struts2.util;

import org.apache.struts2.ServletActionContext;

import javax.servlet.jsp.PageContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;



public class AttributeMap implements Map {

    protected static final String UNSUPPORTED = "method makes no sense for a simplified map";

    Map context;

    public AttributeMap(Map context) {
        this.context = context;
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public void clear() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public boolean containsKey(Object key) {
        return (get(key) != null);
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Set entrySet() {
        return Collections.EMPTY_SET;
    }

    public Object get(Object key) {
        PageContext pc = getPageContext();

        if (pc == null) {
            Map request = (Map) context.get("request");
            Map session = (Map) context.get("session");
            Map application = (Map) context.get("application");

            if ((request != null) && (request.get(key) != null)) {
                return request.get(key);
            } else if ((session != null) && (session.get(key) != null)) {
                return session.get(key);
            } else if ((application != null) && (application.get(key) != null)) {
                return application.get(key);
            }
        } else {
            try{
                return pc.findAttribute(key.toString());
            }catch (NullPointerException npe){
                return null;
            }
        }

        return null;
    }

    public Set keySet() {
        return Collections.EMPTY_SET;
    }

    public Object put(Object key, Object value) {
        PageContext pc = getPageContext();
        if (pc != null) {
            pc.setAttribute(key.toString(), value);
        }

        return null;
    }

    public void putAll(Map t) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public int size() {
        throw new UnsupportedOperationException(UNSUPPORTED);
    }

    public Collection values() {
        return Collections.EMPTY_SET;
    }

    private PageContext getPageContext() {
        return (PageContext) context.get(ServletActionContext.PAGE_CONTEXT);
    }

    @Override
    public String toString() {
        return "AttributeMap {" +
                "request=" + toStringSafe(context.get("request")) +
                ", session=" +  toStringSafe(context.get("session")) +
                ", application=" + toStringSafe(context.get("application")) +
                '}';
    }

    private String toStringSafe(Object obj) {
        try {
            if (obj != null) {
                return String.valueOf(obj);
            }
            return "";
        } catch (Exception e) {
            return "Exception thrown: " + e;
        }
    }

}
