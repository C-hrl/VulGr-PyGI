

package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.RequestUtils;
import org.apache.struts2.util.URLDecoderUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;


public class RestfulActionMapper implements ActionMapper {
    protected static final Logger LOG = LogManager.getLogger(RestfulActionMapper.class);

    
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
        String uri = RequestUtils.getServletPath(request);

        int nextSlash = uri.indexOf('/', 1);
        if (nextSlash == -1) {
            return null;
        }

        String actionName = uri.substring(1, nextSlash);
        Map<String, Object> parameters = new HashMap<>();
        try {
            StringTokenizer st = new StringTokenizer(uri.substring(nextSlash), "/");
            boolean isNameTok = true;
            String paramName = null;
            String paramValue;

            
            if ((st.countTokens() % 2) != 0) {
                isNameTok = false;
                paramName = actionName + "Id";
            }

            while (st.hasMoreTokens()) {
                if (isNameTok) {
                    paramName = URLDecoderUtil.decode(st.nextToken(), "UTF-8");
                    isNameTok = false;
                } else {
                    paramValue = URLDecoderUtil.decode(st.nextToken(), "UTF-8");

                    if ((paramName != null) && (paramName.length() > 0)) {
                        parameters.put(paramName, paramValue);
                    }

                    isNameTok = true;
                }
            }
        } catch (Exception e) {
        	LOG.warn("Cannot determine url parameters", e);
        }

        return new ActionMapping(actionName, "", "", parameters);
    }

    public ActionMapping getMappingFromActionName(String actionName) {
        return new ActionMapping(actionName, null, null, null);
    }

    
    public String getUriFromActionMapping(ActionMapping mapping) {
        StringBuilder retVal = new StringBuilder();
        retVal.append(mapping.getNamespace());
        retVal.append(mapping.getName());
        Object value = mapping.getParams().get(mapping.getName() + "Id");
        if (value != null) {
            retVal.append("/");
            retVal.append(value);
        } 

        return retVal.toString();
    }
}
