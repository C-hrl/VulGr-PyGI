
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.XWorkTestCase;
import com.opensymphony.xwork2.util.location.LocationImpl;


public class ActionConfigTest extends XWorkTestCase {

    public void testToString() {
        ActionConfig cfg = new ActionConfig.Builder("", "bob", "foo.Bar")
                .methodName("execute")
                .location(new LocationImpl(null, "foo/xwork.xml", 10, 12))
                .build();

        assertTrue("Wrong toString(): "+cfg.toString(), 
            "{ActionConfig bob (foo.Bar.execute()) - foo/xwork.xml:10:12 - allowedMethods=[LiteralAllowedMethod{allowedMethod='execute'}]}".equals(cfg.toString()));
    }
    
    public void testToStringWithNoMethod() {
        ActionConfig cfg = new ActionConfig.Builder("", "bob", "foo.Bar")
                .location(new LocationImpl(null, "foo/xwork.xml", 10, 12))
                .build();
        
        assertTrue("Wrong toString(): "+cfg.toString(),
            "{ActionConfig bob (foo.Bar) - foo/xwork.xml:10:12 - allowedMethods=[]}".equals(cfg.toString()));
    }
}
