
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.ExceptionMappingConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.util.WildcardHelper;

import java.util.HashMap;
import java.util.Map;

public class ActionConfigMatcherTest extends XWorkTestCase {

    
    private Map<String,ActionConfig> configMap;
    private ActionConfigMatcher matcher;
    
    
    @Override public void setUp() throws Exception {
        super.setUp();
        configMap = buildActionConfigMap();
        matcher = new ActionConfigMatcher(new WildcardHelper(), configMap, false);
    }

    @Override public void tearDown() throws Exception {
        super.tearDown();
    }

    
    
    public void testNoMatch() {
        assertNull("ActionConfig shouldn't be matched", matcher.match("test"));
    }

    public void testNoWildcardMatch() {
        assertNull("ActionConfig shouldn't be matched", matcher.match("noWildcard"));
    }

    public void testShouldMatch() {
        ActionConfig matched = matcher.match("foo/class/method");

        assertNotNull("ActionConfig should be matched", matched);
        assertTrue("ActionConfig should have properties, had " +
                matched.getParams().size(), matched.getParams().size() == 2);
        assertTrue("ActionConfig should have interceptors",
                matched.getInterceptors().size() == 1);
        assertTrue("ActionConfig should have ex mappings",
                matched.getExceptionMappings().size() == 1);
        assertTrue("ActionConfig should have external refs",
                matched.getExceptionMappings().size() == 1);
        assertTrue("ActionConfig should have results",
                matched.getResults().size() == 1);
    }

    public void testCheckSubstitutionsMatch() {
        ActionConfig m = matcher.match("foo/class/method");

        assertTrue("Class hasn't been replaced", "foo.bar.classAction".equals(m.getClassName()));
        assertTrue("Method hasn't been replaced", "domethod".equals(m.getMethodName()));
        assertTrue("Package isn't correct", "package-class".equals(m.getPackageName()));

        assertTrue("First param isn't correct", "class".equals(m.getParams().get("first")));
        assertTrue("Second param isn't correct", "method".equals(m.getParams().get("second")));
        
        ExceptionMappingConfig ex = m.getExceptionMappings().get(0);
        assertTrue("Wrong name, was "+ex.getName(), "fooclass".equals(ex.getName()));
        assertTrue("Wrong result", "successclass".equals(ex.getResult()));
        assertTrue("Wrong exception", 
                "java.lang.methodException".equals(ex.getExceptionClassName()));
        assertTrue("First param isn't correct", "class".equals(ex.getParams().get("first")));
        assertTrue("Second param isn't correct", "method".equals(ex.getParams().get("second")));
        
        ResultConfig result = m.getResults().get("successclass");
        assertTrue("Wrong name, was "+result.getName(), "successclass".equals(result.getName()));
        assertTrue("Wrong classname", "foo.method".equals(result.getClassName()));
        assertTrue("First param isn't correct", "class".equals(result.getParams().get("first")));
        assertTrue("Second param isn't correct", "method".equals(result.getParams().get("second")));
        
    }

    public void testCheckMultipleSubstitutions() {
        ActionConfig m = matcher.match("bar/class/method/more");

        assertTrue("Method hasn't been replaced correctly: " + m.getMethodName(),
            "doclass_class".equals(m.getMethodName()));
    }
    
    public void testAllowedMethods() {
        ActionConfig m = matcher.match("addEvent!start");
        assertTrue(m.getAllowedMethods().contains("start"));

        m = matcher.match("addEvent!cancel");
        assertTrue(m.getAllowedMethods().contains("cancel"));
    }

    public void testLooseMatch() {
        configMap.put("*!*", configMap.get("bar*", "foo.bar.{1}Action")
                .methodName("do{2}")
                .addParams(params)
                .addExceptionMapping(new ExceptionMappingConfig.Builder("foo{1}", "java.lang.{2}Exception", "success{1}")
                        .addParams(new HashMap<>(params))
                    .build())
                .addInterceptor(new InterceptorMapping(null, null))
                .addResultConfig(new ResultConfig.Builder("success{1}", "foo.{2}").addParams(params).build())
                .build();
        map.put("foo**", "bar")
                .methodName("do{1}_{1}")
                .addParam("first", "{2}")
                .build();
        
        map.put("bar/*/**", config);

        config = new ActionConfig.Builder("package", "eventAdd!*", "bar")
                .methodName("{1}")
                .build();

        map.put("addEvent!*", config);

        map.put("noWildcard", new ActionConfig.Builder("", "", "").build());

        return map;
    }
}
