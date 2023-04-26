

package org.apache.struts2.views.util;

import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.URLDecoderUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;


public class DefaultUrlHelper implements UrlHelper {

    private static final Logger LOG = LogManager.getLogger(DefaultUrlHelper.class);

    public static final String HTTP_PROTOCOL = "http";
    public static final String HTTPS_PROTOCOL = "https";

    private String encoding = "UTF-8";
    private int httpPort = DEFAULT_HTTP_PORT;
    private int httpsPort = DEFAULT_HTTPS_PORT;

    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public void setEncoding(String encoding) {
        if (StringUtils.isNotEmpty(encoding)) {
            this.encoding = encoding;
        }
    }

    @Inject(StrutsConstants.STRUTS_URL_HTTP_PORT)
    public void setHttpPort(String httpPort) {
        this.httpPort = Integer.parseInt(httpPort);
    }

    @Inject(StrutsConstants.STRUTS_URL_HTTPS_PORT)
    public void setHttpsPort(String httpsPort) {
        this.httpsPort = Integer.parseInt(httpsPort);
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params) {
        return buildUrl(action, request, response, params, null, true, true);
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                           boolean includeContext, boolean encodeResult) {
        return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, false);
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String scheme,
                           boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort) {
    	return buildUrl(action, request, response, params, scheme, includeContext, encodeResult, forceAddSchemeHostAndPort, true);
    }

    public String buildUrl(String action, HttpServletRequest request, HttpServletResponse response, Map<String, Object> params, String urlScheme,
                           boolean includeContext, boolean encodeResult, boolean forceAddSchemeHostAndPort, boolean escapeAmp) {

        StringBuilder link = new StringBuilder();
        boolean changedScheme = false;

        String scheme = null;
        if (isValidScheme(urlScheme)) {
            scheme = urlScheme;
        }

        
        
        if (forceAddSchemeHostAndPort) {
            String reqScheme = request.getScheme();
            changedScheme = true;
            link.append(scheme != null ? scheme : reqScheme);
            link.append(":
            link.append(request.getServerName());

            if (scheme != null) {
                
                if (!scheme.equals(reqScheme)) {
                    if ((HTTP_PROTOCOL.equals(scheme) && (httpPort != DEFAULT_HTTP_PORT)) || (HTTPS_PROTOCOL.equals(scheme) && httpsPort != DEFAULT_HTTPS_PORT)) {
                        link.append(":");
                        link.append(HTTP_PROTOCOL.equals(scheme) ? httpPort : httpsPort);
                    }
                
                } else {
                    int reqPort = request.getServerPort();

                    if ((scheme.equals(HTTP_PROTOCOL) && (reqPort != DEFAULT_HTTP_PORT)) || (scheme.equals(HTTPS_PROTOCOL) && reqPort != DEFAULT_HTTPS_PORT)) {
                        link.append(":");
                        link.append(reqPort);
                    }
                }
            }
        } else if ((scheme != null) && !scheme.equals(request.getScheme())) {
            changedScheme = true;
            link.append(scheme);
            link.append(":
            link.append(request.getServerName());

            if ((scheme.equals(HTTP_PROTOCOL) && (httpPort != DEFAULT_HTTP_PORT)) || (HTTPS_PROTOCOL.equals(scheme) && httpsPort != DEFAULT_HTTPS_PORT))
            {
                link.append(":");
                link.append(HTTP_PROTOCOL.equals(scheme) ? httpPort : httpsPort);
            }
        }

        if (action != null) {
            
            
            if (action.startsWith("/") && includeContext) {
                String contextPath = request.getContextPath();
                if (!contextPath.equals("/")) {
                    link.append(contextPath);
                }
            } else if (changedScheme) {

                
                
                String uri = (String) request.getAttribute("javax.servlet.forward.request_uri");

                
                if (uri == null) {
                    uri = request.getRequestURI();
                }

                link.append(uri.substring(0, uri.lastIndexOf('/') + 1));
            }

            
            link.append(action);
        } else {
            
            String requestURI = (String) request.getAttribute("struts.request_uri");

            
            
            if (requestURI == null) {
                requestURI = (String) request.getAttribute("javax.servlet.forward.request_uri");
            }

            
            if (requestURI == null) {
                requestURI = request.getRequestURI();
            }

            link.append(requestURI);
        }

        
        if (escapeAmp) {
            buildParametersString(params, link, AMP);
        } else {
            buildParametersString(params, link, "&");
        }

        String result = link.toString();

        if (StringUtils.containsIgnoreCase(result, "<script")){
            result = StringEscapeUtils.escapeEcmaScript(result);
        }
        try {
            result = encodeResult ? response.encodeURL(result) : result;
        } catch (Exception ex) {
            LOG.debug("Could not encode the URL for some reason, use it unchanged", ex);
            result = link.toString();
        }

        return result;
    }

    public void buildParametersString(Map<String, Object> params, StringBuilder link, String paramSeparator) {
        if ((params != null) && (params.size() > 0)) {
            if (!link.toString().contains("?")) {
                link.append("?");
            } else {
                link.append(paramSeparator);
            }

            
            Iterator<Map.Entry<String, Object>> iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();
                String name = entry.getKey();
                Object value = entry.getValue();

                if (value instanceof Iterable) {
                    for (Iterator iterator = ((Iterable) value).iterator(); iterator.hasNext();) {
                        Object paramValue = iterator.next();
                        link.append(buildParameterSubstring(name, paramValue != null ? paramValue.toString() : StringUtils.EMPTY));

                        if (iterator.hasNext()) {
                            link.append(paramSeparator);
                        }
                    }
                } else if (value instanceof Object[]) {
                    Object[] array = (Object[]) value;
                    for (int i = 0; i < array.length; i++) {
                        Object paramValue = array[i];
                        link.append(buildParameterSubstring(name, paramValue != null ? paramValue.toString() : StringUtils.EMPTY));

                        if (i < array.length - 1) {
                            link.append(paramSeparator);
                        }
                    }
                } else {
                    link.append(buildParameterSubstring(name, value != null ? value.toString() : StringUtils.EMPTY));
                }

                if (iter.hasNext()) {
                    link.append(paramSeparator);
                }
            }
        }
    }

    protected boolean isValidScheme(String scheme) {
        return HTTP_PROTOCOL.equals(scheme) || HTTPS_PROTOCOL.equals(scheme);
    }

    private String buildParameterSubstring(String name, String value) {
        StringBuilder builder = new StringBuilder();
        builder.append(encode(name));
        builder.append('=');
        builder.append(encode(value));
        return builder.toString();
    }

	
	public String encode( String input ) {
		try {
			return URLEncoder.encode(input, encoding);
		} catch (UnsupportedEncodingException e) {
    		LOG.warn("Could not encode URL parameter '{}', returning value un-encoded", input);
			return input;
		}
	}

	
	public String decode( String input ) {
		try {
            return URLDecoderUtil.decode(input, encoding);
		} catch (Exception e) {
    		LOG.warn("Could not decode URL parameter '{}', returning value un-decoded", input);
			return input;
		}
	}

    public Map<String, Object> parseQueryString(String queryString, boolean forceValueArray) {
        Map<String, Object> queryParams = new LinkedHashMap<String, Object>();
        if (queryString != null) {
            String[] params = queryString.split("&");
            for (String param : params) {
                if (param.trim().length() > 0) {
                    String[] tmpParams = param.split("=");
                    String paramName = null;
                    String paramValue = "";
                    if (tmpParams.length > 0) {
                        paramName = tmpParams[0];
                    }
                    if (tmpParams.length > 1) {
                        paramValue = tmpParams[1];
                    }
                    if (paramName != null) {
                        paramName = decode(paramName);
                        String translatedParamValue = decode(paramValue);

                        if (queryParams.containsKey(paramName) || forceValueArray) {
                            
                            Object currentParam = queryParams.get(paramName);
                            if (currentParam instanceof String) {
                                queryParams.put(paramName, new String[]{(String) currentParam, translatedParamValue});
                            } else {
                                String currentParamValues[] = (String[]) currentParam;
                                if (currentParamValues != null) {
                                    List<String> paramList = new ArrayList<String>(Arrays.asList(currentParamValues));
                                    paramList.add(translatedParamValue);
                                    queryParams.put(paramName, paramList.toArray(new String[paramList.size()]));
                                } else {
                                    queryParams.put(paramName, new String[]{translatedParamValue});
                                }
                            }
                        } else {
                            queryParams.put(paramName, translatedParamValue);
                        }
                    }
                }
            }
        }
        return queryParams;
    }
}
