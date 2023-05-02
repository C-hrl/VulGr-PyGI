package com.opensymphony.xwork2.inject;

import junit.framework.TestCase;


public class ContainerImplTest extends TestCase {

    private Container c;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ContainerBuilder cb = new ContainerBuilder();
        cb.constant("methodCheck.name", "Lukasz");
        cb.constant("fieldCheck.name", "Lukasz");
        c = cb.create(false);
    }

    
    public void testFieldInjector() throws Exception {

        FieldCheck fieldCheck = new FieldCheck();

        try {
            c.inject(fieldCheck);
            assertTrue(true);
        } catch (DependencyException expected) {
            fail("No exception expected!");
        }

        assertEquals(fieldCheck.getName(), "Lukasz");
    }

    
    public void testMethodInjector() throws Exception {

        MethodCheck methodCheck = new MethodCheck();

        try {
            c.inject(methodCheck);
            assertTrue(true);
        } catch (DependencyException expected) {
            fail("No exception expected!");
        }
    }

    
    public void testFieldInjectorWithSecurityEnabled() throws Exception {

        System.setSecurityManager(new SecurityManager());

        FieldCheck fieldCheck = new FieldCheck();

        try {
            c.inject(fieldCheck);
            assertEquals(fieldCheck.getName(), "Lukasz");
            fail("Exception should be thrown!");
        } catch (DependencyException expected) {
            
        }
    }

    
    public void testMethodInjectorWithSecurityEnabled() throws Exception {

        
        

        MethodCheck methodCheck = new MethodCheck();

        try {
            c.inject(methodCheck);
            assertEquals(methodCheck.getName(), "Lukasz");
            fail("Exception sould be thrown!");
        } catch (DependencyException expected) {
            
        }
    }

    class FieldCheck {

        @Inject("fieldCheck.name")
        private String name;

        public String getName() {
            return name;
        }
    }

    class MethodCheck {

        private String name;

        @Inject("methodCheck.name")
        private void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

}
