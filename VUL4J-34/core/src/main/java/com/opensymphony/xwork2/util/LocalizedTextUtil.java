

package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.reflection.ReflectionProviderFactory;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;



public class LocalizedTextUtil {

    private static final Logger LOG = LogManager.getLogger(LocalizedTextUtil.class);

    private static final String TOMCAT_RESOURCE_ENTRIES_FIELD = "resourceEntries";

    private static final ConcurrentMap<Integer, List<String>> classLoaderMap = new ConcurrentHashMap<>();

    private static boolean reloadBundles = false;
    private static boolean devMode;

    private static final ConcurrentMap<String, ResourceBundle> bundlesMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<MessageFormatKey, MessageFormat> messageFormats = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, ClassLoader> delegatedClassLoaderMap = new ConcurrentHashMap<>();
    private static final Set<String> missingBundles = Collections.synchronizedSet(new HashSet<String>());

    private static final String RELOADED = "com.opensymphony.xwork2.util.LocalizedTextUtil.reloaded";
    private static final String XWORK_MESSAGES_BUNDLE = "com/opensymphony/xwork2/xwork-messages";

    static {
        clearDefaultResourceBundles();
    }


    
    public static void clearDefaultResourceBundles() {
        ClassLoader ccl = getCurrentThreadContextClassLoader();
        List<String> bundles = new ArrayList<>();
        classLoaderMap.put(ccl.hashCode(), bundles);
        bundles.add(0, XWORK_MESSAGES_BUNDLE);
    }

    
    public static void setReloadBundles(boolean reloadBundles) {
        LocalizedTextUtil.reloadBundles = reloadBundles;
    }

