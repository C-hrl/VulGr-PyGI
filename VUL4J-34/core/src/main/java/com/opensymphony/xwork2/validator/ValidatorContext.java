
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.interceptor.ValidationAware;



public interface ValidatorContext extends ValidationAware, TextProvider, LocaleProvider {

    
    String getFullFieldName(String fieldName);
}
