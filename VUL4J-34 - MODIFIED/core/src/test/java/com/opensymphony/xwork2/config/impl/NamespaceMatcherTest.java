
package com.opensymphony.xwork2.config.impl;

import com.opensymphony.xwork2.util.WildcardHelper;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

public class NamespaceMatcherTest extends TestCase {

    public void testMatch() {
        Set<String> names = new HashSet<>();
        names.add("/bar");
        names.add("/foojimbar", matcher.match("/foo/23/bar").getPattern());
        assertEquals("/foo/*/jim/*", matcher.match("/foo/23/jim/42").getPattern());
        assertNull(matcher.match("/foo/23/asd"));
    }
}
