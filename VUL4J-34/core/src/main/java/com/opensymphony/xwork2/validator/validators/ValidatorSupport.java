
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;



public abstract class ValidatorSupport implements Validator, ShortCircuitableValidator {

    protected final Logger log = LogManager.getLogger(this.getClass());

    protected String defaultMessage = "";
    protected String messageKey;
    private ValidatorContext validatorContext;
    private boolean shortCircuit;
    private String type;
    private String[] messageParameters;
    protected ValueStack stack;


    public void setValueStack(ValueStack stack) {
        this.stack = stack;
    }

    public void setDefaultMessage(String message) {
        if (StringUtils.isNotEmpty(message)) {
            this.defaultMessage = message;
        }
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessage(Object object) {
        String message;
        boolean pop = false;

        if (!stack.getRoot().contains(object)) {
            stack.push(object);
            pop = true;
        }

        stack.push(this);

        if (messageKey != null) {
            if ((defaultMessage == null) || ("".equals(defaultMessage.trim()))) {
                defaultMessage = messageKey;
            }
            if (validatorContext == null) {
                validatorContext = new DelegatingValidatorContext(object);
            }
            List<Object> parsedMessageParameters = null;
            if (messageParameters != null) {
                parsedMessageParameters = new ArrayList<>();
                for (String messageParameter : messageParameters) {
                    if (messageParameter != null) {
                        try {
                            Object val = stack.findValue(messageParameter);
                            parsedMessageParameters.add(val);
                        } catch (Exception e) {
                            
                            
                            log.warn("exception while parsing message parameter [{}]", messageParameter, e);
                            parsedMessageParameters.add(messageParameter);
                        }
                    }
                }
            }

            message = validatorContext.getText(messageKey, defaultMessage, parsedMessageParameters);
        } else {
            message = defaultMessage;
        }

        if (StringUtils.isNotBlank(message))
            message = TextParseUtil.translateVariables(message, stack);

        stack.pop();

        if (pop) {
            stack.pop();
        }

        return message;
    }

    public void setMessageKey(String key) {
        messageKey = key;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getMessageParameters() {
        return this.messageParameters;
    }

    public void setMessageParameters(String[] messageParameters) {
        this.messageParameters = messageParameters;
    }

    public void setShortCircuit(boolean shortcircuit) {
        shortCircuit = shortcircuit;
    }

    public boolean isShortCircuit() {
        return shortCircuit;
    }

    public void setValidatorContext(ValidatorContext validatorContext) {
        this.validatorContext = validatorContext;
    }

    public ValidatorContext getValidatorContext() {
        return validatorContext;
    }

    public void setValidatorType(String type) {
        this.type = type;
    }

    public String getValidatorType() {
        return type;
    }

    
    protected Object parse(String expression, Class type) {
        if (expression == null) {
            return null;
        }
        return TextParseUtil.translateVariables('$', expression, stack, type);
    }

    
    protected Object getFieldValue(String name, Object object) throws ValidationException {

        boolean pop = false;

        if (!stack.getRoot().contains(object)) {
            stack.push(object);
            pop = true;
        }

        Object retVal = stack.findValue(name);

        if (pop) {
            stack.pop();
        }

        return retVal;
    }

    protected void addActionError(Object object) {
        validatorContext.addActionError(getMessage(object));
    }

    protected void addFieldError(String propertyName, Object object) {
        validatorContext.addFieldError(propertyName, getMessage(object));
    }

}
