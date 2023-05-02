package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.UnknownHandler;

import java.util.Map;


public interface UnknownHandlerFactory {

    
    UnknownHandler buildUnknownHandler(String unknownHandlerName, Map<String, Object> extraContext) throws Exception;

}
