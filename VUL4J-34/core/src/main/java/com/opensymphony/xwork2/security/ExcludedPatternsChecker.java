package com.opensymphony.xwork2.security;

import java.util.Set;
import java.util.regex.Pattern;


public interface ExcludedPatternsChecker {

    
    public IsExcluded isExcluded(String value);

    
    public void setExcludedPatterns(String commaDelimitedPatterns);

    
    public void setExcludedPatterns(String[] patterns);

    
    public void setExcludedPatterns(Set<String> patterns);

    
    public Set<Pattern> getExcludedPatterns();

    public final static class IsExcluded {

        private final boolean excluded;
        private final String excludedPattern;

        public static IsExcluded yes(Pattern excludedPattern) {
            return new IsExcluded(true, excludedPattern.pattern());
        }

        public static IsExcluded no(Set<Pattern> excludedPatterns) {
            return new IsExcluded(false, excludedPatterns.toString());
        }

        private IsExcluded(boolean excluded, String excludedPattern) {
            this.excluded = excluded;
            this.excludedPattern = excludedPattern;
        }

        public boolean isExcluded() {
            return excluded;
        }

        public String getExcludedPattern() {
            return excludedPattern;
        }

        @Override
        public String toString() {
            return "IsExcluded { " +
                    "excluded=" + excluded +
                    ", excludedPattern=" + excludedPattern +
                    " }";
        }
    }

}
