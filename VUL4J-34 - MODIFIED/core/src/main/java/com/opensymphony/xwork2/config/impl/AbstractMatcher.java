
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.util.PatternMatcher;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;


public abstract class AbstractMatcher<E> implements Serializable {
    
    private static final Logger log = LogManager.getLogger(AbstractMatcher.class);

    
    PatternMatcher<Object> wildcard;

    
    List<Mapping<E>> compiledPatterns = new ArrayList<>();
    ;
    
    public AbstractMatcher(PatternMatcher<?> helper) {
        this.wildcard = (PatternMatcher<Object>) helper;
    }

    
    public void addPattern(String name, E target, boolean looseMatch) {

        Object pattern;

        if (!wildcard.isLiteral(name)) {
            if (looseMatch && (name.length() > 0) && (name.charAt(0) == '/')) {
                name = name.substring(1);
            }

            log.debug("Compiling pattern '{}'", name);

            pattern = wildcard.compilePattern(name);
            compiledPatterns.add(new Mapping<E>(name, pattern, target));

            if (looseMatch) {
                int lastStar = name.lastIndexOf('*');
                if (lastStar > 1 && lastStar == name.length() - 1) {
                    if (name.charAt(lastStar - 1) != '*') {
                        pattern = wildcard.compilePattern(name.substring(0, lastStar - 1));
                        compiledPatterns.add(new Mapping<E>(name, pattern, target));
                    }
                }
            }
        }
    }
    
    public void freeze() {
        compiledPatterns = Collections.unmodifiableList(new ArrayList<Mapping<E>>());
    }

    
    public E match(String potentialMatch) {
        E config = null;

        if (compiledPatterns.size() > 0) {
            log.debug("Attempting to match '{}' to a wildcard pattern, {} available", potentialMatch, compiledPatterns.size());

            Map<String,String> vars = new LinkedHashMap<String,String>();
            for (Mapping<E> m : compiledPatterns) {
                if (wildcard.match(vars, potentialMatch, m.getPattern())) {
                    log.debug("Value matches pattern '{}'", m.getOriginalPattern());
                    config = convert(potentialMatch, m.getTarget(), vars);
                    break;
                }
            }
        }

        return config;
    }

    
    protected abstract E convert(String path, E orig, Map<String, String> vars);

    
    protected Map<String,String> replaceParameters(Map<String, String> orig, Map<String,String> vars) {
        Map<String, String> map = new LinkedHashMap<>();
        
        
        for (String key : orig.keySet()) {
            map.put(key, convertParam(orig.get(key), vars));
        }
        
        
        
        for (String key: vars.keySet()) {
            if (!NumberUtils.isNumber(key)) {
                map.put(key, vars.get(key));
            }
        }
        
        return map;
    }

    
    protected String convertParam(String val, Map<String, String> vars) {
        if (val == null) {
            return null;
        } 
        
        int len = val.length();
        StringBuilder ret = new StringBuilder();
        char c;
        String varVal;
        for (int x=0; x<len; x++) {
            c = val.charAt(x);
            if (x < len - 2 && 
                    c == '{' && '}' == val.charAt(x+2)) {
                varVal = (String)vars.get(String.valueOf(val.charAt(x + 1)));
                if (varVal != null) {
                    ret.append(varVal);
                } 
                x += 2;
            } else {
                ret.append(c);
            }
        }
        
        return ret.toString();
    }

    
    private static class Mapping<E> implements Serializable {
        
        private String original;

        
        
        private Object pattern;

        
        private E config;

        
        public Mapping(String original, Object pattern, E config) {
            this.original = original;
            this.pattern = pattern;
            this.config = config;
        }

        
        public Object getPattern() {
            return this.pattern;
        }

        
        public E getTarget() {
            return this.config;
        }
        
        
        public String getOriginalPattern() {
            return this.original;
        }
    }
}