    public static void setDevMode(boolean devMode) {
        LocalizedTextUtil.devMode = devMode;
    }

    
    public static void addDefaultResourceBundle(String resourceBundleName) {
        
        ClassLoader ccl;
        synchronized (XWORK_MESSAGES_BUNDLE) {
            ccl = getCurrentThreadContextClassLoader();
            List<String> bundles = classLoaderMap.get(ccl.hashCode());
            if (bundles == null) {
                bundles = new ArrayList<String>();
                classLoaderMap.put(ccl.hashCode(), bundles);
                bundles.add(XWORK_MESSAGES_BUNDLE);
            }
            bundles.remove(resourceBundleName);
            bundles.add(0, resourceBundleName);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Added default resource bundle '{}' to default resource bundles for the following classloader '{}'", resourceBundleName, ccl.toString());
        }
    }

    
    public static Locale localeFromString(String localeStr, Locale defaultLocale) {
        if ((localeStr == null) || (localeStr.trim().length() == 0) || ("_".equals(localeStr))) {
            if (defaultLocale != null) {
                return defaultLocale;
            }
            return Locale.getDefault();
        }

        int index = localeStr.indexOf('_');
        if (index < 0) {
            return new Locale(localeStr);
        }

        String language = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language);
        }

        localeStr = localeStr.substring(index + 1);
        index = localeStr.indexOf('_');
        if (index < 0) {
            return new Locale(language, localeStr);
        }

        String country = localeStr.substring(0, index);
        if (index == localeStr.length()) {
            return new Locale(language, country);
        }

        localeStr = localeStr.substring(index + 1);
        return new Locale(language, country, localeStr);
    }

    
    public static String findDefaultText(String aTextName, Locale locale) {
        List<String> localList = classLoaderMap.get(Thread.currentThread().getContextClassLoader().hashCode());

        for (String bundleName : localList) {
            ResourceBundle bundle = findResourceBundle(bundleName, locale);
            if (bundle != null) {
                reloadBundles();
                try {
                    return bundle.getString(aTextName);
                } catch (MissingResourceException e) {
                	
                }
            }
        }

        if (devMode) {
            LOG.warn("Missing key [{}] in bundles [{}]!", aTextName, localList);
        } else {
            LOG.debug("Missing key [{}] in bundles [{}]!", aTextName, localList);
        }

        return null;
    }

    
    public static String findDefaultText(String aTextName, Locale locale, Object[] params) {
        String defaultText = findDefaultText(aTextName, locale);
        if (defaultText != null) {
            MessageFormat mf = buildMessageFormat(defaultText, locale);
            return formatWithNullDetection(mf, params);
        }
        return null;
    }

    
    public static ResourceBundle findResourceBundle(String aBundleName, Locale locale) {
        ClassLoader classLoader = getCurrentThreadContextClassLoader();
        String key = createMissesKey(String.valueOf(classLoader.hashCode()), aBundleName, locale);

        if (missingBundles.contains(key)) {
            return null;
        }

        ResourceBundle bundle = null;
        try {
            if (bundlesMap.containsKey(key)) {
                bundle = bundlesMap.get(key);
            } else {
                bundle = ResourceBundle.getBundle(aBundleName, locale, classLoader);
                bundlesMap.putIfAbsent(key, bundle);
            }
        } catch (MissingResourceException ex) {
            if (delegatedClassLoaderMap.containsKey(classLoader.hashCode())) {
                try {
                    if (bundlesMap.containsKey(key)) {
                        bundle = bundlesMap.get(key);
                    } else {
                        bundle = ResourceBundle.getBundle(aBundleName, locale, delegatedClassLoaderMap.get(classLoader.hashCode()));
                        bundlesMap.putIfAbsent(key, bundle);
                    }
                } catch (MissingResourceException e) {
                    LOG.debug("Missing resource bundle [{}]!", aBundleName, e);
                    missingBundles.add(key);
                }
            } else {
                LOG.debug("Missing resource bundle [{}]!", aBundleName);
                missingBundles.add(key);
            }
        }
        return bundle;
    }

    
    public static void setDelegatedClassLoader(final ClassLoader classLoader) {
        synchronized (bundlesMap) {
            delegatedClassLoaderMap.put(getCurrentThreadContextClassLoader().hashCode(), classLoader);
        }
    }

    
    public static void clearBundle(final String bundleName) {
        bundlesMap.remove(getCurrentThreadContextClassLoader().hashCode() + bundleName);
    }


    
    private static String createMissesKey(String prefix, String aBundleName, Locale locale) {
        return prefix + aBundleName + "_" + locale.toString();
    }

    
    public static String findText(Class aClass, String aTextName, Locale locale) {
        return findText(aClass, aTextName, locale, aTextName, new Object[0]);
    }

    
    public static String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return findText(aClass, aTextName, locale, defaultMessage, args, valueStack);

    }

    
    public static String findText(Class aClass, String aTextName, Locale locale, String defaultMessage, Object[] args,
                                  ValueStack valueStack) {
        String indexedTextName = null;
        if (aTextName == null) {
        	LOG.warn("Trying to find text with null key!");
            aTextName = "";
        }
        
        if (aTextName.contains("[")) {
            int i = -1;

            indexedTextName = aTextName;

            while ((i = indexedTextName.indexOf("[", i + 1)) != -1) {
                int j = indexedTextName.indexOf("]", i);
                String a = indexedTextName.substring(0, i);
                String b = indexedTextName.substring(j);
                indexedTextName = a + "[*" + b;
            }
        }

        
        String msg = findMessage(aClass, aTextName, indexedTextName, locale, args, null, valueStack);

        if (msg != null) {
            return msg;
        }

        if (ModelDriven.class.isAssignableFrom(aClass)) {
            ActionContext context = ActionContext.getContext();
            
            ActionInvocation actionInvocation = context.getActionInvocation();

            
            if (actionInvocation != null) {
                Object action = actionInvocation.getAction();
                if (action instanceof ModelDriven) {
                    Object model = ((ModelDriven) action).getModel();
                    if (model != null) {
                        msg = findMessage(model.getClass(), aTextName, indexedTextName, locale, args, null, valueStack);
                        if (msg != null) {
                            return msg;
                        }
                    }
                }
            }
        }

        
        for (Class clazz = aClass;
             (clazz != null) && !clazz.equals(Object.class);
             clazz = clazz.getSuperclass()) {

            String basePackageName = clazz.getName();
            while (basePackageName.lastIndexOf('.') != -1) {
                basePackageName = basePackageName.substring(0, basePackageName.lastIndexOf('.'));
                String packageName = basePackageName + ".package";
                msg = getMessage(packageName, locale, aTextName, valueStack, args);

                if (msg != null) {
                    return msg;
                }

                if (indexedTextName != null) {
                    msg = getMessage(packageName, locale, indexedTextName, valueStack, args);

                    if (msg != null) {
                        return msg;
                    }
                }
            }
        }

        
        int idx = aTextName.indexOf(".");

        if (idx != -1) {
            String newKey = null;
            String prop = null;

            if (aTextName.startsWith(XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX)) {
                idx = aTextName.indexOf(".", XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX.length());

                if (idx != -1) {
                    prop = aTextName.substring(XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX.length(), idx);
                    newKey = XWorkConverter.CONVERSION_ERROR_PROPERTY_PREFIX + aTextName.substring(idx + 1);
                }
            } else {
                prop = aTextName.substring(0, idx);
                newKey = aTextName.substring(idx + 1);
            }

            if (prop != null) {
                Object obj = valueStack.findValue(prop);
                try {
                    Object actionObj = ReflectionProviderFactory.getInstance().getRealTarget(prop, valueStack.getContext(), valueStack.getRoot());
                    if (actionObj != null) {
                        PropertyDescriptor propertyDescriptor = ReflectionProviderFactory.getInstance().getPropertyDescriptor(actionObj.getClass(), prop);

                        if (propertyDescriptor != null) {
                            Class clazz = propertyDescriptor.getPropertyType();

                            if (clazz != null) {
                                if (obj != null) {
                                    valueStack.push(obj);
                                }
                                msg = findText(clazz, newKey, locale, null, args);
                                if (obj != null) {
                                    valueStack.pop();
                                }
                                if (msg != null) {
                                    return msg;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOG.debug("unable to find property {}", prop, e);
                }
            }
        }

        
        GetDefaultMessageReturnArg result;
        if (indexedTextName == null) {
            result = getDefaultMessage(aTextName, locale, valueStack, args, defaultMessage);
        } else {
            result = getDefaultMessage(aTextName, locale, valueStack, args, null);
            if (result != null && result.message != null) {
                return result.message;
            }
            result = getDefaultMessage(indexedTextName, locale, valueStack, args, defaultMessage);
        }

        
        if (unableToFindTextForKey(result) && LOG.isDebugEnabled()) {
            String warn = "Unable to find text for key '" + aTextName + "' ";
            if (indexedTextName != null) {
                warn += " or indexed key '" + indexedTextName + "' ";
            }
            warn += "in class '" + aClass.getName() + "' and locale '" + locale + "'";
            LOG.debug(warn);
        }

        return result != null ? result.message : null;
    }

    
    private static boolean unableToFindTextForKey(GetDefaultMessageReturnArg result) {
        if (result == null || result.message == null) {
            return true;
        }

        
        if (result.foundInBundle) {
            return false;
        }

        
        return true;
    }

    
    public static String findText(ResourceBundle bundle, String aTextName, Locale locale) {
        return findText(bundle, aTextName, locale, aTextName, new Object[0]);
    }

    
    public static String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args) {
        ValueStack valueStack = ActionContext.getContext().getValueStack();
        return findText(bundle, aTextName, locale, defaultMessage, args, valueStack);
    }

    
    public static String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args,
                                  ValueStack valueStack) {
        try {
            reloadBundles(valueStack.getContext());

            String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);
            MessageFormat mf = buildMessageFormat(message, locale);

            return formatWithNullDetection(mf, args);
        } catch (MissingResourceException ex) {
            if (devMode) {
                LOG.warn("Missing key [{}] in bundle [{}]!", aTextName, bundle);
            } else {
                LOG.debug("Missing key [{}] in bundle [{}]!", aTextName, bundle);
            }
        }

        GetDefaultMessageReturnArg result = getDefaultMessage(aTextName, locale, valueStack, args, defaultMessage);
        if (unableToFindTextForKey(result)) {
            LOG.warn("Unable to find text for key '{}' in ResourceBundles for locale '{}'", aTextName, locale);
        }
        return result != null ? result.message : null;
    }

    
    private static GetDefaultMessageReturnArg getDefaultMessage(String key, Locale locale, ValueStack valueStack, Object[] args,
                                                                String defaultMessage) {
        GetDefaultMessageReturnArg result = null;
        boolean found = true;

        if (key != null) {
            String message = findDefaultText(key, locale);

            if (message == null) {
                message = defaultMessage;
                found = false; 
            }

            
            if (message != null) {
                MessageFormat mf = buildMessageFormat(TextParseUtil.translateVariables(message, valueStack), locale);

                String msg = formatWithNullDetection(mf, args);
                result = new GetDefaultMessageReturnArg(msg, found);
            }
        }

        return result;
    }

    
    private static String getMessage(String bundleName, Locale locale, String key, ValueStack valueStack, Object[] args) {
        ResourceBundle bundle = findResourceBundle(bundleName, locale);
        if (bundle == null) {
            return null;
        }
        if (valueStack != null) 
            reloadBundles(valueStack.getContext());
        try {
        	String message = bundle.getString(key);
        	if (valueStack != null) 
        		message = TextParseUtil.translateVariables(bundle.getString(key), valueStack);
            MessageFormat mf = buildMessageFormat(message, locale);
            return formatWithNullDetection(mf, args);
        } catch (MissingResourceException e) {
            if (devMode) {
                LOG.warn("Missing key [{}] in bundle [{}]!", key, bundleName);
            } else {
                LOG.debug("Missing key [{}] in bundle [{}]!", key, bundleName);
            }
            return null;
        }
    }

    private static String formatWithNullDetection(MessageFormat mf, Object[] args) {
        String message = mf.format(args);
        if ("null".equals(message)) {
            return null;
        } else {
            return message;
        }
    }

    private static MessageFormat buildMessageFormat(String pattern, Locale locale) {
        MessageFormatKey key = new MessageFormatKey(pattern, locale);
        MessageFormat format = messageFormats.get(key);
        if (format == null) {
            format = new MessageFormat(pattern);
            format.setLocale(locale);
            format.applyPattern(pattern);
            messageFormats.put(key, format);
        }

        return format;
    }

    
    private static String findMessage(Class clazz, String key, String indexedKey, Locale locale, Object[] args, Set<String> checked,
                                      ValueStack valueStack) {
        if (checked == null) {
            checked = new TreeSet<String>();
        } else if (checked.contains(clazz.getName())) {
            return null;
        }

        
        String msg = getMessage(clazz.getName(), locale, key, valueStack, args);

        if (msg != null) {
            return msg;
        }

        if (indexedKey != null) {
            msg = getMessage(clazz.getName(), locale, indexedKey, valueStack, args);

            if (msg != null) {
                return msg;
            }
        }

        
        Class[] interfaces = clazz.getInterfaces();

        for (Class anInterface : interfaces) {
            msg = getMessage(anInterface.getName(), locale, key, valueStack, args);

            if (msg != null) {
                return msg;
            }

            if (indexedKey != null) {
                msg = getMessage(anInterface.getName(), locale, indexedKey, valueStack, args);

                if (msg != null) {
                    return msg;
                }
            }
        }

        
        if (clazz.isInterface()) {
            interfaces = clazz.getInterfaces();

            for (Class anInterface : interfaces) {
                msg = findMessage(anInterface, key, indexedKey, locale, args, checked, valueStack);

                if (msg != null) {
                    return msg;
                }
            }
        } else {
            if (!clazz.equals(Object.class) && !clazz.isPrimitive()) {
                return findMessage(clazz.getSuperclass(), key, indexedKey, locale, args, checked, valueStack);
            }
        }

        return null;
    }

    private static void reloadBundles() {
        reloadBundles(ActionContext.getContext() != null ? ActionContext.getContext().getContextMap() : null);
    }

    private static void reloadBundles(Map<String, Object> context) {
        if (reloadBundles) {
            try {
                Boolean reloaded;
                if (context != null) {
                    reloaded = (Boolean) ObjectUtils.defaultIfNull(context.get(RELOADED), Boolean.FALSE);
                }else {
                    reloaded = Boolean.FALSE;
                }
                if (!reloaded) {
                    bundlesMap.clear();
                    try {
                        clearMap(ResourceBundle.class, null, "cacheList");
                    } catch (NoSuchFieldException e) {
                        
                        
                        clearMap(ResourceBundle.class, null, "cache");
                    }

                    
                    
                    clearTomcatCache();
                    if(context!=null) {
                        context.put(RELOADED, true);
                    }
                    LOG.debug("Resource bundles reloaded");
                }
            } catch (Exception e) {
                LOG.error("Could not reload resource bundles", e);
            }
        }
    }


    private static void clearTomcatCache() {
        ClassLoader loader = getCurrentThreadContextClassLoader();
        
        Class cl = loader.getClass();

        try {
            if ("org.apache.catalina.loader.WebappClassLoader".equals(cl.getName())) {
                clearMap(cl, loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
            } else {
                LOG.debug("Class loader {} is not tomcat loader.", cl.getName());
            }
        } catch (NoSuchFieldException nsfe) {
            if ("org.apache.catalina.loader.WebappClassLoaderBase".equals(cl.getSuperclass().getName())) {
                LOG.debug("Base class {} doesn't contain '{}' field, trying with parent!", cl.getName(), TOMCAT_RESOURCE_ENTRIES_FIELD, nsfe);
                try {
                    clearMap(cl.getSuperclass(), loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
                } catch (Exception e) {
                    LOG.warn("Couldn't clear tomcat cache using {}", cl.getSuperclass().getName(), e);
                }
            }
        } catch (Exception e) {
      	    LOG.warn("Couldn't clear tomcat cache", cl.getName(), e);
        }
    }


    private static void clearMap(Class cl, Object obj, String name)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        Field field = cl.getDeclaredField(name);
        field.setAccessible(true);

        Object cache = field.get(obj);

        synchronized (cache) {
            Class ccl = cache.getClass();
            Method clearMethod = ccl.getMethod("clear");
            clearMethod.invoke(cache);
        }
    }

    
    public static void reset() {
        clearDefaultResourceBundles();
        bundlesMap.clear();
        messageFormats.clear();
    }

    static class MessageFormatKey {
        String pattern;
        Locale locale;

        MessageFormatKey(String pattern, Locale locale) {
            this.pattern = pattern;
            this.locale = locale;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MessageFormatKey)) return false;

            final MessageFormatKey messageFormatKey = (MessageFormatKey) o;

            if (locale != null ? !locale.equals(messageFormatKey.locale) : messageFormatKey.locale != null)
                return false;
            if (pattern != null ? !pattern.equals(messageFormatKey.pattern) : messageFormatKey.pattern != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            result = (pattern != null ? pattern.hashCode() : 0);
            result = 29 * result + (locale != null ? locale.hashCode() : 0);
            return result;
        }
    }

    private static ClassLoader getCurrentThreadContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    static class GetDefaultMessageReturnArg {
        String message;
        boolean foundInBundle;

        public GetDefaultMessageReturnArg(String message, boolean foundInBundle) {
            this.message = message;
            this.foundInBundle = foundInBundle;
        }
    }

    private static class EmptyResourceBundle extends ResourceBundle {
        @Override
        public Enumeration<String> getKeys() {
            return null; 
        }

        @Override
        protected Object handleGetObject(String key) {
            return null; 
        }
    }

}
