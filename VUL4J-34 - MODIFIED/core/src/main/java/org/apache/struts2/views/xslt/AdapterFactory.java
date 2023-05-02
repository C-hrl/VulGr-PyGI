

package org.apache.struts2.views.xslt;

import org.apache.struts2.StrutsException;
import org.w3c.dom.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class AdapterFactory {

    private Map<Class, Class> adapterTypes = new HashMap<>();

    
    public void registerAdapterType(Class type, Class adapterType) {
        adapterTypes.put(type, adapterType);
    }

    
    public Document adaptDocument(String propertyName, Object propertyValue)
            throws IllegalAccessException, InstantiationException {
        return new SimpleAdapterDocument(this, null, propertyName, propertyValue);
    }


    
    public Node adaptNode(AdapterNode parent, String propertyName, Object value) {
        Class adapterClass = getAdapterForValue(value);
        if (adapterClass != null) {
            return constructAdapterInstance(adapterClass, parent, propertyName, value);
        }

        
        if (value instanceof Document) {
            value = ((Document) value).getDocumentElement();
        }

        
        if (value instanceof Node) {
            return proxyNode(parent, (Node) value);
        }

        
        Class valueType = value.getClass();

        if (valueType.isArray()) {
            adapterClass = ArrayAdapter.class;
        } else if (value instanceof String || value instanceof Number || value instanceof Boolean || valueType.isPrimitive()) {
            adapterClass = StringAdapter.class;
        } else if (value instanceof Collection) {
            adapterClass = CollectionAdapter.class;
        } else if (value instanceof Map) {
            adapterClass = MapAdapter.class;
        } else {
            adapterClass = BeanAdapter.class;
        }

        return constructAdapterInstance(adapterClass, parent, propertyName, value);
    }

    
    public Node proxyNode(AdapterNode parent, Node node) {
        
        if (node instanceof Document) {
            node = ((Document) node).getDocumentElement();
        }

        if (node == null) {
            return null;
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            return new ProxyElementAdapter(this, parent, (Element) node);
        }
        if (node.getNodeType() == Node.TEXT_NODE) {
            return new ProxyTextNodeAdapter(this, parent, (Text) node);
        }
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            return new ProxyAttrAdapter(this, parent, (Attr) node);
        }

        return null; 
    }

    public NamedNodeMap proxyNamedNodeMap(AdapterNode parent, NamedNodeMap nnm) {
        return new ProxyNamedNodeMap(this, parent, nnm);
    }

    
    private Node constructAdapterInstance(Class adapterClass, AdapterNode parent, String propertyName, Object propertyValue) {
        
        try {
            adapterClass.getConstructor(new Class []{});
        } catch (NoSuchMethodException e1) {
            throw new StrutsException("Adapter class: " + adapterClass + " does not have a no-args constructor.");
        }

        try {
            AdapterNode adapterNode = (AdapterNode) adapterClass.newInstance();
            adapterNode.setAdapterFactory(this);
            adapterNode.setParent(parent);
            adapterNode.setPropertyName(propertyName);
            adapterNode.setPropertyValue(propertyValue);

            return adapterNode;

        } catch (IllegalAccessException | InstantiationException e) {
            throw new StrutsException("Cannot adapt " + propertyValue + " (" + propertyName + ") :" + e.getMessage(), e);
        }
    }

    
    public Node adaptNullValue(AdapterNode parent, String propertyName) {
        return new StringAdapter(this, parent, propertyName, "null");
    }

    
    public Class getAdapterForValue(Object value) {
        return adapterTypes.get(value.getClass());
    }
}
