package com.opensymphony.xwork2.util.reflection;

import java.util.Map;

public interface ReflectionContextFactory {
    
    Map createDefaultContext( Object root );
}
