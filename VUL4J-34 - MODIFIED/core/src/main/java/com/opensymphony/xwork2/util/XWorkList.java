
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;



public class XWorkList extends ArrayList {
    private static final Logger LOG = LogManager.getLogger(XWorkConverter.class);

    private Class clazz;

    public XWorkList(Class clazz) {
        this.clazz = clazz;
    }

    public XWorkList(Class clazz, int initialCapacity) {
        super(initialCapacity);
        this.clazz = clazz;
    }

    
    @Override
    public void add(int index, Object element) {
        if (index >= this.size()) {
            get(index);
        }

        element = convert(element);

        super.add(index, element);
    }

    
    @Override
    public boolean add(Object element) {
        element = convert(element);

        return super.add(element);
    }

    
    @Override
    public boolean addAll(Collection collection) {
        if (collection == null) {
            throw new NullPointerException("Collection to add is null");
        }

        for (Object nextElement : collection) {
            add(nextElement);
        }

        return true;
    }

    
    @Override
    public boolean addAll(int index, Collection collection) {
        if (collection == null) {
            throw new NullPointerException("Collection to add is null");
        }

        boolean trim = false;

        if (index >= this.size()) {
            trim = true;
        }

        for (Iterator it = collection.iterator(); it.hasNext(); index++) {
            add(index, it.next());
        }

        if (trim) {
            remove(this.size() - 1);
        }

        return true;
    }

    
    @Override
    public synchronized Object get(int index) {
        while (index >= this.size()) {
            try {
                this.add(getObjectFactory().buildBean(clazz, ActionContext.getContext().getContextMap()));
            } catch (Exception e) {
                throw new XWorkException(e);
            }
        }

        return super.get(index);
    }

    private ObjectFactory getObjectFactory() {
        return ActionContext.getContext().getInstance(ObjectFactory.class);
    }

    
    @Override
    public Object set(int index, Object element) {
        if (index >= this.size()) {
            get(index);
        }

        element = convert(element);

        return super.set(index, element);
    }

    private Object convert(Object element) {
        if ((element != null) && !clazz.isAssignableFrom(element.getClass())) {
            
            LOG.debug("Converting from {} to {}", element.getClass().getName(), clazz.getName());
            TypeConverter converter = getTypeConverter();
            Map<String, Object> context = ActionContext.getContext().getContextMap();
            element = converter.convertValue(context, null, null, null, element, clazz);
        }

        return element;
    }

    private TypeConverter getTypeConverter() {
        return ActionContext.getContext().getContainer().getInstance(XWorkConverter.class);
    }

    @Override
    public boolean contains(Object element) {
        element = convert(element);
        return super.contains(element);
    }
}
