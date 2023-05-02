

package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.SetPropertyAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class XWorkCollectionPropertyAccessor extends SetPropertyAccessor {

    private static final Logger LOG = LogManager.getLogger(XWorkCollectionPropertyAccessor.class);

    public static final String KEY_PROPERTY_FOR_CREATION = "makeNew";

    
    
    
    private ObjectPropertyAccessor _accessor = new ObjectPropertyAccessor();
    
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;
    private OgnlUtil ognlUtil;
    
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
    public Object getProperty(Map context, Object target, Object key) throws OgnlException {
        LOG.trace("Entering getProperty()");

        
        
        
        if (!ReflectionContextState.isGettingByKeyProperty(context) && !key.equals(KEY_PROPERTY_FOR_CREATION)) {
            return super.getProperty(context, target, key);
        }	else {
            
            ReflectionContextState.setGettingByKeyProperty(context,false);
        }
        Collection c = (Collection) target;

        
        Class lastBeanClass = ReflectionContextState.getLastBeanClassAccessed(context);

        
        String lastPropertyClass = ReflectionContextState.getLastBeanPropertyAccessed(context);

        
        
        
        if (lastBeanClass == null || lastPropertyClass == null) {
            ReflectionContextState.updateCurrentPropertyPath(context, key);
            return super.getProperty(context, target, key);
        }

        
        
        String keyProperty = objectTypeDeterminer.getKeyProperty(lastBeanClass, lastPropertyClass);

        
        Class collClass = objectTypeDeterminer.getElementClass(lastBeanClass, lastPropertyClass, key);

        Class keyType;
        Class toGetTypeFrom = (collClass != null) ? collClass : c.iterator().next().getClass();
        try {
            keyType = OgnlRuntime.getPropertyDescriptor(toGetTypeFrom, keyProperty).getPropertyType();
        } catch (Exception exc) {
            throw new OgnlException("Error getting property descriptor: " + exc.getMessage());
        }


        if (ReflectionContextState.isCreatingNullObjects(context)) {
            Map collMap = getSetMap(context, c, keyProperty);
            if (key.toString().equals(KEY_PROPERTY_FOR_CREATION)) {
                
                
                
                
                return collMap.get(null);
            }
            Object realKey = xworkConverter.convertValue(context, key, keyType);
            Object value = collMap.get(realKey);
            if (value == null
                    && ReflectionContextState.isCreatingNullObjects(context)
                    && objectTypeDeterminer
                    .shouldCreateIfNew(lastBeanClass,lastPropertyClass,c,keyProperty,false)) {
                	
                    
                    
                	try {
                	    value=objectFactory.buildBean(collClass, context);
                	    
                	    
                	    _accessor.setProperty(context,value,keyProperty,realKey);
                	    
                	    
                	    c.add(value);
                	    
                	    
                	    collMap.put(realKey, value);
                	    
                	    
                	}	catch (Exception exc) {
                	    throw new OgnlException("Error adding new element to collection", exc);
                	}
                
            }
            return value;
        } else {
            if (key.toString().equals(KEY_PROPERTY_FOR_CREATION)) {
                return null;
            }
            
            
            
            
            
            Object realKey = xworkConverter.convertValue(context, key, keyType);
            return getPropertyThroughIteration(context, c, keyProperty, realKey);
        }
    }

    
    private Map getSetMap(Map context, Collection collection, String property) throws OgnlException {
        LOG.trace("getting set Map");

        String path = ReflectionContextState.getCurrentPropertyPath(context);
        Map map = ReflectionContextState.getSetMap(context, path);

        if (map == null) {
            LOG.trace("creating set Map");

            map = new HashMap();
            map.put(null, new SurrugateList(collection));
            for (Object currTest : collection) {
                Object currKey = _accessor.getProperty(context, currTest, property);
                if (currKey != null) {
                    map.put(currKey, currTest);
                }
            }
            ReflectionContextState.setSetMap(context, map, path);
        }
        return map;
    }

    
    public Object getPropertyThroughIteration(Map context, Collection collection, String property, Object key)
            throws OgnlException {
        
        for (Object currTest : collection) {
            if (_accessor.getProperty(context, currTest, property).equals(key)) {
                return currTest;
            }
        }
        
        return null;
    }

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
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

        super.setProperty(context, target, name, realValue);
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return xworkConverter.convertValue(context, value, convertToClass);
    }  
}


class SurrugateList extends ArrayList {

    private Collection surrugate;

    public SurrugateList(Collection surrugate) {
        this.surrugate = surrugate;
    }

    @Override
    public void add(int arg0, Object arg1) {
        if (arg1 != null) {
            surrugate.add(arg1);
        }
        super.add(arg0, arg1);
    }

    @Override
    public boolean add(Object arg0) {
        if (arg0 != null) {
            surrugate.add(arg0);
        }
        return super.add(arg0);
    }

    @Override
    public boolean addAll(Collection arg0) {
        surrugate.addAll(arg0);
        return super.addAll(arg0);
    }

    @Override
    public boolean addAll(int arg0, Collection arg1) {
        surrugate.addAll(arg1);
        return super.addAll(arg0, arg1);
    }

    @Override
    public Object set(int arg0, Object arg1) {
        if (arg1 != null) {
            surrugate.add(arg1);
        }
        return super.set(arg0, arg1);
    }
}
