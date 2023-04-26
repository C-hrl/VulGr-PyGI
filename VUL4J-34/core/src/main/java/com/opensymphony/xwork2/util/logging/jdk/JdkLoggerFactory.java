
package com.opensymphony.xwork2.util.logging.jdk;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


@Deprecated
public class JdkLoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new JdkLogger(java.util.logging.Logger.getLogger(cls.getName()));
    }
    
    @Override
    protected Logger getLoggerImpl(String name) {
        return new JdkLogger(java.util.logging.Logger.getLogger(name));
    }
}
