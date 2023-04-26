
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;

import java.util.ResourceBundle;


public class TextProviderFactory {

    private TextProvider textProvider;

    @Inject
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    public TextProvider createInstance(Class clazz, LocaleProvider provider) {
        TextProvider instance = getTextProvider(clazz, provider);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setClazz(clazz);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(provider);
        }
        return instance;
    }

    public TextProvider createInstance(ResourceBundle bundle, LocaleProvider provider) {
        TextProvider instance = getTextProvider(bundle, provider);
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setBundle(bundle);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(provider);
        }
        return instance;
    }

    protected TextProvider getTextProvider(Class clazz, LocaleProvider provider) {
        if (this.textProvider == null) {
            return new TextProviderSupport(clazz, provider);
        } else {
            return textProvider;
        }
    }

    private TextProvider getTextProvider(ResourceBundle bundle, LocaleProvider provider) {
        if (this.textProvider == null) {
            return new TextProviderSupport(bundle, provider);
        } else {
            return textProvider;
        }
    }

}
