
package org.apache.struts2.util;

import com.opensymphony.xwork2.util.PatternMatcher;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexPatternMatcher implements PatternMatcher<RegexPatternMatcherExpression> {
    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)\\}");

    public RegexPatternMatcherExpression compilePattern(String data) {
        Map<Integer, String> params = new HashMap<>();

        Matcher matcher = PATTERN.matcher(data);
        int count = 0;
        while (matcher.find()) {
            String expression = matcher.group(1);
            
            int index = expression.indexOf(':');
            if (index > 0) {
                String paramName = expression.substring(0, index);
                String regex = StringUtils.substring(expression, index + 1);
                if (StringUtils.isBlank(regex)) {
                    throw new IllegalArgumentException("invalid expression [" + expression + "], named parameter regular exression "
                            + "must be in the format {PARAM_NAME:REGEX}");
                }

                params.put(++count, paramName);

            } else {
                params.put(++count, expression);
            }
        }

        
        
        String newPattern = data.replaceAll("(\\{[^\\}]*?:(.*?)\\})", "($2)");

        
        newPattern = newPattern.replaceAll("(\\{.*?\\})", "(.*?)");
        return new RegexPatternMatcherExpression(Pattern.compile(newPattern), params);
    }

    public boolean isLiteral(String pattern) {
        return (pattern == null || pattern.indexOf('{') == -1);
    }

    public boolean match(Map<String, String> map, String data, RegexPatternMatcherExpression expr) {
        Matcher matcher = expr.getPattern().matcher(data);
        Map<Integer, String> params = expr.getParams();

        if (matcher.matches()) {
            map.put("0", data);
            
            
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String paramName = params.get(i);
                String value = matcher.group(i);
                
                
                map.put(paramName, value);
                
                map.put(String.valueOf(i), value);
            }

            return true;
        } else {
            return false;
        }
    }

}
