

package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;


public class CompositeActionMapper implements ActionMapper {

    private static final Logger LOG = LogManager.getLogger(CompositeActionMapper.class);

    protected List<ActionMapper> actionMappers = new LinkedList<>();

    @Inject
    public CompositeActionMapper(Container container,
                                 @Inject(value = StrutsConstants.STRUTS_MAPPER_COMPOSITE) String list) {
        String[] arr = StringUtils.split(StringUtils.trimToEmpty(list), ",");
        for (String name : arr) {
            Object obj = container.getInstance(ActionMapper.class, name);
            if (obj != null) {
                actionMappers.add((ActionMapper) obj);
            }
        }
    }

    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {

        for (ActionMapper actionMapper : actionMappers) {
            ActionMapping actionMapping = actionMapper.getMapping(request, configManager);
            LOG.debug("Using ActionMapper: {}", actionMapper);
            if (actionMapping == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", actionMapper);
            }
            else {
                return actionMapping;
            }
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }

    public ActionMapping getMappingFromActionName(String actionName) {

        for (ActionMapper actionMapper : actionMappers) {
            ActionMapping actionMapping = actionMapper.getMappingFromActionName(actionName);
            LOG.debug("Using ActionMapper: {}", actionMapper);
            if (actionMapping == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", actionMapper);
            }
            else {
                return actionMapping;
            }
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }

    public String getUriFromActionMapping(ActionMapping mapping) {

        for (ActionMapper actionMapper : actionMappers) {
            String uri = actionMapper.getUriFromActionMapping(mapping);
            LOG.debug("Using ActionMapper: {}", actionMapper);
            if (uri == null) {
                LOG.debug("ActionMapper {} failed to return an ActionMapping (null)", actionMapper);
            }
            else {
                return uri;
            }
        }
        LOG.debug("exhausted from ActionMapper that could return an ActionMapping");
        return null;
    }
}
