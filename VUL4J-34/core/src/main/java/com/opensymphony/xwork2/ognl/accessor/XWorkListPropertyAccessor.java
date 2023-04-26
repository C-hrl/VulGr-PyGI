
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ListPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class XWorkListPropertyAccessor extends ListPropertyAccessor {

    private XWorkCollectionPropertyAccessor _sAcc = new XWorkCollectionPropertyAccessor();
    
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;
    private OgnlUtil ognlUtil;
    
    @Inject("java.util.Collection")
    public void setXWorkCollectionPropertyAccessor(PropertyAccessor acc) {
        this._sAcc = (XWorkCollectionPropertyAccessor) acc;
    }
    
    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }
    
    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer ot) {
        this.objectTypeDeterminer = ot;
    }
    
    @Inject
    public void setOgnlUtil(OgnlUtil util) {
        this.ognlUtil = util;
    }

    @Override
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {

        if (ReflectionContextState.isGettingByKeyProperty(context)
                || name.equals(XWorkCollectionPropertyAccessor.KEY_PROPERTY_FOR_CREATION)) {
            return _sAcc.getProperty(context, target, name);
        } else if (name instanceof String) {
            return super.getProperty(context, target, name);
        }
        ReflectionContextState.updateCurrentPropertyPath(context, name);
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        
        if (name instanceof Number
                && ReflectionContextState.isCreatingNullObjects(context)
                && objectTypeDeterminer.shouldCreateIfNew(lastClass,lastProperty,target,null,true)) {

            List list = (List) target;
            int index = ((Number) name).intValue();
            int listSize = list.size();

            if (lastClass == null || lastProperty == null) {
                return super.getProperty(context, target, name);
            }
            Class beanClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);
            if (listSize <= index) {
                Object result;

                for (int i = listSize; i < index; i++) {
                    list.add(null);
                }
                try {
                    list.add(index, result = objectFactory.buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            } else if (list.get(index) == null) {
                Object result;
                try {
                    list.set(index, result = objectFactory.buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            }
        }
        return super.getProperty(context, target, name);
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value)
            throws OgnlException {

        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        Class convertToClass = objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);

        if (name instanceof String && value.getClass().isArray()) {
            
            
            

            Collection c = (Collection) target;
            Object[] values = (Object[]) value;
            for (Object v : values) {
                try {
                    Object o = objectFactory.buildBean(convertToClass, context);
                    ognlUtil.setValue((String) name, context, o, v);
                    c.add(o);
                } catch (Exception e) {
                    throw new OgnlException("Error converting given String values for Collection.", e);
                }
            }

            
            
            return;
        }

        Object realValue = getRealValue(context, value, convertToClass);

        if (target instanceof List && name instanceof Number) {
            
            List list = (List) target;
            int listSize = list.size();
            int count = ((Number) name).intValue();
            if (count >= listSize) {
                for (int i = listSize; i <= count; i++) {
                    list.add(null);
                }
            }
        }

        super.setProperty(context, target, name, realValue);
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return xworkConverter.convertValue(context, value, convertToClass);
    }
}
