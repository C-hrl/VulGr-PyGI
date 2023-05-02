package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.Map;


public interface InterceptorFactory {

    
    Interceptor buildInterceptor(InterceptorConfig interceptorConfig, Map<String, String> interceptorRefParams) throws ConfigurationException;

}
