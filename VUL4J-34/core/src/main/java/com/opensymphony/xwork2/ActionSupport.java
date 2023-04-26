
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;



public class ActionSupport implements Action, Validateable, ValidationAware, TextProvider, LocaleProvider, Serializable {

    protected static Logger LOG = LogManager.getLogger(ActionSupport.class);

    private final ValidationAwareSupport validationAware = new ValidationAwareSupport();

    private transient TextProvider textProvider;
    private Container container;

    public void setActionErrors(Collection<String> errorMessages) {
        validationAware.setActionErrors(errorMessages);
    }

    public Collection<String> getActionErrors() {
        return validationAware.getActionErrors();
    }

    public void setActionMessages(Collection<String> messages) {
        validationAware.setActionMessages(messages);
    }

    public Collection<String> getActionMessages() {
        return validationAware.getActionMessages();
    }

    public void setFieldErrors(Map<String, List<String>> errorMap) {
        validationAware.setFieldErrors(errorMap);
    }

    public Map<String, List<String>> getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    public Locale getLocale() {
        ActionContext ctx = ActionContext.getContext();
        if (ctx != null) {
            return ctx.getLocale();
        } else {
        	LOG.debug("Action context not initialized");
            return null;
        }
    }

    public boolean hasKey(String key) {
        return getTextProvider().hasKey(key);
    }

    public String getText(String aTextName) {
        return getTextProvider().getText(aTextName);
    }

    public String getText(String aTextName, String defaultValue) {
        return getTextProvider().getText(aTextName, defaultValue);
    }

    public String getText(String aTextName, String defaultValue, String obj) {
        return getTextProvider().getText(aTextName, defaultValue, obj);
    }

    public String getText(String aTextName, List<?> args) {
        return getTextProvider().getText(aTextName, args);
    }

    public String getText(String key, String[] args) {
        return getTextProvider().getText(key, args);
    }

    public String getText(String aTextName, String defaultValue, List<?> args) {
        return getTextProvider().getText(aTextName, defaultValue, args);
    }

    public String getText(String key, String defaultValue, String[] args) {
        return getTextProvider().getText(key, defaultValue, args);
    }

    public String getText(String key, String defaultValue, List<?> args, ValueStack stack) {
        return getTextProvider().getText(key, defaultValue, args, stack);
    }

    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return getTextProvider().getText(key, defaultValue, args, stack);
    }

    
    public String getFormatted(String key, String expr) {
        Map<String, Object> conversionErrors = ActionContext.getContext().getConversionErrors();
        if (conversionErrors.containsKey(expr)) {
            String[] vals = (String[]) conversionErrors.get(expr);
            return vals[0];
        } else {
            final ValueStack valueStack = ActionContext.getContext().getValueStack();
            final Object val = valueStack.findValue(expr);
            return getText(key, Arrays.asList(val));
        }
    }

    public ResourceBundle getTexts() {
        return getTextProvider().getTexts();
    }

    public ResourceBundle getTexts(String aBundleName) {
        return getTextProvider().getTexts(aBundleName);
    }

    public void addActionError(String anErrorMessage) {
        validationAware.addActionError(anErrorMessage);
    }

    public void addActionMessage(String aMessage) {
        validationAware.addActionMessage(aMessage);
    }

    public void addFieldError(String fieldName, String errorMessage) {
        validationAware.addFieldError(fieldName, errorMessage);
    }

    public String input() throws Exception {
        return INPUT;
    }

    public String doDefault() throws Exception {
        return SUCCESS;
    }

    
    public String execute() throws Exception {
        return SUCCESS;
    }

    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    public boolean hasActionMessages() {
        return validationAware.hasActionMessages();
    }

    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    
    public void clearFieldErrors() {
        validationAware.clearFieldErrors();
    }

    
    public void clearActionErrors() {
        validationAware.clearActionErrors();
    }

    
    public void clearMessages() {
        validationAware.clearMessages();
    }

    
    public void clearErrors() {
        validationAware.clearErrors();
    }

    
    public void clearErrorsAndMessages() {
        validationAware.clearErrorsAndMessages();
    }

    
    public void validate() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    public void pause(String result) {
    }

    
    private TextProvider getTextProvider() {
        if (textProvider == null) {
            TextProviderFactory tpf = new TextProviderFactory();
            if (container != null) {
                container.inject(tpf);
            }
            textProvider = tpf.createInstance(getClass(), this);
        }
        return textProvider;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

}
