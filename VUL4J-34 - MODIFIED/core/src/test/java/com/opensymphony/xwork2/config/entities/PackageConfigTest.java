
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.XWorkTestCase;

public class PackageConfigTest extends XWorkTestCase {

    public void testFullDefaultInterceptorRef() {
        PackageConfig cfg1 = new PackageConfig.Builder("pkg1")
                .defaultInterceptorRef("ref1").build();
        PackageConfig cfg2 = new PackageConfig.Builder("pkg2").defaultInterceptorRef("ref2").build();
        PackageConfig cfg = new PackageConfig.Builder("pkg")
                .addParent(cfg1)
                .addParent(cfg2)
                .build();
        
        assertEquals("ref2", cfg.getFullDefaultInterceptorRef());
    }

    public void testStrictDMIInheritance() {
        
        PackageConfig parent = new PackageConfig.Builder("parent").build();

        
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .build();

        
        assertTrue(child.isStrictMethodInvocation());
    }

    public void testStrictDMIInheritanceDisabledInParentPackage() {
        
        PackageConfig parent = new PackageConfig.Builder("parent")
                .strictMethodInvocation(false)
                .build();

        
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .build();

        
        assertTrue(child.isStrictMethodInvocation());
    }

    public void testStrictDMIInheritanceDisabledInBothPackage() {
        
        PackageConfig parent = new PackageConfig.Builder("parent")
                .strictMethodInvocation(false)
                .build();

        
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .strictMethodInvocation(false)
                .build();

        
        assertFalse(child.isStrictMethodInvocation());
    }

    public void testStrictDMIInheritanceDisabledInChildPackage() {
        
        PackageConfig parent = new PackageConfig.Builder("parent").build();

        
        PackageConfig child = new PackageConfig.Builder("child")
                .addParent(parent)
                .strictMethodInvocation(false)
                .build();

        
        assertFalse(child.isStrictMethodInvocation());
    }

}
