
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.conversion.*;
import com.opensymphony.xwork2.conversion.annotations.Conversion;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.*;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class XWorkConverter extends DefaultTypeConverter {

    private static final Logger LOG = LogManager.getLogger(XWorkConverter.class);

    public static final String REPORT_CONVERSION_ERRORS = "report.conversion.errors";
    public static final String CONVERSION_PROPERTY_FULLNAME = "conversion.property.fullName";
    public static final String CONVERSION_ERROR_PROPERTY_PREFIX = "invalid.fieldvalue.";
    public static final String CONVERSION_COLLECTION_PREFIX = "Collection_";

    public static final String LAST_BEAN_CLASS_ACCESSED = "last.bean.accessed";
    public static final String LAST_BEAN_PROPERTY_ACCESSED = "last.property.accessed";
    public static final String MESSAGE_INDEX_PATTERN = "\\[\\d+\\]\\.";
    public static final String MESSAGE_INDEX_BRACKET_PATTERN = "[\\[\\]\\.]";
    public static final String PERIOD = ".";
    public static final Pattern messageIndexPattern = Pattern.compile(MESSAGE_INDEX_PATTERN);

    private TypeConverter defaultTypeConverter;
    private FileManager fileManager;
    private boolean reloadingConfigs;

    private ConversionFileProcessor fileProcessor;
    private ConversionAnnotationProcessor annotationProcessor;

    private TypeConverterHolder converterHolder;

    protected XWorkConverter() {
    }

    @Inject
    public void setDefaultTypeConverter(XWorkBasicConverter converter) {
        this.defaultTypeConverter = converter;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(value = XWorkConstants.RELOAD_XML_CONFIGURATION, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Inject
    public void setConversionPropertiesProcessor(ConversionPropertiesProcessor propertiesProcessor) {
        
        propertiesProcessor.process("xwork-default-conversion.properties");
        propertiesProcessor.process("xwork-conversion.properties");
    }

    @Inject
    public void setConversionFileProcessor(ConversionFileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    @Inject
    public void setConversionAnnotationProcessor(ConversionAnnotationProcessor annotationProcessor) {
        this.annotationProcessor = annotationProcessor;
    }

    @Inject
    public void setTypeConverterHolder(TypeConverterHolder converterHolder) {
        this.converterHolder = converterHolder;
    }

    public static String getConversionErrorMessage(String propertyName, ValueStack stack) {
        String defaultMessage = LocalizedTextUtil.findDefaultText(XWorkMessages.DEFAULT_INVALID_FIELDVALUE,
                ActionContext.getContext().getLocale(),
                new Object[]{
                        propertyName
                });

        List<String> indexValues = getIndexValues(propertyName);

        propertyName = removeAllIndexesInPropertyName(propertyName);

        String getTextExpression = "getText('" + CONVERSION_ERROR_PROPERTY_PREFIX + propertyName + "','" + defaultMessage + "')";
        String message = (String) stack.findValue(getTextExpression);

        if (message == null) {
            message = defaultMessage;
        } else {
            message = MessageFormat.format(message, indexValues.toArray());
        }

        return message;
    }

    private static String removeAllIndexesInPropertyName(String propertyName) {
        return propertyName.replaceAll(MESSAGE_INDEX_PATTERN, PERIOD);
    }

    private static List<String> getIndexValues(String propertyName) {
        Matcher matcher = messageIndexPattern.matcher(propertyName);
        List<String> indexes = new ArrayList<>();
        while (matcher.find()) {
            Integer index = new Integer(matcher.group().replaceAll(MESSAGE_INDEX_BRACKET_PATTERN, "")) + 1;
            indexes.add(Integer.toString(index));
        }
        return indexes;
    }

    public String buildConverterFilename(Class clazz) {
        String className = clazz.getName();
        return className.replace('.', '/') + "-conversion.properties";
    }

    @Override
    public Object convertValue(Map<String, Object> map, Object o, Class aClass) {
        return convertValue(map, null, null, null, o, aClass);
    }

    
    @Override
    public Object convertValue(Map<String, Object> context, Object target, Member member, String property, Object value, Class toClass) {
        
        
        
        TypeConverter tc = null;

        if ((value != null) && (toClass == value.getClass())) {
            return value;
        }

        
        
        if (target != null) {
            Class clazz = target.getClass();

            Object[] classProp = null;

            
            if ((target instanceof CompoundRoot) && (context != null)) {
                classProp = getClassProperty(context);
            }

            if (classProp != null) {
                clazz = (Class) classProp[0];
                property = (String) classProp[1];
            }

            tc = (TypeConverter) getConverter(clazz, property);
            LOG.debug("field-level type converter for property [{}] = {}", property, (tc == null ? "none found" : tc));
        }

        if (tc == null && context != null) {
            
            Object lastPropertyPath = context.get(ReflectionContextState.CURRENT_PROPERTY_PATH);
            Class clazz = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
            if (lastPropertyPath != null && clazz != null) {
                String path = lastPropertyPath + "." + property;
                tc = (TypeConverter) getConverter(clazz, path);
            }
        }

        if (tc == null) {
            if (toClass.equals(String.class) && (value != null) && !(value.getClass().equals(String.class) || value.getClass().equals(String[].class))) {
                
                tc = lookup(value.getClass());
            } else {
                
                tc = lookup(toClass);
            }

            if (LOG.isDebugEnabled())
                LOG.debug("global-level type converter for property [{}] = {} ", property, (tc == null ? "none found" : tc));
        }


        if (tc != null) {
            try {
                return tc.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                LOG.debug("Unable to convert value using type converter [{}]", tc.getClass().getName(), e);
                handleConversionException(context, property, value, target);

                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        }

        if (defaultTypeConverter != null) {
            try {
                LOG.debug("Falling back to default type converter [{}]", defaultTypeConverter);
                return defaultTypeConverter.convertValue(context, target, member, property, value, toClass);
            } catch (Exception e) {
                LOG.debug("Unable to convert value using type converter [{}]", defaultTypeConverter.getClass().getName(), e);
                handleConversionException(context, property, value, target);

                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        } else {
            try {
                LOG.debug("Falling back to Ognl's default type conversion");
                return super.convertValue(value, toClass);
            } catch (Exception e) {
                LOG.debug("Unable to convert value using type converter [{}]", super.getClass().getName(), e);
                handleConversionException(context, property, value, target);

                return TypeConverter.NO_CONVERSION_POSSIBLE;
            }
        }
    }

    
    public TypeConverter lookup(String className, boolean isPrimitive) {
        if (converterHolder.containsUnknownMapping(className) && !converterHolder.containsDefaultMapping(className)) {
            return null;
        }

        TypeConverter result = converterHolder.getDefaultMapping(className);

        
        if (result == null && !isPrimitive) {
            Class clazz = null;

            try {
                clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException cnfe) {
                LOG.debug("Cannot load class {}", className, cnfe);
            }

            result = lookupSuper(clazz);

            if (result != null) {
                
                registerConverter(className, result);
            } else {
                
                registerConverterNotFound(className);
            }
        }

        return result;
    }

    
    public TypeConverter lookup(Class clazz) {
        TypeConverter result = lookup(clazz.getName(), clazz.isPrimitive());

        if (result == null && clazz.isPrimitive()) {
            
            return defaultTypeConverter;
        }

        return result;
    }

    protected Object getConverter(Class clazz, String property) {
        LOG.debug("Retrieving convert for class [{}] and property [{}]", clazz, property);

        synchronized (clazz) {
            if ((property != null) && !converterHolder.containsNoMapping(clazz)) {
                try {
                    Map<String, Object> mapping = converterHolder.getMapping(clazz);

                    if (mapping == null) {
                        mapping = buildConverterMapping(clazz);
                    } else {
                        mapping = conditionalReload(clazz, mapping);
                    }

                    Object converter = mapping.get(property);
                    if (converter == null && LOG.isDebugEnabled()) {
                        LOG.debug("Converter is null for property [{}]. Mapping size [{}]:", property, mapping.size());
                        for (String next : mapping.keySet()) {
                            LOG.debug("{}:{}", next, mapping.get(next));
                        }
                    }
                    return converter;
                } catch (Throwable t) {
                    LOG.debug("Got exception trying to resolve convert for class [{}] and property [{}]", clazz, property, t);
                    converterHolder.addNoMapping(clazz);
                }
            }
        }
        return null;
    }

    protected void handleConversionException(Map<String, Object> context, String property, Object value, Object object) {
        if (context != null && (Boolean.TRUE.equals(context.get(REPORT_CONVERSION_ERRORS)))) {
            String realProperty = property;
            String fullName = (String) context.get(CONVERSION_PROPERTY_FULLNAME);

            if (fullName != null) {
                realProperty = fullName;
            }

            Map<String, Object> conversionErrors = (Map<String, Object>) context.get(ActionContext.CONVERSION_ERRORS);

            if (conversionErrors == null) {
                conversionErrors = new HashMap<>();
                context.put(ActionContext.CONVERSION_ERRORS, conversionErrors);
            }

            conversionErrors.put(realProperty, value);
        }
    }

    public synchronized void registerConverter(String className, TypeConverter converter) {
        converterHolder.addDefaultMapping(className, converter);
    }

    public synchronized void registerConverterNotFound(String className) {
        converterHolder.addUnknownMapping(className);
    }

    private Object[] getClassProperty(Map<String, Object> context) {
        Object lastClass = context.get(LAST_BEAN_CLASS_ACCESSED);
        Object lastProperty = context.get(LAST_BEAN_PROPERTY_ACCESSED);
        return (lastClass != null && lastProperty != null) ? new Object[] {lastClass, lastProperty} : null;
    }

    
    protected void addConverterMapping(Map<String, Object> mapping, Class clazz) {
        
        String converterFilename = buildConverterFilename(clazz);
        fileProcessor.process(mapping, clazz, converterFilename);

        
        Annotation[] annotations = clazz.getAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation instanceof Conversion) {
                Conversion conversion = (Conversion) annotation;
                for (TypeConversion tc : conversion.conversions()) {
                    if (mapping.containsKey(tc.key())) {
                        break;
                    }
                    if (LOG.isDebugEnabled()) {
                        if (StringUtils.isEmpty(tc.key())) {
                            LOG.debug("WARNING! key of @TypeConversion [{}] applied to [{}] is empty!", tc.converter(), clazz.getName());
                        } else {
                            LOG.debug("TypeConversion [{}] with key: [{}]", tc.converter(), tc.key());
                        }
                    }
                    annotationProcessor.process(mapping, tc, tc.key());
                }
            }
        }

        
        for (Method method : clazz.getMethods()) {
            annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof TypeConversion) {
                    TypeConversion tc = (TypeConversion) annotation;
                    if (mapping.containsKey(tc.key())) {
                        break;
                    }
                    String key = tc.key();
                    
                    if (StringUtils.isEmpty(key)) {
                        key = AnnotationUtils.resolvePropertyName(method);
                        LOG.debug("Retrieved key [{}] from method name [{}]", key, method.getName());
                    }
                    annotationProcessor.process(mapping, tc, key);
                }
            }
        }
    }


    
    protected Map<String, Object> buildConverterMapping(Class clazz) throws Exception {
        Map<String, Object> mapping = new HashMap<>();

        
        Class curClazz = clazz;

        while (!curClazz.equals(Object.class)) {
            
            addConverterMapping(mapping, curClazz);

            
            Class[] interfaces = curClazz.getInterfaces();

            for (Class anInterface : interfaces) {
                addConverterMapping(mapping, anInterface);
            }

            curClazz = curClazz.getSuperclass();
        }

        if (mapping.size() > 0) {
            converterHolder.addMapping(clazz, mapping);
        } else {
            converterHolder.addNoMapping(clazz);
        }

        return mapping;
    }

    private Map<String, Object> conditionalReload(Class clazz, Map<String, Object> oldValues) throws Exception {
        Map<String, Object> mapping = oldValues;

        if (reloadingConfigs) {
            URL fileUrl = ClassLoaderUtil.getResource(buildConverterFilename(clazz), clazz);
            if (fileManager.fileNeedsReloading(fileUrl)) {
                mapping = buildConverterMapping(clazz);
            }
        }

        return mapping;
    }

    
    TypeConverter lookupSuper(Class clazz) {
        TypeConverter result = null;

        if (clazz != null) {
            result = converterHolder.getDefaultMapping(clazz.getName());

            if (result == null) {
                
                Class[] interfaces = clazz.getInterfaces();

                for (Class anInterface : interfaces) {
                    if (converterHolder.containsDefaultMapping(anInterface.getName())) {
                        result = converterHolder.getDefaultMapping(anInterface.getName());
                        break;
                    }
                }

                if (result == null) {
                    
                    
                    result = lookupSuper(clazz.getSuperclass());
                }
            }
        }

        return result;
    }

}
