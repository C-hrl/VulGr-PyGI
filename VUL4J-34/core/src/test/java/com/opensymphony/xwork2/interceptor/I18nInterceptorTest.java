
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.SimpleFooAction;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import junit.framework.TestCase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class I18nInterceptorTest extends TestCase {

    private I18nInterceptor interceptor;
    private ActionContext ac;
    private Map<String, Serializable> params;
    private Map session;
    private ActionInvocation mai;

    public void testEmptyParamAndSession() throws Exception {
        interceptor.intercept(mai);
    }

    public void testNoSession() throws Exception {
        ac.setSession(null);
        interceptor.intercept(mai);
    }

    public void testDefaultLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "_"); 
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(Locale.getDefault(), session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
    }

    public void testDenmarkLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "da_DK");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        Locale denmark = new Locale("da", "DK");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
    }

    public void testDenmarkLocaleRequestOnly() throws Exception {
        params.put(I18nInterceptor.DEFAULT_REQUESTONLY_PARAMETER, "da_DK");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        Locale denmark = new Locale("da", "DK");
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(denmark, mai.getInvocationContext().getLocale()); 
    }

    public void testCountryOnlyLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "NL");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        Locale denmark = new Locale("NL");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
    }

    public void testLanguageOnlyLocale() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "da_");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        Locale denmark = new Locale("da");
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(denmark, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
    }

    public void testWithVariant() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "ja_JP_JP");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        Locale variant = new Locale("ja", "JP", "JP");
        Locale locale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
        assertNotNull(locale); 
        assertEquals(variant, locale);
        assertEquals("JP", locale.getVariant());
    }

    public void testWithVariantRequestOnly() throws Exception {
        params.put(I18nInterceptor.DEFAULT_REQUESTONLY_PARAMETER, "ja_JP_JP");
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 
        assertNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));

        Locale variant = new Locale("ja", "JP", "JP");
        Locale locale = mai.getInvocationContext().getLocale();
        assertNotNull(locale); 
        assertEquals(variant, locale);
        assertEquals("JP", locale.getVariant());
    }

    public void testRealLocaleObjectInParams() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, Locale.CANADA_FRENCH);
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
    }

    public void testRealLocalesInParams() throws Exception {
        Locale[] locales = new Locale[] { Locale.CANADA_FRENCH };
        assertTrue(locales.getClass().isArray());
        params.put(I18nInterceptor.DEFAULT_PARAMETER, locales);
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(Locale.CANADA_FRENCH, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE));
    }

    public void testSetParameterAndAttributeNames() throws Exception {
        interceptor.setAttributeName("hello");
        interceptor.setParameterName("world");

        params.put("world", Locale.CHINA);
        interceptor.intercept(mai);

        assertNull(params.get("world")); 

        assertNotNull(session.get("hello")); 
        assertEquals(Locale.CHINA, session.get("hello"));
    }

    public void testActionContextLocaleIsPreservedWhenNotOverridden() throws Exception {
        final Locale locale1 = Locale.TRADITIONAL_CHINESE;
        mai.getInvocationContext().setLocale(locale1);
        interceptor.intercept(mai);

        Locale locale = (Locale) session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE);
        assertNull(locale); 
        locale = mai.getInvocationContext().getLocale();
        assertEquals(locale1, locale);
    }

    public void testCVE_2016_2162() throws Exception {
        params.put(I18nInterceptor.DEFAULT_PARAMETER, "DK"); 
        interceptor.intercept(mai);

        assertNull(params.get(I18nInterceptor.DEFAULT_PARAMETER)); 

        Locale defaultLocale = Locale.getDefault();
        assertNotNull(session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
        assertEquals(defaultLocale, session.get(I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE)); 
    }

    @Override
    protected void setUp() throws Exception {
        interceptor = new I18nInterceptor();
        interceptor.init();
        params = new HashMap<>();
        session = new HashMap();

        Map<String, Object> ctx = new HashMap<>();
        ctx.put(ActionContext.PARAMETERS, params);
        ctx.put(ActionContext.SESSION, session);
        ac = new ActionContext(ctx);

        Action action = new SimpleFooAction();
        mai = new MockActionInvocation();
        ((MockActionInvocation) mai).setAction(action);
        ((MockActionInvocation) mai).setInvocationContext(ac);
    }

    @Override
    protected void tearDown() throws Exception {
        interceptor.destroy();
        interceptor = null;
        ac = null;
        params = null;
        session = null;
        mai = null;
    }

}
