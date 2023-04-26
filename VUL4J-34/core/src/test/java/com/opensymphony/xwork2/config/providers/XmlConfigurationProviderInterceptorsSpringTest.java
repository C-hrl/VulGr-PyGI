
package com.opensymphony.xwork2.config.providers;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.config.entities.InterceptorConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.interceptor.TimerInterceptor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.support.StaticApplicationContext;

import java.util.Map;



public class XmlConfigurationProviderInterceptorsSpringTest extends ConfigurationTestBase {

    InterceptorConfig timerInterceptor = new InterceptorConfig.Builder("timer", TimerInterceptor.class.getName()).build();
    ObjectFactory objectFactory;
    StaticApplicationContext sac;


    public void testInterceptorsLoadedFromSpringApplicationContext() throws ConfigurationException {
        sac.registerSingleton("timer-interceptor", TimerInterceptor.class, new MutablePropertyValues());

        final String filename = "com/opensymphony/xwork2/config/providers/xwork-test-interceptors-spring.xml";

        
        
        ConfigurationProvider provider = buildConfigurationProvider(filename);

        
        provider.init(configuration);
        provider.loadPackages();

        PackageConfig pkg = configuration.getPackageConfig("default");
        Map interceptorConfigs = pkg.getInterceptorConfigs();

        
        assertEquals(1, interceptorConfigs.size());

        
        InterceptorConfig seen = (InterceptorConfig) interceptorConfigs.get("timer");
        assertEquals("timer-interceptor", seen.getClassName());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        sac = new StaticApplicationContext();

        
        
        

        objectFactory = container.getInstance(ObjectFactory.class);
    }
}
