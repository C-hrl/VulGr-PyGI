
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;



public class ConversionErrorFieldValidator extends RepopulateConversionErrorFieldValidatorSupport {

    
    @Override
    public void doValidate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
        ActionContext context = ActionContext.getContext();
        Map<String, Object> conversionErrors = context.getConversionErrors();
        
        if (conversionErrors.containsKey(fullFieldName)) {
            if (StringUtils.isBlank(defaultMessage)) {
                defaultMessage = XWorkConverter.getConversionErrorMessage(fullFieldName, context.getValueStack());
            }
            
            addFieldError(fieldName, object);
        }
    }
    
}
