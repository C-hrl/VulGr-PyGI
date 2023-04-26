
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;


public abstract class RepopulateConversionErrorFieldValidatorSupport extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(RepopulateConversionErrorFieldValidatorSupport.class);

    private boolean repopulateField = false;

    public boolean isRepopulateField() {
        return repopulateField;
    }

    public void setRepopulateField(boolean repopulateField) {
        this.repopulateField = repopulateField;
    }

    public void validate(Object object) throws ValidationException {
        doValidate(object);
        if (repopulateField) {
            repopulateField(object);
        }
    }

    public void repopulateField(Object object) throws ValidationException {

        ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
        Map<String, Object> conversionErrors = ActionContext.getContext().getConversionErrors();

        String fieldName = getFieldName();
        String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
        if (conversionErrors.containsKey(fullFieldName)) {
            Object value = conversionErrors.get(fullFieldName);

            final Map<Object, Object> fakeParams = new LinkedHashMap<Object, Object>();
            boolean doExprOverride = false;

            if (value instanceof String[]) {
                
                String[] tmpValue = (String[]) value;
                if ((tmpValue.length > 0)) {
                    doExprOverride = true;
                    fakeParams.put(fullFieldName, escape(tmpValue[0]));
                } else {
                    LOG.warn("value is an empty array of String or with first element in it as null [{}], will not repopulate conversion error", value);
                }
            } else if (value instanceof String) {
                String tmpValue = (String) value;
                doExprOverride = true;
                fakeParams.put(fullFieldName, escape(tmpValue));
            } else {
                
                LOG.warn("conversion error value is not a String or array of String but instead is [{}], will not repopulate conversion error", value);
            }

            if (doExprOverride) {
                invocation.addPreResultListener(new PreResultListener() {
                    public void beforeResult(ActionInvocation invocation, String resultCode) {
                        ValueStack stack = ActionContext.getContext().getValueStack();
                        stack.setExprOverrides(fakeParams);
                    }
                });
            }
        }
    }

    protected String escape(String value) {
        return "\"" + StringEscapeUtils.escapeJava(value) + "\"";
    }

    protected abstract void doValidate(Object object) throws ValidationException;
}
