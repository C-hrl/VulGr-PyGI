
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.CreateIfNull;
import com.opensymphony.xwork2.util.Element;
import com.opensymphony.xwork2.util.Key;
import com.opensymphony.xwork2.util.KeyProperty;
import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;


public class DefaultObjectTypeDeterminer implements ObjectTypeDeterminer {

    protected static final Logger LOG = LogManager.getLogger(DefaultObjectTypeDeterminer.class);

    public static final String KEY_PREFIX = "Key_";
    public static final String ELEMENT_PREFIX = "Element_";
    public static final String KEY_PROPERTY_PREFIX = "KeyProperty_";
    public static final String CREATE_IF_NULL_PREFIX = "CreateIfNull_";
    public static final String DEPRECATED_ELEMENT_PREFIX = "Collection_";

    private ReflectionProvider reflectionProvider;
    private XWorkConverter xworkConverter;

    @Inject
    public DefaultObjectTypeDeterminer(@Inject XWorkConverter converter, @Inject ReflectionProvider provider) {
        this.reflectionProvider = provider;
        this.xworkConverter = converter;
    }

    
    public Class getKeyClass(Class parentClass, String property) {
        Key annotation = getAnnotation(parentClass, property, Key.class);
        if (annotation != null) {
            return annotation.value();
        }
        Class clazz = getClass(parentClass, property, false);
        if (clazz != null) {
            return clazz;
        }
        return (Class) xworkConverter.getConverter(parentClass, KEY_PREFIX + property);
    }

    
    public Class getElementClass(Class parentClass, String property, Object key) {
        Element annotation = getAnnotation(parentClass, property, Element.class);
        if (annotation != null) {
            return annotation.value();
        }
        Class clazz = getClass(parentClass, property, true);
        if (clazz != null) {
            return clazz;
        }
        clazz = (Class) xworkConverter.getConverter(parentClass, ELEMENT_PREFIX + property);
        if (clazz == null) {
            clazz = (Class) xworkConverter.getConverter(parentClass, DEPRECATED_ELEMENT_PREFIX + property);
            if (clazz != null) {
                LOG.info("The Collection_xxx pattern for collection type conversion is deprecated. Please use Element_xxx!");
            }
        }
        return clazz;
    }

    
    public String getKeyProperty(Class parentClass, String property) {
        KeyProperty annotation = getAnnotation(parentClass, property, KeyProperty.class);
        if (annotation != null) {
            return annotation.value();
        }
        return (String) xworkConverter.getConverter(parentClass, KEY_PROPERTY_PREFIX + property);
    }

    
    public boolean shouldCreateIfNew(Class parentClass, String property, Object target, String keyProperty, boolean isIndexAccessed) {
        CreateIfNull annotation = getAnnotation(parentClass, property, CreateIfNull.class);
        if (annotation != null) {
            return annotation.value();
        }
        String configValue = (String) xworkConverter.getConverter(parentClass, CREATE_IF_NULL_PREFIX + property);
        
        if (configValue != null) {
            return BooleanUtils.toBoolean(configValue);
        }

        
        
        
        return (target instanceof Map) || isIndexAccessed;
    }

    
    protected <T extends Annotation> T getAnnotation(Class parentClass, String property, Class<T> annotationClass) {
        T annotation = null;
        Field field = reflectionProvider.getField(parentClass, property);

        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
        }
        if (annotation == null) { 
            annotation = getAnnotationFromSetter(parentClass, property, annotationClass);
        }
        if (annotation == null) { 
            annotation = getAnnotationFromGetter(parentClass, property, annotationClass);
        }

        return annotation;
    }

    
    private <T extends Annotation> T getAnnotationFromGetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method getter = reflectionProvider.getGetMethod(parentClass, property);

            if (getter != null) {
                return getter.getAnnotation(annotationClass);
            }
        } catch (ReflectionException | IntrospectionException e) {
            
        }
        return null;
    }

    
    private <T extends Annotation> T getAnnotationFromSetter(Class parentClass, String property, Class<T> annotationClass) {
        try {
            Method setter = reflectionProvider.getSetMethod(parentClass, property);

            if (setter != null) {
                return setter.getAnnotation(annotationClass);
            }
        } catch (ReflectionException | IntrospectionException e) {
            
        }
        return null;
    }

    
    private Class getClass(Class parentClass, String property, boolean element) {
        try {
            Field field = reflectionProvider.getField(parentClass, property);
            Type genericType = null;
            
            if (field != null) {
                genericType = field.getGenericType();
            }
            
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method setter = reflectionProvider.getSetMethod(parentClass, property);
                    genericType = setter != null ? setter.getGenericParameterTypes()[0] : null;
                } catch (ReflectionException | IntrospectionException e) {
                    
                }
            }

            
            if (genericType == null || !(genericType instanceof ParameterizedType)) {
                try {
                    Method getter = reflectionProvider.getGetMethod(parentClass, property);
                    genericType = getter.getGenericReturnType();
                } catch (ReflectionException | IntrospectionException e) {
                    
                }
            }

            if (genericType instanceof ParameterizedType) {
                ParameterizedType type = (ParameterizedType) genericType;
                int index = (element && type.getRawType().toString().contains(Map.class.getName())) ? 1 : 0;
                Type resultType = type.getActualTypeArguments()[index];
                if (resultType instanceof ParameterizedType) {
                    return (Class) ((ParameterizedType) resultType).getRawType();
                }
                return (Class) resultType;
            }
        } catch (Exception e) {
            LOG.debug("Error while retrieving generic property class for property: {}", property, e);
        }
        return null;
    }
}
