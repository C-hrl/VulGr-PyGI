

package org.apache.struts2.dispatcher.mapper;

import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.util.URLDecoderUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.StringTokenizer;


public class Restful2ActionMapper extends DefaultActionMapper {

    protected static final Logger LOG = LogManager.getLogger(Restful2ActionMapper.class);
    public static final String HTTP_METHOD_PARAM = "__http_method";
    private String idParameterName = null;
    
    public Restful2ActionMapper() {
    	setSlashesInActionNames("true");
    }

    
    public ActionMapping getMapping(HttpServletRequest request, ConfigurationManager configManager) {
    	if (!isSlashesInActionNames()) {
    		throw new IllegalStateException("This action mapper requires the setting 'slashesInActionNames' to be set to 'true'");
    	}
        ActionMapping mapping = super.getMapping(request, configManager);
        
        if (mapping == null) {
            return null;
        }

        String actionName = mapping.getName();
        String id = null;

        
        if (StringUtils.isNotBlank(actionName)) {

            int lastSlashPos = actionName.lastIndexOf('/');
            if (lastSlashPos > -1) {
                id = actionName.substring(lastSlashPos+1);
            }

            
            if (mapping.getMethod() == null) {

                if (lastSlashPos == actionName.length() -1) {

                    
                    if (isGet(request)) {
                        mapping.setMethod("index");
                        
                    
                    } else if (isPost(request)) {
                        mapping.setMethod("create");
                    }

                } else if (lastSlashPos > -1) {
                    
                    if (isGet(request) && "new".equals(id)) {
                        mapping.setMethod("editNew");

                    
                    } else if (isGet(request)) {
                        mapping.setMethod("view");

                    
                    } else if (isDelete(request)) {
                        mapping.setMethod("remove");
                    
                    
                    }  else if (isPut(request)) {
                        mapping.setMethod("update");
                    }
                    
                }
                
                if (idParameterName != null && lastSlashPos > -1) {
                	actionName = actionName.substring(0, lastSlashPos);
                }
            }

            if (idParameterName != null && id != null) {
                if (mapping.getParams() == null) {
                    mapping.setParams(new HashMap<String, Object>());
                }
                mapping.getParams().put(idParameterName, id);
            }

            
            int actionSlashPos = actionName.lastIndexOf('/', lastSlashPos - 1);
            if (actionSlashPos > 0 && actionSlashPos < lastSlashPos) {
                String params = actionName.substring(0, actionSlashPos);
                HashMap<String, String> parameters = new HashMap<>();
                try {
                    StringTokenizer st = new StringTokenizer(params, "/");
                    boolean isNameTok = true;
                    String paramName = null;
                    String paramValue;

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
                    if (parameters.size() > 0) {
                        if (mapping.getParams() == null) {
                            mapping.setParams(new HashMap<String, Object>());
                        }
                        mapping.getParams().putAll(parameters);
                    }
                } catch (Exception e) {
                	LOG.warn("Unable to determine parameters from the url", e);
                }
                mapping.setName(actionName.substring(actionSlashPos+1));
            }
        }

        return mapping;
    }

    protected boolean isGet(HttpServletRequest request) {
        return "get".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPost(HttpServletRequest request) {
        return "post".equalsIgnoreCase(request.getMethod());
    }

    protected boolean isPut(HttpServletRequest request) {
        if ("put".equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return isPost(request) && "put".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

    protected boolean isDelete(HttpServletRequest request) {
        if ("delete".equalsIgnoreCase(request.getMethod())) {
            return true;
        } else {
            return isPost(request) && "delete".equalsIgnoreCase(request.getParameter(HTTP_METHOD_PARAM));
        }
    }

	public String getIdParameterName() {
		return idParameterName;
	}

	@Inject(required=false,value=StrutsConstants.STRUTS_ID_PARAMETER_NAME)
	public void setIdParameterName(String idParameterName) {
		this.idParameterName = idParameterName;
	}
}
