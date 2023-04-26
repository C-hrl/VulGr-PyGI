
package com.opensymphony.xwork2.util.logging.log4j2;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerUtils;


@Deprecated
public class Log4j2Logger implements Logger {

    private org.apache.logging.log4j.Logger log;

    public Log4j2Logger(org.apache.logging.log4j.Logger log) {
        this.log = log;
    }

    public void error(String msg, String... args) {
        log.error(LoggerUtils.format(msg, args));
    }

    public void error(String msg, Object... args) {
        log.error(LoggerUtils.format(msg, args));
    }

    public void error(String msg, Throwable ex, String... args) {
        log.error(LoggerUtils.format(msg, args), ex);
    }

    public void info(String msg, String... args) {
        log.info(LoggerUtils.format(msg, args));
    }

    public void info(String msg, Throwable ex, String... args) {
        log.info(LoggerUtils.format(msg, args), ex);
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void warn(String msg, String... args) {
        log.warn(LoggerUtils.format(msg, args));
    }

    public void warn(String msg, Object... args) {
        log.warn(LoggerUtils.format(msg, args));
    }

    public void warn(String msg, Throwable ex, String... args) {
        log.warn(LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }
    
    public void debug(String msg, String... args) {
        log.debug(LoggerUtils.format(msg, args));
    }

    public void debug(String msg, Object... args) {
        log.debug(LoggerUtils.format(msg, args));
    }

    public void debug(String msg, Throwable ex, String... args) {
        log.debug(LoggerUtils.format(msg, args), ex);
    }
    
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }
    
    public void trace(String msg, String... args) {
        log.trace(LoggerUtils.format(msg, args));
    }

    public void trace(String msg, Object... args) {
        log.trace(LoggerUtils.format(msg, args));
    }

    public void trace(String msg, Throwable ex, String... args) {
        log.trace(LoggerUtils.format(msg, args), ex);
    }


    public void fatal(String msg, String... args) {
        log.fatal(LoggerUtils.format(msg, args));
    }

    public void fatal(String msg, Throwable ex, String... args) {
        log.fatal(LoggerUtils.format(msg, args), ex);
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return log.isFatalEnabled();
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

}
