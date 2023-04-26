
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ExceptionMappingInterceptor extends AbstractInterceptor {
    
    protected static final Logger LOG = LogManager.getLogger(ExceptionMappingInterceptor.class);

    protected Logger categoryLogger;
    protected boolean logEnabled = false;
    protected String logCategory;
    protected String logLevel;
    

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }

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
        String result;

        try {
            result = invocation.invoke();
        } catch (Exception e) {
            if (isLogEnabled()) {
                handleLogging(e);
            }
            List<ExceptionMappingConfig> exceptionMappings = invocation.getProxy().getConfig().getExceptionMappings();
            ExceptionMappingConfig mappingConfig = this.findMappingFromExceptions(exceptionMappings, e);
            if (mappingConfig != null && mappingConfig.getResult()!=null) {
                Map parameterMap = mappingConfig.getParams();
                
                invocation.getInvocationContext().setParameters(new HashMap<String, Object>(parameterMap));
                result = mappingConfig.getResult();
                publishException(invocation, new ExceptionHolder(e));
            } else {
                throw e;
            }
        }

        return result;
    }

    
    protected void handleLogging(Exception e) {
    	if (logCategory != null) {
        	if (categoryLogger == null) {
        		
        		categoryLogger = LogManager.getLogger(logCategory);
        	}
        	doLog(categoryLogger, e);
    	} else {
    		doLog(LOG, e);
    	}
    }
    
    
    protected void doLog(Logger logger, Exception e) {
    	if (logLevel == null) {
    		logger.debug(e.getMessage(), e);
    		return;
    	}
    	
    	if ("trace".equalsIgnoreCase(logLevel)) {
    		logger.trace(e.getMessage(), e);
    	} else if ("debug".equalsIgnoreCase(logLevel)) {
    		logger.debug(e.getMessage(), e);
    	} else if ("info".equalsIgnoreCase(logLevel)) {
    		logger.info(e.getMessage(), e);
    	} else if ("warn".equalsIgnoreCase(logLevel)) {
    		logger.warn(e.getMessage(), e);
    	} else if ("error".equalsIgnoreCase(logLevel)) {
    		logger.error(e.getMessage(), e);
    	} else if ("fatal".equalsIgnoreCase(logLevel)) {
    		logger.fatal(e.getMessage(), e);
    	} else {
    		throw new IllegalArgumentException("LogLevel [" + logLevel + "] is not supported");
    	}
    }

    
    protected ExceptionMappingConfig findMappingFromExceptions(List<ExceptionMappingConfig> exceptionMappings, Throwable t) {
    	ExceptionMappingConfig config = null;
        
        if (exceptionMappings != null) {
            int deepest = Integer.MAX_VALUE;
            for (Object exceptionMapping : exceptionMappings) {
                ExceptionMappingConfig exceptionMappingConfig = (ExceptionMappingConfig) exceptionMapping;
                int depth = getDepth(exceptionMappingConfig.getExceptionClassName(), t);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    config = exceptionMappingConfig;
                }
            }
        }
        return config;
    }

    
    public int getDepth(String exceptionMapping, Throwable t) {
        return getDepth(exceptionMapping, t.getClass(), 0);
    }

    private int getDepth(String exceptionMapping, Class exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionMapping)) {
            
            return depth;
        }
        
        if (exceptionClass.equals(Throwable.class)) {
            return -1;
        }
        return getDepth(exceptionMapping, exceptionClass.getSuperclass(), depth + 1);
    }

    
    protected void publishException(ActionInvocation invocation, ExceptionHolder exceptionHolder) {
        invocation.getStack().push(exceptionHolder);
    }

}
