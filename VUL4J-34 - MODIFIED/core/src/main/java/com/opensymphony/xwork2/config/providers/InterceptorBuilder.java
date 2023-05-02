
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.InterceptorLocator;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.InterceptorStackConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.util.location.Location;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



public class InterceptorBuilder {

    private static final Logger LOG = LogManager.getLogger(InterceptorBuilder.class);


    
    public static List<InterceptorMapping> constructInterceptorReference(InterceptorLocator interceptorLocator,
                                                                         String refName, Map<String,String> refParams, Location location, ObjectFactory objectFactory) throws ConfigurationException {
        Object referencedConfig = interceptorLocator.getInterceptorConfig(refName);
        List<InterceptorMapping> result = new ArrayList<>();

        if (referencedConfig == null) {
            throw new ConfigurationException("Unable to find interceptor class referenced by ref-name " + refName, location);
        } else {
            if (referencedConfig instanceof InterceptorConfig) {
                InterceptorConfig config = (InterceptorConfig) referencedConfig;
                Interceptor inter;
                try {

                    inter = objectFactory.buildInterceptor(config, refParams);
                    result.add(new InterceptorMapping(refName, inter));
                } catch (ConfigurationException ex) {
              	    LOG.warn("Unable to load config class {} at {} probably due to a missing jar, which might be fine if you never plan to use the {} interceptor",
                            config.getClassName(), ex.getLocation(), config.getName());
                    LOG.error("Unable to load config class {}", config.getClassName(), ex);
                }

            } else if (referencedConfig instanceof InterceptorStackConfig) {
                InterceptorStackConfig stackConfig = (InterceptorStackConfig) referencedConfig;

                if ((refParams != null) && (refParams.size() > 0)) {
                    result = constructParameterizedInterceptorReferences(interceptorLocator, stackConfig, refParams, objectFactory);
                } else {
                    result.addAll(stackConfig.getInterceptors());
                }

            } else {
                LOG.error("Got unexpected type for interceptor {}. Got {}", refName, referencedConfig);
            }
        }

        return result;
    }

    
    private static List<InterceptorMapping> constructParameterizedInterceptorReferences(
            InterceptorLocator interceptorLocator, InterceptorStackConfig stackConfig, Map<String,String> refParams,
            ObjectFactory objectFactory) {
        List<InterceptorMapping> result;
        Map<String, Map<String, String>> params = new LinkedHashMap<>();

        
        for (String key : refParams.keySet()) {
            String value = refParams.get(key);

            try {
                String name = key.substring(0, key.indexOf('.'));
                key = key.substring(key.indexOf('.') + 1);

                Map<String, String> map;
                if (params.containsKey(name)) {
                    map = params.get(name);
                } else {
                    map = new LinkedHashMap<>();
                }

                map.put(key, value);
                params.put(name, map);

            } catch (Exception e) {
                LOG.warn("No interceptor found for name = {}", key);
            }
        }

        result = new ArrayList<>(stackConfig.getInterceptors());

        for (String key : params.keySet()) {

            Map<String, String> map = params.get(key);


            Object interceptorCfgObj = interceptorLocator.getInterceptorConfig(key);

            
            if (interceptorCfgObj instanceof InterceptorConfig) {  
                InterceptorConfig cfg = (InterceptorConfig) interceptorCfgObj;
                Interceptor interceptor = objectFactory.buildInterceptor(cfg, map);

                InterceptorMapping mapping = new InterceptorMapping(key, interceptor);
                if (result != null && result.contains(mapping)) {
                    
                    
                    
                    int index = result.indexOf(mapping);
                    result.set(index, mapping);
                } else {
                    result.add(mapping);
                }
            } else
            if (interceptorCfgObj instanceof InterceptorStackConfig) {  

                
                
                

                InterceptorStackConfig stackCfg = (InterceptorStackConfig) interceptorCfgObj;
                List<InterceptorMapping> tmpResult = constructParameterizedInterceptorReferences(interceptorLocator, stackCfg, map, objectFactory);
                for (InterceptorMapping tmpInterceptorMapping : tmpResult) {
                    if (result.contains(tmpInterceptorMapping)) {
                        int index = result.indexOf(tmpInterceptorMapping);
                        result.set(index, tmpInterceptorMapping);
                    } else {
                        result.add(tmpInterceptorMapping);
                    }
                }
            }
        }

        return result;
    }
}
