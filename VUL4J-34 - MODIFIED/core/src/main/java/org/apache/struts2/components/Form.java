

package org.apache.struts2.components;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.InterceptorMapping;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptorUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.*;
import com.opensymphony.xwork2.validator.validators.VisitorFieldValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.TagUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@StrutsTag(
    name="form",
    tldTagClass="org.apache.struts2.views.jsp.ui.FormTag",
    description="Renders an input form",
    allowDynamicAttributes=true)
public class Form extends ClosingUIBean {
    public static final String OPEN_TEMPLATE = "form";
    public static final String TEMPLATE = "form-close";

    private int sequence = 0;

    protected String onsubmit;
    protected String onreset;
    protected String action;
    protected String target;
    protected String enctype;
    protected String method;
    protected String namespace;
    protected String validate;
    protected String portletMode;
    protected String windowState;
    protected String acceptcharset;
    protected boolean includeContext = true;

    protected String focusElement;
    protected Configuration configuration;
    protected ObjectFactory objectFactory;
    protected UrlRenderer urlRenderer;
    protected ActionValidatorManager actionValidatorManager;

    public Form(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    @Override
    protected boolean evaluateNameValue() {
        return false;
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Inject
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setUrlRenderer(UrlRenderer urlRenderer) {
    	this.urlRenderer = urlRenderer;
    }

    @Inject
    public void setActionValidatorManager(ActionValidatorManager mgr) {
        this.actionValidatorManager = mgr;
    }


    
    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (validate != null) {
            addParameter("validate", findValue(validate, Boolean.class));
        }

        if (name == null) {
            
            String id = (String) getParameters().get("id");
             if (StringUtils.isNotEmpty(id)) {
                addParameter("name", id);
             }
        }

        if (onsubmit != null) {
            addParameter("onsubmit", findString(onsubmit));
        }

        if (onreset != null) {
            addParameter("onreset", findString(onreset));
        }

        if (target != null) {
            addParameter("target", findString(target));
        }

        if (enctype != null) {
            addParameter("enctype", findString(enctype));
        }

        if (method != null) {
            addParameter("method", findString(method));
        }

        if (acceptcharset != null) {
            addParameter("acceptcharset", findString(acceptcharset));
        }

        
        
        if (!parameters.containsKey("tagNames")) {
            
            addParameter("tagNames", new ArrayList());
        }

        if (focusElement != null) {
            addParameter("focusElement", findString(focusElement));
        }
    }

    
    @Override
    protected void populateComponentHtmlId(Form form) {
        if (id != null) {
            addParameter("id", escape(id));
        }

        
        
        urlRenderer.renderFormUrl(this);
    }

    
    protected void evaluateClientSideJsEnablement(String actionName, String namespace, String actionMethod) {

        
        Boolean validate = (Boolean) getParameters().get("validate");
        if (validate != null && validate) {

            addParameter("performValidation", Boolean.FALSE);

            RuntimeConfiguration runtimeConfiguration = configuration.getRuntimeConfiguration();
            ActionConfig actionConfig = runtimeConfiguration.getActionConfig(namespace, actionName);

            if (actionConfig != null) {
                List<InterceptorMapping> interceptors = actionConfig.getInterceptors();
                for (InterceptorMapping interceptorMapping : interceptors) {
                    if (ValidationInterceptor.class.isInstance(interceptorMapping.getInterceptor())) {
                        ValidationInterceptor validationInterceptor = (ValidationInterceptor) interceptorMapping.getInterceptor();

                        Set excludeMethods = validationInterceptor.getExcludeMethodsSet();
                        Set includeMethods = validationInterceptor.getIncludeMethodsSet();

                        if (MethodFilterInterceptorUtil.applyMethod(excludeMethods, includeMethods, actionMethod)) {
                            addParameter("performValidation", Boolean.TRUE);
                        }
                        return;
                    }
                }
            }
        }
    }

    public List getValidators(String name) {
        Class actionClass = (Class) getParameters().get("actionClass");
        if (actionClass == null) {
            return Collections.EMPTY_LIST;
        }

        String formActionValue = findString(action);
        ActionMapping mapping = actionMapper.getMappingFromActionName(formActionValue);
        String actionName = mapping.getName();

        String methodName = null;
        if (isValidateAnnotatedMethodOnly(actionName)) {
            methodName = mapping.getMethod();
        }
        
        List<Validator> actionValidators = actionValidatorManager.getValidators(actionClass, actionName, methodName);
        List<Validator> validators = new ArrayList<>();

        findFieldValidators(name, actionClass, actionName, actionValidators, validators, "");

        return validators;
    }

    private boolean isValidateAnnotatedMethodOnly(String actionName) {
        RuntimeConfiguration runtimeConfiguration = configuration.getRuntimeConfiguration();
        String actionNamespace = TagUtils.buildNamespace(actionMapper, stack, request);
        ActionConfig actionConfig = runtimeConfiguration.getActionConfig(actionNamespace, actionName);

        if (actionConfig != null) {
            List<InterceptorMapping> interceptors = actionConfig.getInterceptors();
            for (InterceptorMapping interceptorMapping : interceptors) {
                if (ValidationInterceptor.class.isInstance(interceptorMapping.getInterceptor())) {
                    ValidationInterceptor validationInterceptor = (ValidationInterceptor) interceptorMapping.getInterceptor();
                    return validationInterceptor.isValidateAnnotatedMethodOnly();
                }
            }
        }
        return false;
    }

    private void findFieldValidators(String name, Class actionClass, String actionName,
                                     List<Validator> validatorList, List<Validator> resultValidators, String prefix) {

        for (Validator validator : validatorList) {
            if (validator instanceof FieldValidator) {
                FieldValidator fieldValidator = (FieldValidator) validator;

                if (validator instanceof VisitorFieldValidator) {
                    VisitorFieldValidator vfValidator = (VisitorFieldValidator) fieldValidator;
                    Class clazz = getVisitorReturnType(actionClass, vfValidator.getFieldName());
                    if (clazz == null) {
                        continue;
                    }

                    List<Validator> visitorValidators = actionValidatorManager.getValidators(clazz, actionName);
                    String vPrefix = prefix + (vfValidator.isAppendPrefix() ? vfValidator.getFieldName() + "." : "");
                    findFieldValidators(name, clazz, actionName, visitorValidators, resultValidators, vPrefix);
                } else if ((prefix + fieldValidator.getFieldName()).equals(name)) {
                    if (StringUtils.isNotBlank(prefix)) {
                        
                        FieldVisitorValidatorWrapper wrap = new FieldVisitorValidatorWrapper(fieldValidator, prefix);
                        resultValidators.add(wrap);
                    } else {
                        resultValidators.add(fieldValidator);
                    }
                }
            }
        }
    }

    
    
    public static class FieldVisitorValidatorWrapper implements FieldValidator {
        private FieldValidator fieldValidator;
        private String namePrefix;
        public FieldVisitorValidatorWrapper(FieldValidator fv, String namePrefix) {
            this.fieldValidator = fv;
            this.namePrefix = namePrefix;
        }
        public String getValidatorType() {
            return "field-visitor";
        }
        public String getFieldName() {
            return namePrefix + fieldValidator.getFieldName();
        }
        public FieldValidator getFieldValidator() {
            return fieldValidator;
        }
        public void setFieldValidator(FieldValidator fieldValidator) {
            this.fieldValidator = fieldValidator;
        }
        public String getDefaultMessage() {
            return fieldValidator.getDefaultMessage();
        }
        public String getMessage(Object object) {
            return fieldValidator.getMessage(object);
        }
        public String getMessageKey() {
            return fieldValidator.getMessageKey();
        }
        public String[] getMessageParameters() {
            return fieldValidator.getMessageParameters();
        }
        public ValidatorContext getValidatorContext() {
            return fieldValidator.getValidatorContext();
        }
        public void setDefaultMessage(String message) {
            fieldValidator.setDefaultMessage(message);
        }
        public void setFieldName(String fieldName) {
            fieldValidator.setFieldName(fieldName);
        }
        public void setMessageKey(String key) {
            fieldValidator.setMessageKey(key);
        }
        public void setMessageParameters(String[] messageParameters) {
            fieldValidator.setMessageParameters(messageParameters);
        }
        public void setValidatorContext(ValidatorContext validatorContext) {
            fieldValidator.setValidatorContext(validatorContext);
        }
        public void setValidatorType(String type) {
            fieldValidator.setValidatorType(type);
        }
        public void setValueStack(ValueStack stack) {
            fieldValidator.setValueStack(stack);
        }
        public void validate(Object object) throws ValidationException {
            fieldValidator.validate(object);
        }
        public String getNamePrefix() {
            return namePrefix;
        }
        public void setNamePrefix(String namePrefix) {
            this.namePrefix = namePrefix;
        }
    }

    
    @SuppressWarnings("unchecked")
    protected Class getVisitorReturnType(Class actionClass, String visitorFieldName) {
        if (visitorFieldName == null) {
            return null;
        }
        String methodName = "get" + org.apache.commons.lang.StringUtils.capitalize(visitorFieldName);
        try {
            Method method = actionClass.getMethod(methodName, new Class[0]);
            return method.getReturnType();
        } catch (NoSuchMethodException e) {
            return null;
        }
    }


    
    protected int getSequence() {
        return sequence++;
    }

    @StrutsTagAttribute(description="HTML onsubmit attribute")
    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }

