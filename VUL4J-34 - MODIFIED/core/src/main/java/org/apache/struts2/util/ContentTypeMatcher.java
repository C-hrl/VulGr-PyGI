package org.apache.struts2.util;

import java.util.Map;


public interface ContentTypeMatcher<E extends Object> {

    E compilePattern(String data);

    boolean match(Map<String,String> map, String data, E expr);

}
