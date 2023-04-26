
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.mock.MockActionProxy;

import java.util.HashMap;
import java.util.Map;



public class AliasInterceptorTest extends XWorkTestCase {

    public void testUsingDefaultInterceptorThatAliasPropertiesAreCopied() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("aliasSource", "source here");

        XmlConfigurationProvider provider = new XmlConfigurationProvider("xwork-sample.xml");
        container.inject(provider);
        loadConfigurationProviders(provider);
        ActionProxy proxy = actionProxyFactory.createActionProxy("", "aliasTest", null, params);
        SimpleAction actionOne = (SimpleAction) proxy.getAction();
        actionOne.setAliasSource("name to be copied");
        actionOne.setFoo(17);
        actionOne.setBar(23);
        proxy.execute();
        assertEquals(actionOne.getAliasSource(), actionOne.getAliasDest());
    }

    public void testInvalidAliasExpression() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig.Builder("", "", "")
                .addParam("aliases", "invalid alias expression")
                .build();
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();

        ai.intercept(mai);

        ai.destroy();
    }

    public void testSetAliasKeys() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig.Builder("", "", "")
                .addParam("hello", "invalid alias expression")
                .build();
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();
        ai.setAliasesKey("hello");

        ai.intercept(mai);

        ai.destroy();
    }

    public void testSetInvalidAliasKeys() throws Exception {
        Action action = new SimpleFooAction();
        MockActionInvocation mai = new MockActionInvocation();

        MockActionProxy map = new MockActionProxy();

        ActionConfig cfg = new ActionConfig.Builder("", "", "")
                .addParam("hello", "invalid alias expression")
                .build();
        map.setConfig(cfg);

        mai.setProxy(map);
        mai.setAction(action);
        mai.setInvocationContext(ActionContext.getContext());

        AliasInterceptor ai = new AliasInterceptor();
        ai.init();
        ai.setAliasesKey("iamnotinconfig");

        ai.intercept(mai);

        ai.destroy();
    }

}