    @StrutsTagAttribute(description="HTML onreset attribute")
    public void setOnreset(String onreset) {
        this.onreset = onreset;
    }

    @StrutsTagAttribute(description="Set action name to submit to, without .action suffix", defaultValue="current action")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description="HTML form target attribute")
    public void setTarget(String target) {
        this.target = target;
    }

    @StrutsTagAttribute(description="HTML form enctype attribute")
    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    @StrutsTagAttribute(description="HTML form method attribute")
    public void setMethod(String method) {
        this.method = method;
    }

    @StrutsTagAttribute(description="Namespace for action to submit to", defaultValue="current namespace")
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @StrutsTagAttribute(description="Whether client side/remote validation should be performed. Only" +
                " useful with theme xhtml/ajax", type="Boolean", defaultValue="false")
    public void setValidate(String validate) {
        this.validate = validate;
    }

    @StrutsTagAttribute(description="The portlet mode to display after the form submit")
    public void setPortletMode(String portletMode) {
        this.portletMode = portletMode;
    }

    @StrutsTagAttribute(description="The window state to display after the form submit")
    public void setWindowState(String windowState) {
        this.windowState = windowState;
    }

    @StrutsTagAttribute(description="The accepted charsets for this form. The values may be comma or blank delimited.")
    public void setAcceptcharset(String acceptcharset) {
        this.acceptcharset = acceptcharset;
    }

    @StrutsTagAttribute(description="Id of element that will receive the focus when page loads.")
    public void setFocusElement(String focusElement) {
        this.focusElement = focusElement;
    }

    @StrutsTagAttribute(description="Whether actual context should be included in URL", type="Boolean", defaultValue="true")
    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }
}
