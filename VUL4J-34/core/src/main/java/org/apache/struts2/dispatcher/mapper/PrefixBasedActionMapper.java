package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class PrefixBasedActionMapper extends DefaultActionMapper implements ActionMapper {

    private static final Logger LOG = LogManager.getLogger(PrefixBasedActionMapper.class);

    protected Container container;
    protected Map<String, ActionMapper> actionMappers = new HashMap<>();

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Inject(StrutsConstants.PREFIX_BASED_MAPPER_CONFIGURATION)
    public void setPrefixBasedActionMappers(String list) {
        String[] mappers = StringUtils.split(StringUtils.trimToEmpty(list), ",");
        for (String mapper : mappers) {
            String[] thisMapper = mapper.split(":");
            if (thisMapper.length == 2) {
                String mapperPrefix = thisMapper[0].trim();
                String mapperName = thisMapper[1].trim();
                Object obj = container.getInstance(ActionMapper.class, mapperName);
                if (obj != null) {
                    actionMappers.put(mapperPrefix, (ActionMapper) obj);
                } else {
                    LOG.debug("invalid PrefixBasedActionMapper config entry: [{}]", mapper);
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        String uri = RequestUtils.getUri(request);
        for (int lastIndex = uri.lastIndexOf('/'); lastIndex > (-1); lastIndex = uri.lastIndexOf('/', lastIndex - 1)) {
            ActionMapper actionMapper = actionMappers.get(uri.substring(0, lastIndex));
            if (actionMapper != null) {
                ActionMapping actionMapping = actionMapper.getMapping(request, configManager);
                LOG.debug("Using ActionMapper [{}]", actionMapper);
                if (actionMapping != null) {
                    if (LOG.isDebugEnabled()) {
                        if (actionMapping.getParams() != null) {
                            LOG.debug("ActionMapper found mapping. Parameters: [{}]", actionMapping.getParams().toString());
                            for (Map.Entry<String, Object> mappingParameterEntry : actionMapping.getParams().entrySet()) {
                                Object paramValue = mappingParameterEntry.getValue();
                                if (paramValue == null) {
                                    LOG.debug("[{}] : null!", mappingParameterEntry.getKey());
                                } else if (paramValue instanceof String[]) {
                                    LOG.debug("[{}] : (String[]) {}", mappingParameterEntry.getKey(), Arrays.toString((String[]) paramValue));
                                } else if (paramValue instanceof String) {
                                    LOG.debug("[{}] : (String) [{}]", mappingParameterEntry.getKey(), paramValue.toString());
                                } else {
                                    LOG.debug("[{}] : (Object) [{}]", mappingParameterEntry.getKey(), paramValue.toString());
                                }
                            }
                        }
                    }
                    return actionMapping;
                } else {
                    LOG.debug("ActionMapper [{}] failed to return an ActionMapping", actionMapper);
                }
            }
        }
        LOG.debug("No ActionMapper found");
        return null;
    }

    public String getUriFromActionMapping(ActionMapping mapping) {
        String namespace = mapping.getNamespace();
        for (int lastIndex = namespace.length(); lastIndex > (-1); lastIndex = namespace.lastIndexOf('/', lastIndex - 1)) {
            ActionMapper actionMapper = actionMappers.get(namespace.substring(0, lastIndex));
            if (actionMapper != null) {
                String uri = actionMapper.getUriFromActionMapping(mapping);
                LOG.debug("Using ActionMapper [{}]", actionMapper);
                if (uri != null) {
                    return uri;
                } else if (LOG.isDebugEnabled()) {
                    LOG.debug("ActionMapper [{}] failed to return an ActionMapping (null)", actionMapper);
                }
            }
        }
        LOG.debug("ActionMapper failed to return a uri");
        return null;
    }

}
