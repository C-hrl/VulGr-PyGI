

package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;

import javax.servlet.http.HttpServletRequest;


public interface ActionMapper {

    
    ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager);

    
    ActionMapping getMappingFromActionName(String actionName);

    
    String getUriFromActionMapping(ActionMapping mapping);
}
