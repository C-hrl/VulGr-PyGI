
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.opensymphony.xwork2.util.AnnotationUtils;
import java.lang.reflect.Method;


public class DefaultWorkflowInterceptor extends MethodFilterInterceptor {

    private static final long serialVersionUID = 7563014655616490865L;

    private static final Logger LOG = LogManager.getLogger(DefaultWorkflowInterceptor.class);

    private static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    private String inputResultName = Action.INPUT;

    
    public void setInputResultName(String inputResultName) {
        this.inputResultName = inputResultName;
    }

    
    @Override
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            ValidationAware validationAwareAction = (ValidationAware) action;

            if (validationAwareAction.hasErrors()) {
                LOG.debug("Errors on action [{}], returning result name [{}]", validationAwareAction, inputResultName);

                String resultName = inputResultName;
                resultName = processValidationWorkflowAware(action, resultName);
                resultName = processInputConfig(action, invocation.getProxy().getMethod(), resultName);
                resultName = processValidationErrorAware(action, resultName);

                return resultName;
            }
        }

        return invocation.invoke();
    }

    
    private String processValidationWorkflowAware(final Object action, final String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationWorkflowAware) {
            resultName = ((ValidationWorkflowAware) action).getInputResultName();
            LOG.debug("Changing result name from [{}] to [{}] because of processing [{}] interface applied to [{}]",
                        currentResultName, resultName, ValidationWorkflowAware.class.getSimpleName(), action);
        }
        return resultName;
    }

    
    protected String processInputConfig(final Object action, final String method, final String currentResultName) throws Exception {
        String resultName = currentResultName;
        InputConfig annotation = action.getClass().getMethod(method, EMPTY_CLASS_ARRAY).getAnnotation(InputConfig.class);
        if (annotation != null) {
            if (StringUtils.isNotEmpty(annotation.methodName())) {
                Method m = action.getClass().getMethod(annotation.methodName());
                resultName = (String) m.invoke(action);
            } else {
                resultName = annotation.resultName();
            }
            LOG.debug("Changing result name from [{}] to [{}] because of processing annotation [{}] on action [{}]",
                        currentResultName, resultName, InputConfig.class.getSimpleName(), action);
        }
        return resultName;
    }

    
    protected String processValidationErrorAware(final Object action, final String currentResultName) {
        String resultName = currentResultName;
        if (action instanceof ValidationErrorAware) {
            resultName = ((ValidationErrorAware) action).actionErrorOccurred(currentResultName);
            LOG.debug("Changing result name from [{}] to [{}] because of processing interface [{}] on action [{}]",
                        currentResultName, resultName, ValidationErrorAware.class.getSimpleName(), action);
        }
        return resultName;
    }

}
