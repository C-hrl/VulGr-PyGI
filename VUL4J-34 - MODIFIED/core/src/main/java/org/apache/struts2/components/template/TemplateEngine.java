

package org.apache.struts2.components.template;

import java.util.Map;


public interface TemplateEngine {

    
    void renderTemplate(TemplateRenderingContext templateContext) throws Exception;

    
    Map getThemeProps(Template template);

}
