
package com.opensymphony.xwork2.util.logging.commons;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import org.apache.commons.logging.LogFactory;


@Deprecated
public class CommonsLoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new CommonsLogger(LogFactory.getLog(cls));
    }
    
    @Override
    protected Logger getLoggerImpl(String name) {
        return new CommonsLogger(LogFactory.getLog(name));
    }

}
