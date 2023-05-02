
package com.opensymphony.xwork2.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NamedVariablePatternMatcher implements PatternMatcher<NamedVariablePatternMatcher.CompiledPattern> {

    public boolean isLiteral(String pattern) {
        return (pattern == null || pattern.indexOf('{') == -1);
    }

    
    public CompiledPattern compilePattern(String data) {
        StringBuilder regex = new StringBuilder();
        if (data != null && data.length() > 0) {
            List<String> varNames = new ArrayList<>();
            StringBuilder varName = null;
            for (int x=0; x<data.length(); x++) {
                char c = data.charAt(x);
                switch (c) {
                    case '{' :  varName = new StringBuilder(); break;
                    case '}' :  if (varName == null) {
                                    throw new IllegalArgumentException("Mismatched braces in pattern");
                                }
                                varNames.add(varName.toString());
                                regex.append("([^/]+)");
                                varName = null;
                                break;
                    default  :  if (varName == null) {
                                    regex.append(c);
                                } else {
                                    varName.append(c);
                                }
                }
            }
            return new CompiledPattern(Pattern.compile(regex.toString()), varNames);
        }
        return null;
    }

    
    public boolean match(Map<String, String> map, String data, CompiledPattern expr) {

        if (data != null && data.length() > 0) {
            Matcher matcher = expr.getPattern().matcher(data);
            if (matcher.matches()) {
                for (int x=0; x<expr.getVariableNames().size(); x++)  {
                    map.put(expr.getVariableNames().get(x), matcher.group(x+1));
                }
                return true;
            }
        }
        return false;
    }

    
    public static class CompiledPattern {
        private Pattern pattern;
        private List<String> variableNames;


        public CompiledPattern(Pattern pattern, List<String> variableNames) {
            this.pattern = pattern;
            this.variableNames = variableNames;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public List<String> getVariableNames() {
            return variableNames;
        }
    }
}
