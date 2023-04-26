
package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


public interface UrlProvider {
    
    public static final String NONE = "none";
    public static final String GET = "get";
    public static final String ALL = "all";

    boolean isPutInContext();

    String getVar();

    String getValue();

    String findString(String value);

    void setValue(String string);

    String getUrlIncludeParams();

    String getIncludeParams();

    Map getParameters();

    HttpServletRequest getHttpServletRequest();

    String getAction();

    ExtraParameterProvider getExtraParameterProvider();

    String getScheme();

    String getNamespace();

    String getMethod();

    HttpServletResponse getHttpServletResponse();

    boolean isIncludeContext();

    boolean isEncode();

    boolean isForceAddSchemeHostAndPort();

    boolean isEscapeAmp();
    
    String getPortletMode();
    
    String getWindowState();

    String determineActionURL(String action, String namespace, String method, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Map parameters, String scheme, boolean includeContext, boolean encode, boolean forceAddSchemeHostAndPort, boolean escapeAmp);
    
    String determineNamespace(String namespace, ValueStack stack, HttpServletRequest req);

    String getAnchor();
    
    String getPortletUrlType();
    
    ValueStack getStack();

    void setUrlIncludeParams(String urlIncludeParams);

    void setHttpServletRequest(HttpServletRequest req);

    void setHttpServletResponse(HttpServletResponse res);

    void setUrlRenderer(UrlRenderer urlRenderer);

    void setExtraParameterProvider(ExtraParameterProvider provider);

    void setIncludeParams(String includeParams);

    void setScheme(String scheme);

    void setAction(String action);

    void setPortletMode(String portletMode);

    void setNamespace(String namespace);

    void setMethod(String method);

    void setEncode(boolean encode);

    void setIncludeContext(boolean includeContext);

    void setWindowState(String windowState);

    void setPortletUrlType(String portletUrlType);

    void setAnchor(String anchor);

    void setEscapeAmp(boolean escapeAmp);

    void setForceAddSchemeHostAndPort(boolean forceAddSchemeHostAndPort);

    void putInContext(String result);
}
