package com.opensymphony.xwork2.util;

import java.util.Set;
import java.util.regex.Pattern;


public interface MemberAccessValueStack {

    void setExcludeProperties(Set<Pattern> excludeProperties);

    void setAcceptProperties(Set<Pattern> acceptedProperties);

}
