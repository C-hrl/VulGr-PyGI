
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.util.PatternMatcher;

import java.util.Map;
import java.util.Set;


public class NamespaceMatcher extends AbstractMatcher<NamespaceMatch> {
     public NamespaceMatcher(PatternMatcher<?> patternMatcher,
            Set<String> namespaces) {
        super(patternMatcher);
        for (String name : namespaces) {
            if (!patternMatcher.isLiteral(name)) {
                addPattern(name, new NamespaceMatch(name, null), false);
            }
        }
    }

    @Override
    protected NamespaceMatch convert(String path, NamespaceMatch orig, Map<String, String> vars) {
        
        return new NamespaceMatch(orig.getPattern(), vars);
    }
}
