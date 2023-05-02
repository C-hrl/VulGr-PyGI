
package org.apache.struts2.util;

import java.util.Map;
import java.util.regex.Pattern;


public class RegexPatternMatcherExpression {
    
    private final Pattern pattern;
    
    
    private final Map<Integer, String> params;

    public RegexPatternMatcherExpression(Pattern pattern, Map<Integer, String> params) {
        this.pattern = pattern;
        this.params = params;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Map<Integer, String> getParams() {
        return params;
    }
}
