
package com.opensymphony.xwork2.util.logging.log4j2;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


@Deprecated
public class Log4j2LoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(cls));
    }

    @Override
    protected Logger getLoggerImpl(String name) {
        return new Log4j2Logger(org.apache.logging.log4j.LogManager.getLogger(name));
    }

}
