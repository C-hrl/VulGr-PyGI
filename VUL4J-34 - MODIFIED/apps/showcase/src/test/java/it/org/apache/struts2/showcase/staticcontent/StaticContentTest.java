package it.org.apache.struts2.showcase.staticcontent;

import it.org.apache.struts2.showcase.ITBaseTest;

import java.io.IOException;

import net.sourceforge.jwebunit.exception.TestingEngineResponseException;

public class StaticContentTest extends ITBaseTest {

    public void testInvalidRersources1() throws IOException {
        try {
            beginAt("/struts..");
            fail("Previous request should have failed");
        } catch (TestingEngineResponseException ex) {
            
        }
    }

    public void testInvalidRersources2() throws IOException {
        try {
            beginAt("/struts/..%252f");
            fail("Previous request should have failed");
        } catch (TestingEngineResponseException ex) {
            
        }
    }

    
}
