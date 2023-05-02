package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;



public class CompositeTextProvider implements TextProvider {

    private static final Logger LOG = LogManager.getLogger(CompositeTextProvider.class);

    private List<TextProvider> textProviders = new ArrayList<>();

    
    public CompositeTextProvider(List<TextProvider> textProviders) {
        this.textProviders.addAll(textProviders);
    }

    
    public CompositeTextProvider(TextProvider[] textProviders) {
        this(Arrays.asList(textProviders));
    }

    
    public boolean hasKey(String key) {
        
        for (TextProvider tp : textProviders) {
            if (tp.hasKey(key)) {
                return true;
            }
        }
        return false;
    }

    
    public String getText(String key) {
        return getText(key, key, Collections.emptyList());
    }

    
    public String getText(String key, String defaultValue) {
        return getText(key, defaultValue, Collections.emptyList());
    }

    
    public String getText(String key, String defaultValue, final String obj) {
        return getText(key, defaultValue, new ArrayList<Object>() {
            {
                add(obj);
            }
        });
    }

    
    public String getText(String key, List<?> args) {
        return getText(key, key, args);
    }

    
    public String getText(String key, String[] args) {
        return getText(key, key, args);
    }


    
    public String getText(String key, String defaultValue, List<?> args) {
        
        
        
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    
    public String getText(String key, String defaultValue, String[] args) {
        
        
        
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    
    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        
        
        
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }

    
    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        
        
        
        for (TextProvider textProvider : textProviders) {
            String msg = textProvider.getText(key, defaultValue, args, stack);
            if (msg != null && (!msg.equals(defaultValue))) {
                return msg;
            }
        }
        return defaultValue;
    }


    
    public ResourceBundle getTexts(String bundleName) {
        
        
        for (TextProvider textProvider : textProviders) {
            ResourceBundle bundle = textProvider.getTexts(bundleName);
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }

    
    public ResourceBundle getTexts() {
        
        
        for (TextProvider textProvider : textProviders) {
            ResourceBundle bundle = textProvider.getTexts();
            if (bundle != null) {
                return bundle;
            }
        }
        return null;
    }
}


