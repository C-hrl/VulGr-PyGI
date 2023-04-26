

package org.apache.struts2.interceptor.debugging;

import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;

import java.beans.IntrospectionException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


class ObjectToHTMLWriter {
    private PrettyPrintWriter prettyWriter;

    ObjectToHTMLWriter(Writer writer) {
        this.prettyWriter = new PrettyPrintWriter(writer);
        this.prettyWriter.setEscape(false);
    }

    @SuppressWarnings("unchecked")
    public void write(ReflectionProvider reflectionProvider, Object root, String expr) throws IntrospectionException,
        ReflectionException {
        prettyWriter.startNode("table");
        prettyWriter.addAttribute("class", "debugTable");

        if (root instanceof Map) {
            for (Object next : ((Map) root).entrySet()) {
                Map.Entry property = (Map.Entry) next;
                String key = property.getKey().toString();
                Object value = property.getValue();
                writeProperty(key, value, expr);
            }
        } else if (root instanceof List) {
            List list = (List) root;
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                writeProperty(String.valueOf(i), element, expr);
            }
        } else if (root instanceof Set) {
            Set set = (Set) root;
            for (Object next : set) {
                writeProperty("", next, expr);
            }
        } else if (root.getClass().isArray()) {
            Object[] objects = (Object[]) root;
            for (int i = 0; i < objects.length; i++) {
                writeProperty(String.valueOf(i), objects[i], expr);
            }
        } else {
            
            Map<String, Object> properties = reflectionProvider.getBeanMap(root);
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                String name = property.getKey();
                Object value = property.getValue();

                if ("class".equals(name)) {
                    continue;
                }

                writeProperty(name, value, expr);
            }
        }

        prettyWriter.endNode();
    }

    private void writeProperty(String name, Object value, String expr) {
        prettyWriter.startNode("tr");

        
        prettyWriter.startNode("td");
        prettyWriter.addAttribute("class", "nameColumn");
        prettyWriter.setValue(name);
        prettyWriter.endNode();

        
        prettyWriter.startNode("td");
        if (value != null) {
            
            if (isEmptyCollection(value) || isEmptyMap(value) || (value.getClass()
                    .isArray() && ((Object[]) value).length == 0)) {
                prettyWriter.addAttribute("class", "emptyCollection");
                prettyWriter.setValue("empty");
            } else {
                prettyWriter.addAttribute("class", "valueColumn");
                writeValue(name, value, expr);
            }
        } else {
            prettyWriter.addAttribute("class", "nullValue");
            prettyWriter.setValue("null");
        }
        prettyWriter.endNode();

        
        prettyWriter.startNode("td");
        if (value != null) {
            prettyWriter.addAttribute("class", "typeColumn");
            Class clazz = value.getClass();
            prettyWriter.setValue(clazz.getName());
        } else {
            prettyWriter.addAttribute("class", "nullValue");
            prettyWriter.setValue("unknown");
        }
        prettyWriter.endNode();

        
        prettyWriter.endNode();
    }

    
    private boolean isEmptyMap(Object value) {
        try {
            return value instanceof Map && ((Map) value).isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    
    private boolean isEmptyCollection(Object value) {
        try {
            return value instanceof Collection && ((Collection) value).isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    private void writeValue(String name, Object value, String expr) {
        Class clazz = value.getClass();
        if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) ||
            clazz.equals(String.class) || Boolean.class.equals(clazz)) {
            prettyWriter.setValue(String.valueOf(value));
        } else {
            prettyWriter.startNode("a");
            String path = expr.replaceAll("#", "%23") + "[\"" +
                name.replaceAll("#", "%23") + "\"]";
            prettyWriter.addAttribute("onclick", "expand(this, '" + path + "')");
            prettyWriter.addAttribute("href", "javascript:
            prettyWriter.setValue("Expand");
            prettyWriter.endNode();
        }
    }
}
