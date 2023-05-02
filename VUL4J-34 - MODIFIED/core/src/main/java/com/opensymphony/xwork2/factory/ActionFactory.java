package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.Map;


public interface ActionFactory {

    
    Object buildAction(String actionName, String namespace, ActionConfig config, Map<String, Object> extraContext) throws Exception;

}

