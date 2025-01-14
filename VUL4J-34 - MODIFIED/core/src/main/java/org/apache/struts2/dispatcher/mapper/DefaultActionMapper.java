

package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.PrefixTrie;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;


public class DefaultActionMapper implements ActionMapper {

    private static final Logger LOG = LogManager.getLogger(DefaultActionMapper.class);

    protected static final String METHOD_PREFIX = "method:";
    protected static final String ACTION_PREFIX = "action:";

    protected boolean allowDynamicMethodCalls = false;
    protected boolean allowSlashesInActionNames = false;
    protected boolean alwaysSelectFullNamespace = false;
    protected PrefixTrie prefixTrie = null;
    protected Pattern allowedActionNames = Pattern.compile("[a-zA-Z0-9._!/\\-]*");
    private boolean allowActionPrefix = false;
    private boolean allowActionCrossNamespaceAccess = false;

    protected List<String> extensions = new ArrayList<String>() {{
        add("action");
        add("");
    }};

    protected Container container;

    public DefaultActionMapper() {
        prefixTrie = new PrefixTrie() {
            {
                put(METHOD_PREFIX, new ParameterAction() {
                    public void execute(String key, ActionMapping mapping) {
                        if (allowDynamicMethodCalls) {
                            mapping.setMethod(key.substring(METHOD_PREFIX.length()));
                        }
                    }
                });

                put(ACTION_PREFIX, new ParameterAction() {
                    public void execute(final String key, ActionMapping mapping) {
                        if (allowActionPrefix) {
                            String name = key.substring(ACTION_PREFIX.length());
                            if (allowDynamicMethodCalls) {
                                int bang = name.indexOf('!');
                                if (bang != -1) {
                                    String method = name.substring(bang + 1);
                                    mapping.setMethod(method);
                                    name = name.substring(0, bang);
                                }
                            }
                            String actionName = cleanupActionName(name);
                            if (allowSlashesInActionNames && !allowActionCrossNamespaceAccess) {
                                if (actionName.startsWith("/")) {
                                    actionName = actionName.substring(1);
                                }
                            }
                            if (!allowSlashesInActionNames && !allowActionCrossNamespaceAccess) {
                                if (actionName.lastIndexOf("/") != -1) {
                                    actionName = actionName.substring(actionName.lastIndexOf("/") + 1);
                                }
                            }
                            mapping.setName(actionName);
                        }
                    }
                });

            }
        };
    }

    
    protected void addParameterAction(String prefix, ParameterAction parameterAction) {
        prefixTrie.put(prefix, parameterAction);
    }

    @Inject(StrutsConstants.STRUTS_ENABLE_DYNAMIC_METHOD_INVOCATION)
    public void setAllowDynamicMethodCalls(String enableDynamicMethodCalls) {
        this.allowDynamicMethodCalls = BooleanUtils.toBoolean(enableDynamicMethodCalls);
    }

    @Inject(StrutsConstants.STRUTS_ENABLE_SLASHES_IN_ACTION_NAMES)
    public void setSlashesInActionNames(String enableSlashesInActionNames) {
        this.allowSlashesInActionNames = BooleanUtils.toBoolean(enableSlashesInActionNames);
    }

    @Inject(StrutsConstants.STRUTS_ALWAYS_SELECT_FULL_NAMESPACE)
    public void setAlwaysSelectFullNamespace(String alwaysSelectFullNamespace) {
        this.alwaysSelectFullNamespace = BooleanUtils.toBoolean(alwaysSelectFullNamespace);
    }

    @Inject(value = StrutsConstants.STRUTS_ALLOWED_ACTION_NAMES, required = false)
    public void setAllowedActionNames(String allowedActionNames) {
        this.allowedActionNames = Pattern.compile(allowedActionNames);
    }

    @Inject(value = StrutsConstants.STRUTS_MAPPER_ACTION_PREFIX_ENABLED)
    public void setAllowActionPrefix(String allowActionPrefix) {
        this.allowActionPrefix = BooleanUtils.toBoolean(allowActionPrefix);
    }

