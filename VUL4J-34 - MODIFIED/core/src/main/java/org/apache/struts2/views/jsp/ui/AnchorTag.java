

package org.apache.struts2.views.jsp.ui;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.struts2.components.Anchor;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AnchorTag extends AbstractClosingTag {

    private static final long serialVersionUID = -1034616578492431113L;

    protected String href;
    protected String includeParams;
    protected String scheme;
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
        return new Anchor(stack, req, res);
    }
    
    protected void populateParams() {
        super.populateParams();

        Anchor tag = (Anchor) component;
        tag.setHref(href);
        tag.setIncludeParams(includeParams);
        tag.setScheme(scheme);
        tag.setValue(value);
        tag.setMethod(method);
        tag.setNamespace(namespace);
        tag.setAction(action);
        tag.setPortletMode(portletMode);
        tag.setPortletUrlType(portletUrlType);
        tag.setWindowState(windowState);
        tag.setAnchor(anchor);

        if (encode != null) {
            tag.setEncode(BooleanUtils.toBoolean(encode));
        }
        if (includeContext != null) {
            tag.setIncludeContext(BooleanUtils.toBoolean(includeContext));
        }
        if (escapeAmp != null) {
            tag.setEscapeAmp(BooleanUtils.toBoolean(escapeAmp));
        }
	    if (forceAddSchemeHostAndPort != null) {
            tag.setForceAddSchemeHostAndPort(BooleanUtils.toBoolean(forceAddSchemeHostAndPort));
        }
    }
    
    public void setHref(String href) {
        this.href = href;
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


