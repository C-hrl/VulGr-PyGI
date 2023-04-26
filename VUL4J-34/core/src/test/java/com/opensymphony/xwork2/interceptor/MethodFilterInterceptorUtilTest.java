
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.XWorkTestCase;

import java.util.HashSet;

public class MethodFilterInterceptorUtilTest extends XWorkTestCase {
    
    public void testApplyMethodNoWildcards() {

        HashSet<String> included = new HashSet<>();
        included.add("included");
        included.add("includedAgain");

        HashSet<String> excluded = new HashSet<>();
        excluded.add("excluded");
        excluded.add("excludedAgain");
        
        
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "excluded"));
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "included"));

        
        included.add("excluded");
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "excluded"));

    }

    public void testApplyMethodWithWildcards() {

        HashSet<String> included = new HashSet<>();
        included.add("included*");

        HashSet<String> excluded = new HashSet<>();
        excluded.add("excluded*");
        
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "includedMethod"));
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "excludedMethod"));

        
        included.clear();
        excluded.clear();
        included.add("wildIncluded");
        excluded.add("wild*");
        
        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "wildIncluded"));
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "wildNotIncluded"));

        
        included.clear();
        excluded.clear();
        included.add("*");
        excluded.add("excluded");

        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "anyMethod"));

        
        included.clear();
        excluded.clear();
        included.add("included");
        excluded.add("*");

        assertTrue(MethodFilterInterceptorUtil.applyMethod(excluded, included, "included"));
        assertFalse(MethodFilterInterceptorUtil.applyMethod(excluded, included, "shouldBeExcluded"));

    }

}
