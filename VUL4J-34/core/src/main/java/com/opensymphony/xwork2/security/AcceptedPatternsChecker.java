package com.opensymphony.xwork2.security;

import java.util.Set;
import java.util.regex.Pattern;


public interface AcceptedPatternsChecker {

    
    public IsAccepted isAccepted(String value);

    
    public void setAcceptedPatterns(String commaDelimitedPatterns);

    
    public void setAcceptedPatterns(String[] patterns);

    
    public void setAcceptedPatterns(Set<String> patterns);

    
    public Set<Pattern> getAcceptedPatterns();

    public final static class IsAccepted {

        private final boolean accepted;
        private final String acceptedPattern;

        public static IsAccepted yes(String acceptedPattern) {
            return new IsAccepted(true, acceptedPattern);
        }

        public static IsAccepted no(String acceptedPatterns) {
            return new IsAccepted(false, acceptedPatterns);
        }

        private IsAccepted(boolean accepted, String acceptedPattern) {
            this.accepted = accepted;
            this.acceptedPattern = acceptedPattern;
        }

        public boolean isAccepted() {
            return accepted;
        }

        public String getAcceptedPattern() {
            return acceptedPattern;
        }

        @Override
        public String toString() {
            return "IsAccepted {" +
                    "accepted=" + accepted +
                    ", acceptedPattern=" + acceptedPattern +
                    " }";
        }
    }

}
