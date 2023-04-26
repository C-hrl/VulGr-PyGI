
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ActionValidatorManager;
import com.opensymphony.xwork2.validator.DelegatingValidatorContext;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidatorContext;

import java.util.Collection;



public class VisitorFieldValidator extends FieldValidatorSupport {

    private String context;
    private boolean appendPrefix = true;
    private ActionValidatorManager actionValidatorManager;


    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }

    
    public void setAppendPrefix(boolean appendPrefix) {
        this.appendPrefix = appendPrefix;
    }

    
    public boolean isAppendPrefix() {
        return appendPrefix;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        if (value == null) {
            log.warn("The visited object is null, VisitorValidator will not be able to handle validation properly. Please make sure the visited object is not null for VisitorValidator to function properly");
            return;
        }
        ValueStack stack = ActionContext.getContext().getValueStack();

        stack.push(object);

        String visitorContext = (context == null) ? ActionContext.getContext().getName() : context;

        if (value instanceof Collection) {
            Collection coll = (Collection) value;
            Object[] array = coll.toArray();

            validateArrayElements(array, fieldName, visitorContext);
        } else if (value instanceof Object[]) {
            Object[] array = (Object[]) value;

            validateArrayElements(array, fieldName, visitorContext);
        } else {
            validateObject(fieldName, value, visitorContext);
        }

        stack.pop();
    }

    private void validateArrayElements(Object[] array, String fieldName, String visitorContext) throws ValidationException {
        if (array == null) {
            return;
        }

        for (int i = 0; i < array.length; i++) {
            Object o = array[i];
            if (o != null) {
                validateObject(fieldName + "[" + i + "]", o, visitorContext);
            }
        }
    }

    private void validateObject(String fieldName, Object o, String visitorContext) throws ValidationException {
        ValueStack stack = ActionContext.getContext().getValueStack();
        stack.push(o);

        ValidatorContext validatorContext;

        if (appendPrefix) {
            validatorContext = new AppendingValidatorContext(getValidatorContext(), o, fieldName, getMessage(o));
        } else {
            ValidatorContext parent = getValidatorContext();
            validatorContext = new DelegatingValidatorContext(parent, DelegatingValidatorContext.makeTextProvider(o, parent), parent);
        }

        actionValidatorManager.validate(o, visitorContext, validatorContext);
        stack.pop();
    }


    public static class AppendingValidatorContext extends DelegatingValidatorContext {
        private String field;
        private String message;
        private ValidatorContext parent;

        public AppendingValidatorContext(ValidatorContext parent, Object object, String field, String message) {
            super(parent, makeTextProvider(object, parent), parent);

            this.field = field;
            this.message = message;
            this.parent = parent;
        }

        
        @Override
        public String getFullFieldName(String fieldName) {
            if (parent instanceof VisitorFieldValidator.AppendingValidatorContext) {
                return parent.getFullFieldName(field + "." + fieldName);
            }
            return field + "." + fieldName;
        }

        public String getFieldNameWithField(String fieldName) {
            return field + "." + fieldName;
        }

        @Override
        public void addActionError(String anErrorMessage) {
            super.addFieldError(getFieldNameWithField(field), message + anErrorMessage);
        }

        @Override
        public void addFieldError(String fieldName, String errorMessage) {
            super.addFieldError(getFieldNameWithField(fieldName), message + errorMessage);
        }
    }
}
