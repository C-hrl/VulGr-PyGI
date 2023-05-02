
package com.opensymphony.xwork2;

import java.util.ResourceBundle;


public interface ResourceBundleTextProvider extends TextProvider {

    
    void setBundle(ResourceBundle bundle);

    
    void setClazz(Class clazz);

    
    void setLocaleProvider(LocaleProvider localeProvider);

}
