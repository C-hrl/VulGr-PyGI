
package it.org.apache.struts2.showcase;

import net.sourceforge.jwebunit.junit.WebTestCase;

public abstract class ITBaseTest extends WebTestCase {

    public void setUp() throws Exception {
        getTestContext().setBaseUrl(ParameterUtils.getBaseUrl());
    }
}
