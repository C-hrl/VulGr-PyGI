
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.PatternMatcher;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ActionConfigMatcher extends AbstractMatcher<ActionConfig> implements Serializable {
   
    
    public ActionConfigMatcher(PatternMatcher<?> patternMatcher,
            Map<String, ActionConfig> configs,
            boolean looseMatch) {
        super(patternMatcher);
        for (String name : configs.keySet()) {
            addPattern(name, configs.get(name), looseMatch);
        }
    }

    
    @Override public ActionConfig convert(String path, ActionConfig orig,
        Map<String, String> vars) {

        String methodName = convertParam(orig.getMethodName(), vars);

        if (StringUtils.isEmpty(methodName)) {
            methodName = ActionConfig.DEFAULT_METHOD;
        }

        if (!orig.isAllowedMethod(methodName)) {
            return null;
        }

        String className = convertParam(orig.getClassName(), vars);
        String pkgName = convertParam(orig.getPackageName(), vars);

        Map<String,String> params = replaceParameters(orig.getParams(), vars);

        Map<String, ResultConfig> results = new LinkedHashMap<>();
        for (String name : orig.getResults().keySet()) {
            ResultConfig result = orig.getResults().get(name);
            name = convertParam(name, vars);
            ResultConfig r = new ResultConfig.Builder(name, convertParam(result.getClassName(), vars))
                    .addParams(replaceParameters(result.getParams(), vars))
                    .build();
            results.put(name, r);
        }

        List<ExceptionMappingConfig> exs = new ArrayList<ExceptionMappingConfig>();
        for (ExceptionMappingConfig ex : orig.getExceptionMappings()) {
            String name = convertParam(ex.getName(), vars);
            String exClassName = convertParam(ex.getExceptionClassName(), vars);
            String exResult = convertParam(ex.getResult(), vars);
            Map<String,String> exParams = replaceParameters(ex.getParams(), vars);
            ExceptionMappingConfig e = new ExceptionMappingConfig.Builder(name, exClassName, exResult).addParams(exParams).build();
            exs.add(e);
        }

        return new ActionConfig.Builder(pkgName, orig.getName(), className)
                .methodName(methodName)
                .addParams(params)
                .addResultConfigs(results)
                .addAllowedMethod(orig.getAllowedMethods())
                .addInterceptors(orig.getInterceptors())
                .addExceptionMappings(exs)
                .location(orig.getLocation())
                .build();
    }
}
