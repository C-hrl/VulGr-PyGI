

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class BeanAdapter extends AbstractAdapterElement {
    

    private static final Object[] NULLPARAMS = new Object[0];

    
    private static Map<Class, PropertyDescriptor[]> propertyDescriptorCache;

    

    private Logger log = LogManager.getLogger(this.getClass());

    

    public BeanAdapter() {
    }

    public BeanAdapter(
            AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(adapterFactory, parent, propertyName, value);
    }

    

    public String getTagName() {
        return getPropertyName();
    }

    public NodeList getChildNodes() {
        NodeList nl = super.getChildNodes();
        
        if (log.isDebugEnabled() && nl != null) {
            log.debug("BeanAdapter getChildNodes for: {}", getTagName());
            log.debug(nl.toString());
        }
        return nl;
    }

    protected List<Node> buildChildAdapters() {
        log.debug("BeanAdapter building children. Property name: {}", getPropertyName());
        List<Node> newAdapters = new ArrayList<>();
        Class type = getPropertyValue().getClass();
        PropertyDescriptor[] props = getPropertyDescriptors(getPropertyValue());

        if (props.length > 0) {
            for (PropertyDescriptor prop : props) {
                Method m = prop.getReadMethod();

                if (m == null) {
                    
                    continue;
                }
                log.debug("Bean reading property method: {}", m.getName());

                String propertyName = prop.getName();
                Object propertyValue;

                
                try {
                    propertyValue = m.invoke(getPropertyValue(), NULLPARAMS);
                } catch (Exception e) {
                    if (e instanceof InvocationTargetException) {
                        e = (Exception) ((InvocationTargetException) e).getTargetException();
                    }
                    log.error("Cannot access bean property: {}", propertyName, e);
                    continue;
                }

                Node childAdapter;

                if (propertyValue == null) {
                    childAdapter = getAdapterFactory().adaptNullValue(this, propertyName);
                } else {
                    childAdapter = getAdapterFactory().adaptNode(this, propertyName, propertyValue);
                }

                if (childAdapter != null)
                    newAdapters.add(childAdapter);

                log.debug("{} adding adapter: {}", this, childAdapter);
            }
        } else {
            
            log.info("Class {} has no readable properties, trying to adapt {} with StringAdapter...", type.getName(), getPropertyName());
        }

        return newAdapters;
    }

    
    private synchronized PropertyDescriptor[] getPropertyDescriptors(Object bean) {
        try {
            if (propertyDescriptorCache == null) {
                propertyDescriptorCache = new HashMap<Class, PropertyDescriptor[]>();
            }

            PropertyDescriptor[] props = propertyDescriptorCache.get(bean.getClass());

            if (props == null) {
                log.debug("Caching property descriptor for {}", bean.getClass().getName());
                props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
                propertyDescriptorCache.put(bean.getClass(), props);
            }

            return props;
        } catch (IntrospectionException e) {
            throw new StrutsException("Error getting property descriptors for " + bean + " : " + e.getMessage());
        }
    }
}