    @Inject(value = StrutsConstants.STRUTS_MAPPER_ACTION_PREFIX_CROSSNAMESPACES)
    public void setAllowActionCrossNamespaceAccess(String allowActionCrossNamespaceAccess) {
        this.allowActionCrossNamespaceAccess = BooleanUtils.toBoolean(allowActionCrossNamespaceAccess);
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(StrutsConstants.STRUTS_ACTION_EXTENSION)
    public void setExtensions(String extensions) {
        if (StringUtils.isNotEmpty(extensions)) {
            List<String> list = new ArrayList<>();
            String[] tokens = extensions.split(",");
            Collections.addAll(list, tokens);
            if (extensions.endsWith(",")) {
                list.add("");
            }
            this.extensions = Collections.unmodifiableList(list);
        } else {
            this.extensions = null;
        }
    }

    public ActionMapping getMappingFromActionName(String actionName) {
        ActionMapping mapping = new ActionMapping();
        mapping.setName(actionName);
        return parseActionName(mapping);
    }

    public boolean isSlashesInActionNames() {
        return allowSlashesInActionNames;
    }

    
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        ActionMapping mapping = new ActionMapping();
        String uri = RequestUtils.getUri(request);

        int indexOfSemicolon = uri.indexOf(";");
        uri = (indexOfSemicolon > -1) ? uri.substring(0, indexOfSemicolon) : uri;

        uri = dropExtension(uri, mapping);
        if (uri == null) {
            return null;
        }

        parseNameAndNamespace(uri, mapping, configManager);
        handleSpecialParameters(request, mapping);
        return parseActionName(mapping);
    }

    protected ActionMapping parseActionName(ActionMapping mapping) {
        if (mapping.getName() == null) {
            return null;
        }
        if (allowDynamicMethodCalls) {
            
            String name = mapping.getName();
            int exclamation = name.lastIndexOf("!");
            if (exclamation != -1) {
                mapping.setName(name.substring(0, exclamation));

                mapping.setMethod(name.substring(exclamation + 1));
            }
        }
        return mapping;
    }

    
    public void handleSpecialParameters(HttpServletRequest request, ActionMapping mapping) {
        
        Set<String> uniqueParameters = new HashSet<>();
        Map parameterMap = request.getParameterMap();
        for (Object o : parameterMap.keySet()) {
            String key = (String) o;

            
            if (key.endsWith(".x") || key.endsWith(".y")) {
                key = key.substring(0, key.length() - 2);
            }

            
            if (!uniqueParameters.contains(key)) {
                ParameterAction parameterAction = (ParameterAction) prefixTrie.get(key);
                if (parameterAction != null) {
                    parameterAction.execute(key, mapping);
                    uniqueParameters.add(key);
                    break;
                }
            }
        }
    }

    
    protected void parseNameAndNamespace(String uri, ActionMapping mapping, ConfigurationManager configManager) {
        String namespace, name;
        int lastSlash = uri.lastIndexOf("/");
        if (lastSlash == -1) {
            namespace = "";
            name = uri;
        } else if (lastSlash == 0) {
            
            
            
            namespace = "/";
            name = uri.substring(lastSlash + 1);
        } else if (alwaysSelectFullNamespace) {
            
            namespace = uri.substring(0, lastSlash);
            name = uri.substring(lastSlash + 1);
        } else {
            
            Configuration config = configManager.getConfiguration();
            String prefix = uri.substring(0, lastSlash);
            namespace = "";
            boolean rootAvailable = false;
            
            for (PackageConfig cfg : config.getPackageConfigs().values()) {
                String ns = cfg.getNamespace();
                if (ns != null && prefix.startsWith(ns) && (prefix.length() == ns.length() || prefix.charAt(ns.length()) == '/')) {
                    if (ns.length() > namespace.length()) {
                        namespace = ns;
                    }
                }
                if ("/".equals(ns)) {
                    rootAvailable = true;
                }
            }

            name = uri.substring(namespace.length() + 1);

            
            if (rootAvailable && "".equals(namespace)) {
                namespace = "/";
            }
        }

        if (!allowSlashesInActionNames) {
            int pos = name.lastIndexOf('/');
            if (pos > -1 && pos < name.length() - 1) {
                name = name.substring(pos + 1);
            }
        }

        mapping.setNamespace(namespace);
        mapping.setName(cleanupActionName(name));
    }

    
    protected String cleanupActionName(final String rawActionName) {
        if (allowedActionNames.matcher(rawActionName).matches()) {
            return rawActionName;
        } else {
            LOG.warn("Action [{}] does not match allowed action names pattern [{}], cleaning it up!",
                    rawActionName, allowedActionNames);
            String cleanActionName = rawActionName;
            for (String chunk : allowedActionNames.split(rawActionName)) {
                cleanActionName = cleanActionName.replace(chunk, "");
            }
            LOG.debug("Cleaned action name [{}]", cleanActionName);
            return cleanActionName;
        }
    }

    
    protected String dropExtension(String name, ActionMapping mapping) {
        if (extensions == null) {
            return name;
        }
        for (String ext : extensions) {
            if ("".equals(ext)) {
                
                
                
                int index = name.lastIndexOf('.');
                if (index == -1 || name.indexOf('/', index) >= 0) {
                    return name;
                }
            } else {
                String extension = "." + ext;
                if (name.endsWith(extension)) {
                    name = name.substring(0, name.length() - extension.length());
                    mapping.setExtension(ext);
                    return name;
                }
            }
        }
        return null;
    }

    
    protected String getDefaultExtension() {
        if (extensions == null) {
            return null;
        } else {
            return extensions.get(0);
        }
    }

    
    public String getUriFromActionMapping(ActionMapping mapping) {
        StringBuilder uri = new StringBuilder();

        handleNamespace(mapping, uri);
        handleName(mapping, uri);
        handleDynamicMethod(mapping, uri);
        handleExtension(mapping, uri);
        handleParams(mapping, uri);

        return uri.toString();
    }

