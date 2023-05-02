
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.location.Located;
import com.opensymphony.xwork2.util.location.Location;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class ValidatorConfig extends Located {

    private String type;
    private Map<String, Object> params;
    private String defaultMessage;
    private String messageKey;
    private boolean shortCircuit;
    private String[] messageParams;
    
    
    protected ValidatorConfig(String validatorType) {
        this.type = validatorType;
        params = new LinkedHashMap<>();
    }

    protected ValidatorConfig(ValidatorConfig orig) {
        this.type = orig.type;
        this.params = new LinkedHashMap<>(orig.params);
        this.defaultMessage = orig.defaultMessage;
        this.messageKey = orig.messageKey;
        this.shortCircuit = orig.shortCircuit;
        this.messageParams = orig.messageParams;
    }
    
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    
    public String getMessageKey() {
        return messageKey;
    }
    
    
    public boolean isShortCircuit() {
        return shortCircuit;
    }
    
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    
    public String getType() {
        return type;
    }

    
    public String[] getMessageParams() {
        return messageParams;
    }

    
    public static final class Builder {
        private ValidatorConfig target;

        public Builder(String validatorType) {
            target = new ValidatorConfig(validatorType);
        }

        public Builder(ValidatorConfig config) {
            target = new ValidatorConfig(config);
        }

        public Builder shortCircuit(boolean shortCircuit) {
            target.shortCircuit = shortCircuit;
            return this;
        }

        public Builder defaultMessage(String msg) {
            if ((msg != null) && (msg.trim().length() > 0)) {
                target.defaultMessage = msg;
            }
            return this;
        }

        public Builder messageParams(String[] msgParams) {
            target.messageParams = msgParams;
            return this;
        }

        public Builder messageKey(String key) {
            if ((key != null) && (key.trim().length() > 0)) {
                target.messageKey = key;
            }
            return this;
        }

        public Builder addParam(String name, Object value) {
            if (value != null && name != null) {
                target.params.put(name, value);
            }
            return this;
        }

        public Builder addParams(Map<String,Object> params) {
            target.params.putAll(params);
            return this;
        }

        public Builder location(Location loc) {
            target.location = loc;
            return this;
        }

        public ValidatorConfig build() {
            target.params = Collections.unmodifiableMap(target.params);
            ValidatorConfig result = target;
            target = new ValidatorConfig(target);
            return result;
        }

        public Builder removeParam(String key) {
            target.params.remove(key);
            return this;
        }
    }
}
