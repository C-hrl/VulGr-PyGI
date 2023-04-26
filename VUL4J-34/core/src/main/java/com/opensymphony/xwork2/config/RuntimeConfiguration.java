
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.io.Serializable;
import java.util.Map;



public interface RuntimeConfiguration extends Serializable {

    
    ActionConfig getActionConfig(String namespace, String name);

    
    Map<String, Map<String, ActionConfig>> getActionConfigs();
}
