

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.struts2.result.ServletRedirectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


public class MessageStoreInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = 9161650888603380164L;

    private static final Logger LOG = LogManager.getLogger(MessageStoreInterceptor.class);

    public static final String AUTOMATIC_MODE = "AUTOMATIC";
    public static final String STORE_MODE = "STORE";
    public static final String RETRIEVE_MODE = "RETRIEVE";
    public static final String NONE = "NONE";

    private boolean allowRequestParameterSwitch = true;
    private String requestParameterSwitch = "operationMode";
    private String operationMode = NONE;

    public static final String fieldErrorsSessionKey = "__MessageStoreInterceptor_FieldErrors_SessionKey";
    public static final String actionErrorsSessionKey = "__MessageStoreInterceptor_ActionErrors_SessionKey";
    public static final String actionMessagesSessionKey = "__MessageStoreInterceptor_ActionMessages_SessionKey";

    public void setAllowRequestParameterSwitch(boolean allowRequestParameterSwitch) {
        this.allowRequestParameterSwitch = allowRequestParameterSwitch;
    }

    public boolean getAllowRequestParameterSwitch() {
        return this.allowRequestParameterSwitch;
    }

    public void setRequestParameterSwitch(String requestParameterSwitch) {
        this.requestParameterSwitch = requestParameterSwitch;
    }

    public String getRequestParameterSwitch() {
        return this.requestParameterSwitch;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public String getOperationModel() {
        return this.operationMode;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        LOG.trace("entering MessageStoreInterceptor ...");

        before(invocation);

        LOG.trace("Registering listener to store messages before result will be executed");
        invocation.addPreResultListener(new MessageStorePreResultListener(this));

        String result = invocation.invoke();

        LOG.debug("exit executing MessageStoreInterceptor");

        return result;
    }

    
    protected void before(ActionInvocation invocation) throws Exception {
        String reqOperationMode = getRequestOperationMode(invocation);

        if (RETRIEVE_MODE.equalsIgnoreCase(reqOperationMode) ||
                RETRIEVE_MODE.equalsIgnoreCase(operationMode) ||
                AUTOMATIC_MODE.equalsIgnoreCase(operationMode)) {

            Object action = invocation.getAction();
            if (action instanceof ValidationAware) {
                
                Map session = (Map) invocation.getInvocationContext().get(ActionContext.SESSION);

                if (session == null) {
                    LOG.debug("Session is not open, no errors / messages could be retrieve for action [{}]", action);
                    return;
                }

                ValidationAware validationAwareAction = (ValidationAware) action;

                LOG.debug("Retrieve error / message from session to populate into action [{}]", action);

                Collection actionErrors = (Collection) session.get(actionErrorsSessionKey);
                Collection actionMessages = (Collection) session.get(actionMessagesSessionKey);
                Map fieldErrors = (Map) session.get(fieldErrorsSessionKey);

                if (actionErrors != null && actionErrors.size() > 0) {
                    Collection mergedActionErrors = mergeCollection(validationAwareAction.getActionErrors(), actionErrors);
                    validationAwareAction.setActionErrors(mergedActionErrors);
                }

                if (actionMessages != null && actionMessages.size() > 0) {
                    Collection mergedActionMessages = mergeCollection(validationAwareAction.getActionMessages(), actionMessages);
                    validationAwareAction.setActionMessages(mergedActionMessages);
                }

                if (fieldErrors != null && fieldErrors.size() > 0) {
                    Map mergedFieldErrors = mergeMap(validationAwareAction.getFieldErrors(), fieldErrors);
                    validationAwareAction.setFieldErrors(mergedFieldErrors);
                }
                session.remove(actionErrorsSessionKey);
                session.remove(actionMessagesSessionKey);
                session.remove(fieldErrorsSessionKey);
            }
        }
    }

    
    protected String getRequestOperationMode(ActionInvocation invocation) {
        String reqOperationMode = NONE;
        if (allowRequestParameterSwitch) {
            Map reqParams = (Map) invocation.getInvocationContext().get(ActionContext.PARAMETERS);
            boolean containsParameter = reqParams.containsKey(requestParameterSwitch);
            if (containsParameter) {
                String[] reqParamsArr = (String[]) reqParams.get(requestParameterSwitch);
                if (reqParamsArr != null && reqParamsArr.length > 0) {
                    reqOperationMode = reqParamsArr[0];
                }
            }
        }
        return reqOperationMode;
    }

    
    protected Collection mergeCollection(Collection col1, Collection col2) {
        Collection _col1 = (col1 == null ? new ArrayList() : col1);
        Collection _col2 = (col2 == null ? new ArrayList() : col2);
        _col1.addAll(_col2);
        return _col1;
    }

    
    protected Map mergeMap(Map map1, Map map2) {
        Map _map1 = (map1 == null ? new LinkedHashMap() : map1);
        Map _map2 = (map2 == null ? new LinkedHashMap() : map2);
        _map1.putAll(_map2);
        return _map1;
    }

}
