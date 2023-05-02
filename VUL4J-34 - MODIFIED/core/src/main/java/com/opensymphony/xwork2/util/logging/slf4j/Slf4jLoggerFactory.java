
package com.opensymphony.xwork2.util.logging.slf4j;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;


@Deprecated
public class Slf4jLoggerFactory extends LoggerFactory {

    @Override
    protected Logger getLoggerImpl(Class<?> cls) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(cls));
    }

    @Override
    protected Logger getLoggerImpl(String name) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(name));
    }

}
