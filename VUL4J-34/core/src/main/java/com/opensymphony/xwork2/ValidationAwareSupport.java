
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.interceptor.ValidationAware;

import java.io.Serializable;
import java.util.*;


public class ValidationAwareSupport implements ValidationAware, Serializable {

    private Collection<String> actionErrors;
    private Collection<String> actionMessages;
    private Map<String, List<String>> fieldErrors;


    public synchronized void setActionErrors(Collection<String> errorMessages) {
        this.actionErrors = errorMessages;
    }

    public synchronized Collection<String> getActionErrors() {
        return new LinkedList<>(internalGetActionErrors());
    }

    public synchronized void setActionMessages(Collection<String> messages) {
        this.actionMessages = messages;
    }

    public synchronized Collection<String> getActionMessages() {
        return new LinkedList<>(internalGetActionMessages());
    }

    public synchronized void setFieldErrors(Map<String, List<String>> errorMap) {
        this.fieldErrors = errorMap;
    }

    public synchronized Map<String, List<String>> getFieldErrors() {
        return new LinkedHashMap<>(internalGetFieldErrors());
    }

    public synchronized void addActionError(String anErrorMessage) {
        internalGetActionErrors().add(anErrorMessage);
    }

    public synchronized void addActionMessage(String aMessage) {
        internalGetActionMessages().add(aMessage);
    }

    public synchronized void addFieldError(String fieldName, String errorMessage) {
        final Map<String, List<String>> errors = internalGetFieldErrors();
        List<String> thisFieldErrors = errors.get(fieldName);

        if (thisFieldErrors == null) {
            thisFieldErrors = new ArrayList<>();
            errors.put(fieldName, thisFieldErrors);
        }

        thisFieldErrors.add(errorMessage);
    }

    public synchronized boolean hasActionErrors() {
        return (actionErrors != null) && !actionErrors.isEmpty();
    }

    public synchronized boolean hasActionMessages() {
        return (actionMessages != null) && !actionMessages.isEmpty();
    }

    public synchronized boolean hasErrors() {
        return (hasActionErrors() || hasFieldErrors());
    }

    public synchronized boolean hasFieldErrors() {
        return (fieldErrors != null) && !fieldErrors.isEmpty();
    }

    private Collection<String> internalGetActionErrors() {
        if (actionErrors == null) {
            actionErrors = new ArrayList<>();
        }

        return actionErrors;
    }

    private Collection<String> internalGetActionMessages() {
        if (actionMessages == null) {
            actionMessages = new ArrayList<>();
        }

        return actionMessages;
    }

    private Map<String, List<String>> internalGetFieldErrors() {
        if (fieldErrors == null) {
            fieldErrors = new LinkedHashMap<>();
        }

        return fieldErrors;
    }

    
    public synchronized void clearFieldErrors() {
        internalGetFieldErrors().clear();
    }

    
    public synchronized void clearActionErrors() {
        internalGetActionErrors().clear();
    }

    
    public synchronized void clearMessages() {
        internalGetActionMessages().clear();
    }

    
    public synchronized void clearErrors() {
        internalGetFieldErrors().clear();
        internalGetActionErrors().clear();
    }

    
    public synchronized void clearErrorsAndMessages() {
        internalGetFieldErrors().clear();
        internalGetActionErrors().clear();
        internalGetActionMessages().clear();
    }
}