

package org.apache.struts2.views.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.URL;

import com.opensymphony.xwork2.util.ValueStack;



public class URLTag extends ContextBeanTag {

    private static final long serialVersionUID = 1722460444125206226L;

    protected String includeParams;
    protected String scheme;
    protected String value;
    protected String action;
    protected String namespace;
    protected String method;
    protected String encode;
    protected String includeContext;
    protected String escapeAmp;
    protected String portletMode;
    protected String windowState;
    protected String portletUrlType;
    protected String anchor;
    protected String forceAddSchemeHostAndPort;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new URL(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        URL url = (URL) component;
        url.setIncludeParams(includeParams);
        url.setScheme(scheme);
        url.setValue(value);
        url.setMethod(method);
        url.setNamespace(namespace);
        url.setAction(action);
        url.setPortletMode(portletMode);
        url.setPortletUrlType(portletUrlType);
        url.setWindowState(windowState);
        url.setAnchor(anchor);

        if (encode != null) {
            url.setEncode(Boolean.valueOf(encode).booleanValue());
        }
        if (includeContext != null) {
            url.setIncludeContext(Boolean.valueOf(includeContext).booleanValue());
        }
        if (escapeAmp != null) {
            url.setEscapeAmp(Boolean.valueOf(escapeAmp).booleanValue());
        }
	    if (forceAddSchemeHostAndPort != null) {
            url.setForceAddSchemeHostAndPort(Boolean.valueOf(forceAddSchemeHostAndPort).booleanValue());
        }
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public void setIncludeContext(String includeContext) {
        this.includeContext = includeContext;
    }
    
    public void setEscapeAmp(String escapeAmp) {
        this.escapeAmp = escapeAmp;
    }

    public void setIncludeParams(String name) {
        includeParams = name;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    public void setPortletUrlType(String portletUrlType) {
        this.portletUrlType = portletUrlType;
    }

    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void setForceAddSchemeHostAndPort(String forceAddSchemeHostAndPort) {
        this.forceAddSchemeHostAndPort = forceAddSchemeHostAndPort;
    }
}
