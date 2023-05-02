
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class TimerInterceptor extends AbstractInterceptor {
    protected static final Logger LOG = LogManager.getLogger(TimerInterceptor.class);

    protected Logger categoryLogger;
    protected String logCategory;
    protected String logLevel;

    public String getLogCategory() {
        return logCategory;
    }

    public void setLogCategory(String logCatgory) {
        this.logCategory = logCatgory;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (! shouldLog()) {
            return invocation.invoke();
        } else {
            return invokeUnderTiming(invocation);
        }
    }

    
    protected String invokeUnderTiming(ActionInvocation invocation) throws Exception {
        long startTime = System.currentTimeMillis();
        String result = invocation.invoke();
        long executionTime = System.currentTimeMillis() - startTime;

        StringBuilder message = new StringBuilder(100);
        message.append("Executed action [");
        String namespace = invocation.getProxy().getNamespace();
        if (StringUtils.isNotBlank(namespace)) {
            message.append(namespace).append("/");
        }
        message.append(invocation.getProxy().getActionName());
        message.append("!");
        message.append(invocation.getProxy().getMethod());
        message.append("] took ").append(executionTime).append(" ms.");

        doLog(getLoggerToUse(), message.toString());

        return result;
    }

    
    protected boolean shouldLog() {
        
        if (logLevel == null && logCategory == null) {
            return LOG.isInfoEnabled();
        }

        
        return isLoggerEnabled(getLoggerToUse(), logLevel);
    }

    
    protected Logger getLoggerToUse() {
        if (logCategory != null) {
            if (categoryLogger == null) {
                
                categoryLogger = LogManager.getLogger(logCategory);
                if (logLevel == null) {
                    logLevel = "info"; 
                }
            }
            return categoryLogger;
        }

        return LOG;
    }

    
    protected void doLog(Logger logger, String message) {
        if (logLevel == null) {
            logger.info(message);
            return;
        }

        if ("debug".equalsIgnoreCase(logLevel)) {
            logger.debug(message);
        } else if ("info".equalsIgnoreCase(logLevel)) {
            logger.info(message);
        } else if ("warn".equalsIgnoreCase(logLevel)) {
            logger.warn(message);
        } else if ("error".equalsIgnoreCase(logLevel)) {
            logger.error(message);
        } else if ("fatal".equalsIgnoreCase(logLevel)) {
            logger.fatal(message);
        } else if ("trace".equalsIgnoreCase(logLevel)) {
            logger.trace(message);
        } else {
            throw new IllegalArgumentException("LogLevel [" + logLevel + "] is not supported");
        }
    }

    
    private static boolean isLoggerEnabled(Logger logger, String level) {
        if ("debug".equalsIgnoreCase(level)) {
            return logger.isDebugEnabled();
        } else if ("info".equalsIgnoreCase(level)) {
            return logger.isInfoEnabled();
        } else if ("warn".equalsIgnoreCase(level)) {
            return logger.isWarnEnabled();
        } else if ("error".equalsIgnoreCase(level)) {
            return logger.isErrorEnabled();
        } else if ("fatal".equalsIgnoreCase(level)) {
            return logger.isFatalEnabled();
        } else if ("trace".equalsIgnoreCase(level)) {
            return logger.isTraceEnabled();
        } else {
            throw new IllegalArgumentException("LogLevel [" + level + "] is not supported");
        }
    }

}
