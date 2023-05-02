
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.*;



public class TextProviderSupport implements ResourceBundleTextProvider {

    private Class clazz;
    private LocaleProvider localeProvider;
    private ResourceBundle bundle;

    
    public TextProviderSupport() {
    }

    
    public TextProviderSupport(Class clazz, LocaleProvider provider) {
        this.clazz = clazz;
        this.localeProvider = provider;
    }

    
    public TextProviderSupport(ResourceBundle bundle, LocaleProvider provider) {
        this.bundle = bundle;
        this.localeProvider = provider;
    }

    
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    
    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }


    
    @Inject
    public void setLocaleProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }


    
    public boolean hasKey(String key) {
    	String message;
    	if (clazz != null) {
            message =  LocalizedTextUtil.findText(clazz, key, getLocale(), null, new Object[0] );
        } else {
            message = LocalizedTextUtil.findText(bundle, key, getLocale(), null, new Object[0]);
        }
    	return message != null;
    }

    
    public String getText(String key) {
        return getText(key, key, Collections.emptyList());
    }

    
    public String getText(String key, String defaultValue) {
        return getText(key, defaultValue, Collections.emptyList());
    }

    
    public String getText(String key, String defaultValue, String arg) {
        List<Object> args = new ArrayList<>();
        args.add(arg);
        return getText(key, defaultValue, args);
    }

    
    public String getText(String key, List<?> args) {
        return getText(key, key, args);
    }

    
    public String getText(String key, String[] args) {
        return getText(key, key, args);
    }

    
    public String getText(String key, String defaultValue, List<?> args) {
        Object[] argsArray = ((args != null && !args.equals(Collections.emptyList())) ? args.toArray() : null);
        if (clazz != null) {
            return LocalizedTextUtil.findText(clazz, key, getLocale(), defaultValue, argsArray);
        } else {
            return LocalizedTextUtil.findText(bundle, key, getLocale(), defaultValue, argsArray);
        }
    }

    
    public String getText(String key, String defaultValue, String[] args) {
        if (clazz != null) {
            return LocalizedTextUtil.findText(clazz, key, getLocale(), defaultValue, args);
        } else {
            return LocalizedTextUtil.findText(bundle, key, getLocale(), defaultValue, args);
        }
    }

    
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        Object[] argsArray = ((args != null) ? args.toArray() : null);
        Locale locale;
        if (stack == null){
        	locale = getLocale();
        }else{
        	locale = (Locale) stack.getContext().get(ActionContext.LOCALE);
        }
        if (locale == null) {
            locale = getLocale();
        }
        if (clazz != null) {
            return LocalizedTextUtil.findText(clazz, key, locale, defaultValue, argsArray, stack);
        } else {
            return LocalizedTextUtil.findText(bundle, key, locale, defaultValue, argsArray, stack);
        }
    }


    
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        Locale locale;
        if (stack == null){
        	locale = getLocale();
        }else{
        	locale = (Locale) stack.getContext().get(ActionContext.LOCALE);
        }
        if (locale == null) {
            locale = getLocale();
        }
        if (clazz != null) {
            return LocalizedTextUtil.findText(clazz, key, locale, defaultValue, args, stack);
        } else {
            return LocalizedTextUtil.findText(bundle, key, locale, defaultValue, args, stack);
        }

    }

    
    public ResourceBundle getTexts(String aBundleName) {
        return LocalizedTextUtil.findResourceBundle(aBundleName, getLocale());
    }

    
    public ResourceBundle getTexts() {
        if (clazz != null) {
            return getTexts(clazz.getName());
        }
        return bundle;
    }

    
    private Locale getLocale() {
        return localeProvider.getLocale();
    }
}
