
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.NullHandler;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.util.*;



public class InstantiatingNullHandler implements NullHandler {

    private static final Logger LOG = LogManager.getLogger(InstantiatingNullHandler.class);

    private ReflectionProvider reflectionProvider;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer det) {
        this.objectTypeDeterminer = det;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    public Object nullMethodResult(Map<String, Object> context, Object target, String methodName, Object[] args) {
        LOG.debug("Entering nullMethodResult");
        return null;
    }

    public Object nullPropertyValue(Map<String, Object> context, Object target, Object property) {
        LOG.debug("Entering nullPropertyValue [target={}, property={}]", target, property);
        boolean c = ReflectionContextState.isCreatingNullObjects(context);

        if (!c) {
            return null;
        }

        if ((target == null) || (property == null)) {
            return null;
        }

        try {
            String propName = property.toString();
            Object realTarget = reflectionProvider.getRealTarget(propName, context, target);
            Class clazz = null;

            if (realTarget != null) {
                PropertyDescriptor pd = reflectionProvider.getPropertyDescriptor(realTarget.getClass(), propName);
                if (pd == null) {
                    return null;
                }

                clazz = pd.getPropertyType();
            }

            if (clazz == null) {
                
                return null;
            }

            Object param = createObject(clazz, realTarget, propName, context);

            reflectionProvider.setValue(propName, context, realTarget, param);

            return param;
        } catch (Exception e) {
            LOG.error("Could not create and/or set value back on to object", e);
        }

        return null;
    }

    private Object createObject(Class clazz, Object target, String property, Map<String, Object> context) throws Exception {
        if (Set.class.isAssignableFrom(clazz)) {
            return new HashSet();
        } else if (Collection.class.isAssignableFrom(clazz)) {
            return new ArrayList();
        } else if (clazz == Map.class) {
            return new HashMap();
        } else if (clazz == EnumMap.class) {
            Class keyClass = objectTypeDeterminer.getKeyClass(target.getClass(), property);
            return new EnumMap(keyClass);
        }

        return objectFactory.buildBean(clazz, context);
    }
}
