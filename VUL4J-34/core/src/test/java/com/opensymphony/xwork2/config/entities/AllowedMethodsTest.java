package com.opensymphony.xwork2.config.entities;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class AllowedMethodsTest extends TestCase {

    public void testLiteralMethods() throws Exception {
        
        String method = "myMethod";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        
        AllowedMethods allowedMethods = AllowedMethods.build(literals);

        
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed(method));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWidlcardMethods() throws Exception {
        
        String method = "my{1}";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        
        AllowedMethods allowedMethods = AllowedMethods.build(literals);

        
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

    public void testWidlcardWithStarMethods() throws Exception {
        
        String method = "cancel*";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        
        AllowedMethods allowedMethods = AllowedMethods.build(literals);

        
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("cancelAction"));
        assertFalse(allowedMethods.isAllowed("startEvent"));
    }

    public void testRegexMethods() throws Exception {
        
        String method = "regex:my([a-zA-Z].*)";
        Set<String> literals = new HashSet<>();
        literals.add(method);

        
        AllowedMethods allowedMethods = AllowedMethods.build(literals);

        
        assertEquals(1, allowedMethods.list().size());
        assertTrue(allowedMethods.isAllowed("myMethod"));
        assertFalse(allowedMethods.isAllowed("someOtherMethod"));
    }

}
