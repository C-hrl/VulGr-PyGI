

package org.apache.struts2.components.template;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.StrutsConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TemplateEngineManager {

    
    public static final String DEFAULT_TEMPLATE_TYPE = "ftl";


    Map<String, EngineFactory> templateEngines = new HashMap<>();
    Container container;
    String defaultTemplateType;
    
    @Inject(StrutsConstants.DEFAULT_TEMPLATE_TYPE_CONFIG_KEY)
    public void setDefaultTemplateType(String type) {
        this.defaultTemplateType = type;
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
        Map<String, EngineFactory> map = new HashMap<>();
        Set<String> prefixes = container.getInstanceNames(TemplateEngine.class);
        for (String prefix : prefixes) {
            map.put(prefix, new LazyEngineFactory(prefix));
        }
        this.templateEngines = Collections.unmodifiableMap(map);
    }
    
    
    public void registerTemplateEngine(String templateExtension, final TemplateEngine templateEngine) {
        templateEngines.put(templateExtension, new EngineFactory() {
            public TemplateEngine create() {
                return templateEngine;
            }
        });
    }

    
    public TemplateEngine getTemplateEngine(Template template, String templateTypeOverride) {
        String templateType = DEFAULT_TEMPLATE_TYPE;
        String templateName = template.toString();
        if (StringUtils.contains(templateName, ".")) {
            templateType = StringUtils.substring(templateName, StringUtils.indexOf(templateName, ".") + 1);
        } else if (StringUtils.isNotBlank(templateTypeOverride)) {
            templateType = templateTypeOverride;
        } else {
            String type = defaultTemplateType;
            if (type != null) {
                templateType = type;
            }
        }
        return templateEngines.get(templateType).create();
    }

    
    interface EngineFactory {
        TemplateEngine create();
    }    

    
    class LazyEngineFactory implements EngineFactory {
        private String name;
        public LazyEngineFactory(String name) {
            this.name = name;
        }    
        public TemplateEngine create() {
            TemplateEngine engine = container.getInstance(TemplateEngine.class, name);
            if (engine == null) {
                throw new ConfigurationException("Unable to locate template engine: "+name);
            }
            return engine;
        }    
    }    
}
