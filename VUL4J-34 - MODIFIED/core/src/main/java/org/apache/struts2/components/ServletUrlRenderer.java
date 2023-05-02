

package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.util.UrlHelper;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;


public class ServletUrlRenderer implements UrlRenderer {
    
    private static final Logger LOG = LogManager.getLogger(ServletUrlRenderer.class);

    private ActionMapper actionMapper;
    private UrlHelper urlHelper;

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @Inject
    public void setUrlHelper(UrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    
    public void renderUrl(Writer writer, UrlProvider urlComponent) {
        String scheme = urlComponent.getHttpServletRequest().getScheme();

        if (urlComponent.getScheme() != null) {
            ValueStack vs = ActionContext.getContext().getValueStack();
            scheme = vs.findString(urlComponent.getScheme());
            if (scheme == null) {
                scheme = urlComponent.getScheme();
            }
        }

        String result;
        ActionInvocation ai = (ActionInvocation) ActionContext.getContext().get(ActionContext.ACTION_INVOCATION);
        if (urlComponent.getValue() == null && urlComponent.getAction() != null) {
            result = urlComponent.determineActionURL(urlComponent.getAction(), urlComponent.getNamespace(), urlComponent.getMethod(), urlComponent.getHttpServletRequest(), urlComponent.getHttpServletResponse(), urlComponent.getParameters(), scheme, urlComponent.isIncludeContext(), urlComponent.isEncode(), urlComponent.isForceAddSchemeHostAndPort(), urlComponent.isEscapeAmp());
        } else if (urlComponent.getValue() == null && urlComponent.getAction() == null && ai != null) {
            

            final String action = ai.getProxy().getActionName();
            final String namespace = ai.getProxy().getNamespace();
            final String method = urlComponent.getMethod() != null || !ai.getProxy().isMethodSpecified() ? urlComponent.getMethod() : ai.getProxy().getMethod();
            result = urlComponent.determineActionURL(action, namespace, method, urlComponent.getHttpServletRequest(), urlComponent.getHttpServletResponse(), urlComponent.getParameters(), scheme, urlComponent.isIncludeContext(), urlComponent.isEncode(), urlComponent.isForceAddSchemeHostAndPort(), urlComponent.isEscapeAmp());
        } else {
            String _value = urlComponent.getValue();

            
            
            if (_value != null && _value.indexOf("?") > 0) {
                _value = _value.substring(0, _value.indexOf("?"));
            }
            result = urlHelper.buildUrl(_value, urlComponent.getHttpServletRequest(), urlComponent.getHttpServletResponse(), urlComponent.getParameters(), scheme, urlComponent.isIncludeContext(), urlComponent.isEncode(), urlComponent.isForceAddSchemeHostAndPort(), urlComponent.isEscapeAmp());
        }
        String anchor = urlComponent.getAnchor();
        if (StringUtils.isNotEmpty(anchor)) {
            result += '#' + urlComponent.findString(anchor);
        }

        if (urlComponent.isPutInContext()) {
            String var = urlComponent.getVar();
            if (StringUtils.isNotEmpty(var)) {
                urlComponent.putInContext(result);

                
                urlComponent.getHttpServletRequest().setAttribute(var, result);
            } else {
                try {
                    writer.write(result);
                } catch (IOException e) {
                    throw new StrutsException("IOError: " + e.getMessage(), e);
                }
            }
        } else {
            try {
                writer.write(result);
            } catch (IOException e) {
                throw new StrutsException("IOError: " + e.getMessage(), e);
            }
        }
    }

    
    public void renderFormUrl(Form formComponent) {
        String namespace = formComponent.determineNamespace(formComponent.namespace, formComponent.getStack(), formComponent.request);
        String action;

        ValueStack vs = ActionContext.getContext().getValueStack();
        String scheme = vs.findString("scheme");

        if (formComponent.action != null) {
            action = formComponent.findString(formComponent.action);
        } else {
            
            
            ActionInvocation ai = (ActionInvocation) formComponent.getStack().getContext().get(
                    ActionContext.ACTION_INVOCATION);
            if (ai != null) {
                action = ai.getProxy().getActionName();
                namespace = ai.getProxy().getNamespace();
            } else {
                
                String uri = formComponent.request.getRequestURI();
                action = uri.substring(uri.lastIndexOf('/'));
            }
        }

        Map actionParams = null;
        if (action != null && action.indexOf("?") > 0) {
            String queryString = action.substring(action.indexOf("?") + 1);
            actionParams = urlHelper.parseQueryString(queryString, false);
            action = action.substring(0, action.indexOf("?"));
        }

        ActionMapping nameMapping = actionMapper.getMappingFromActionName(action);
        String actionName = nameMapping.getName();
        String actionMethod = nameMapping.getMethod();

        final ActionConfig actionConfig = formComponent.configuration.getRuntimeConfiguration().getActionConfig(
                namespace, actionName);
        if (actionConfig != null) {

            ActionMapping mapping = new ActionMapping(actionName, namespace, actionMethod, formComponent.parameters);
            String result = urlHelper.buildUrl(formComponent.actionMapper.getUriFromActionMapping(mapping),
                    formComponent.request, formComponent.response, actionParams, scheme, formComponent.includeContext, true, false, false);
            formComponent.addParameter("action", result);

            
            
            formComponent.addParameter("actionName", actionName);
            try {
                Class clazz = formComponent.objectFactory.getClassInstance(actionConfig.getClassName());
                formComponent.addParameter("actionClass", clazz);
            } catch (ClassNotFoundException e) {
                
            }

            formComponent.addParameter("namespace", namespace);

            
            if (formComponent.name == null) {
                formComponent.addParameter("name", actionName);
            }

            
            if (formComponent.getId() == null && actionName != null) {
                formComponent.addParameter("id", formComponent.escape(actionName));
            }
        } else if (action != null) {
            
            
            

            
            
            if (namespace != null && LOG.isWarnEnabled()) {
                LOG.warn("No configuration found for the specified action: '{}' in namespace: '{}'. Form action defaulting to 'action' attribute's literal value.", actionName, namespace);
            }

            String result = urlHelper.buildUrl(action, formComponent.request, formComponent.response, null, scheme, formComponent.includeContext, true);
            formComponent.addParameter("action", result);

            
            int slash = result.lastIndexOf('/');
            if (slash != -1) {
                formComponent.addParameter("namespace", result.substring(0, slash));
            } else {
                formComponent.addParameter("namespace", "");
            }

            
            
            String id = formComponent.getId();
            if (id == null) {
                slash = result.lastIndexOf('/');
                int dot = result.indexOf('.', slash);
                if (dot != -1) {
                    id = result.substring(slash + 1, dot);
                } else {
                    id = result.substring(slash + 1);
                }
                formComponent.addParameter("id", formComponent.escape(id));
            }
        }

        
        
        
        formComponent.evaluateClientSideJsEnablement(actionName, namespace, actionMethod);
    }


    public void beforeRenderUrl(UrlProvider urlComponent) {
        if (urlComponent.getValue() != null) {
            urlComponent.setValue(urlComponent.findString(urlComponent.getValue()));
        }

        
        
        try {
            
            String includeParams = (urlComponent.getUrlIncludeParams() != null ? urlComponent.getUrlIncludeParams().toLowerCase() : UrlProvider.GET);

            if (urlComponent.getIncludeParams() != null) {
                includeParams = urlComponent.findString(urlComponent.getIncludeParams());
            }

            if (UrlProvider.NONE.equalsIgnoreCase(includeParams)) {
                mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), Collections.<String, Object>emptyMap());
            } else if (UrlProvider.ALL.equalsIgnoreCase(includeParams)) {
                mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), urlComponent.getHttpServletRequest().getParameterMap());

                
                includeGetParameters(urlComponent);
                includeExtraParameters(urlComponent);
            } else if (UrlProvider.GET.equalsIgnoreCase(includeParams) || (includeParams == null && urlComponent.getValue() == null && urlComponent.getAction() == null)) {
                includeGetParameters(urlComponent);
                includeExtraParameters(urlComponent);
            } else if (includeParams != null) {
                LOG.warn("Unknown value for includeParams parameter to URL tag: {}", includeParams);
            }
        } catch (Exception e) {
            LOG.warn("Unable to put request parameters ({}) into parameter map.", urlComponent.getHttpServletRequest().getQueryString(), e);
        }
    }

    private void includeExtraParameters(UrlProvider urlComponent) {
        if (urlComponent.getExtraParameterProvider() != null) {
            mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), urlComponent.getExtraParameterProvider().getExtraParameters());
        }
    }

    private void includeGetParameters(UrlProvider urlComponent) {
        String query = extractQueryString(urlComponent);
        mergeRequestParameters(urlComponent.getValue(), urlComponent.getParameters(), urlHelper.parseQueryString(query, false));
    }

    private String extractQueryString(UrlProvider urlComponent) {
        
        String query = urlComponent.getHttpServletRequest().getQueryString();
        if (query == null) {
            query = (String) urlComponent.getHttpServletRequest().getAttribute("javax.servlet.forward.query_string");
        }

        if (query != null) {
            
            int idx = query.lastIndexOf('#');

            if (idx != -1) {
                query = query.substring(0, idx);
            }
        }
        return query;
    }

    
    protected void mergeRequestParameters(String value, Map<String, Object> parameters, Map<String, Object> contextParameters) {

        Map<String, Object> mergedParams = new LinkedHashMap<>(contextParameters);

        
        
        

        if (StringUtils.contains(value, "?")) {
            String queryString = value.substring(value.indexOf("?") + 1);

            mergedParams = urlHelper.parseQueryString(queryString, false);
            for (Map.Entry<String, Object> entry : contextParameters.entrySet()) {
                if (!mergedParams.containsKey(entry.getKey())) {
                    mergedParams.put(entry.getKey(), entry.getValue());
                }
            }
        }

        
        
        
        
        

        for (Map.Entry<String, Object> entry : mergedParams.entrySet()) {
            if (!parameters.containsKey(entry.getKey())) {
                parameters.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
