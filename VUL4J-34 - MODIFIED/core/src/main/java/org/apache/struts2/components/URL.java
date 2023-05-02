

package org.apache.struts2.components;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;


@StrutsTag(name="url", tldTagClass="org.apache.struts2.views.jsp.URLTag", description="This tag is used to create a URL")
public class URL extends ContextBean {

    private UrlProvider urlProvider;
    private UrlRenderer urlRenderer;

    public URL(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack);
        urlProvider = new ComponentUrlProvider(this, this.parameters);
        urlProvider.setHttpServletRequest(req);
        urlProvider.setHttpServletResponse(res);
    }

    @Inject(StrutsConstants.STRUTS_URL_INCLUDEPARAMS)
    public void setUrlIncludeParams(String urlIncludeParams) {
       urlProvider.setUrlIncludeParams(urlIncludeParams);
    }

    @Inject
	public void setUrlRenderer(UrlRenderer urlRenderer) {
		urlProvider.setUrlRenderer(urlRenderer);
        this.urlRenderer = urlRenderer;
	}

    @Inject(required=false)
    public void setExtraParameterProvider(ExtraParameterProvider provider) {
        urlProvider.setExtraParameterProvider(provider);
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);
        urlRenderer.beforeRenderUrl(urlProvider);
        return result;
    }

    public boolean end(Writer writer, String body) {
    	urlRenderer.renderUrl(writer, urlProvider);
        return super.end(writer, body);
    }

    public String findString(String expr) {
        return super.findString(expr);
    }

    public UrlProvider getUrlProvider() {
        return urlProvider;
    }

    @StrutsTagAttribute(description="The includeParams attribute may have the value 'none', 'get' or 'all'", defaultValue="none")
    public void setIncludeParams(String includeParams) {
        urlProvider.setIncludeParams(includeParams);
    }

    @StrutsTagAttribute(description="Set scheme attribute")
    public void setScheme(String scheme) {
        urlProvider.setScheme(scheme);
    }

    @StrutsTagAttribute(description="The target value to use, if not using action")
    public void setValue(String value) {
        urlProvider.setValue(value);
    }

    @StrutsTagAttribute(description="The action to generate the URL for, if not using value")
    public void setAction(String action) {
        urlProvider.setAction(action);
    }

    @StrutsTagAttribute(description="The namespace to use")
    public void setNamespace(String namespace) {
        urlProvider.setNamespace(namespace);
    }

    @StrutsTagAttribute(description="The method of action to use")
    public void setMethod(String method) {
        urlProvider.setMethod(method);
    }

    @StrutsTagAttribute(description="Whether to encode parameters", type="Boolean", defaultValue="true")
    public void setEncode(boolean encode) {
        urlProvider.setEncode(encode);
    }

    @StrutsTagAttribute(description="Whether actual context should be included in URL", type="Boolean", defaultValue="true")
    public void setIncludeContext(boolean includeContext) {
        urlProvider.setIncludeContext(includeContext);
    }

    @StrutsTagAttribute(description="The resulting portlet mode")
    public void setPortletMode(String portletMode) {
        urlProvider.setPortletMode(portletMode);
    }

    @StrutsTagAttribute(description="The resulting portlet window state")
    public void setWindowState(String windowState) {
        urlProvider.setWindowState(windowState);
    }

    @StrutsTagAttribute(description="Specifies if this should be a portlet render or action URL. Default is \"render\". To create an action URL, use \"action\".")
    public void setPortletUrlType(String portletUrlType) {
       urlProvider.setPortletUrlType(portletUrlType);
    }

    @StrutsTagAttribute(description="The anchor for this URL")
    public void setAnchor(String anchor) {
        urlProvider.setAnchor(anchor);
    }

    @StrutsTagAttribute(description="Specifies whether to escape ampersand (&amp;) to (&amp;amp;) or not", type="Boolean", defaultValue="true")
    public void setEscapeAmp(boolean escapeAmp) {
        urlProvider.setEscapeAmp(escapeAmp);
    }

    @StrutsTagAttribute(description="Specifies whether to force the addition of scheme, host and port or not", type="Boolean", defaultValue="false")
    public void setForceAddSchemeHostAndPort(boolean forceAddSchemeHostAndPort) {
        urlProvider.setForceAddSchemeHostAndPort(forceAddSchemeHostAndPort);
    }
}