    protected void handleNamespace(ActionMapping mapping, StringBuilder uri) {
        if (mapping.getNamespace() != null) {
            uri.append(mapping.getNamespace());
            if (!"/".equals(mapping.getNamespace())) {
                uri.append("/");
            }
        }
    }

    protected void handleName(ActionMapping mapping, StringBuilder uri) {
        String name = mapping.getName();
        if (name.indexOf('?') != -1) {
            name = name.substring(0, name.indexOf('?'));
        }
        uri.append(name);
    }

    protected void handleDynamicMethod(ActionMapping mapping, StringBuilder uri) {
        
        if (StringUtils.isNotEmpty(mapping.getMethod())) {
            if (allowDynamicMethodCalls) {
                
                String name = mapping.getName();
                if (!name.contains("!")) {
                    
                    uri.append("!").append(mapping.getMethod());
                }
            } else {
                uri.append("!").append(mapping.getMethod());
            }
        }
    }

    protected void handleExtension(ActionMapping mapping, StringBuilder uri) {
        String extension = lookupExtension(mapping.getExtension());

        if (extension != null) {
            if (extension.length() == 0 || (extension.length() > 0 && uri.indexOf('.' + extension) == -1)) {
                if (extension.length() > 0) {
                    uri.append(".").append(extension);
                }
            }
        }
    }

    protected String lookupExtension(String extension) {
        if (extension == null) {
            
            ActionContext context = ActionContext.getContext();
            if (context != null) {
                ActionMapping orig = (ActionMapping) context.get(ServletActionContext.ACTION_MAPPING);
                if (orig != null) {
                    extension = orig.getExtension();
                }
            }
            if (extension == null) {
                extension = getDefaultExtension();
            }
        }
        return extension;
    }

    protected void handleParams(ActionMapping mapping, StringBuilder uri) {
        String name = mapping.getName();
        String params = "";
        if (name.indexOf('?') != -1) {
            params = name.substring(name.indexOf('?'));
        }
        if (params.length() > 0) {
            uri.append(params);
        }
    }

}
