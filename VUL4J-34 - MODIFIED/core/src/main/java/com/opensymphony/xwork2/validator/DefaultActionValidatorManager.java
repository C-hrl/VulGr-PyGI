
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;



public class DefaultActionValidatorManager implements ActionValidatorManager {

    
    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";

    private final Map<String, List<ValidatorConfig>> validatorCache = Collections.synchronizedMap(new HashMap<String, List<ValidatorConfig>>());
    private final Map<String, List<ValidatorConfig>> validatorFileCache = Collections.synchronizedMap(new HashMap<String, List<ValidatorConfig>>());
    private final Logger LOG = LogManager.getLogger(DefaultActionValidatorManager.class);
    private ValidatorFactory validatorFactory;
    private ValidatorFileParser validatorFileParser;
    private FileManager fileManager;
    private boolean reloadingConfigs;

    @Inject
    public void setValidatorFileParser(ValidatorFileParser parser) {
        this.validatorFileParser = parser;
    }
    
    @Inject
    public void setValidatorFactory(ValidatorFactory fac) {
        this.validatorFactory = fac;
    }

    @Inject
    public void setFileManagerFactory(FileManagerFactory fileManagerFactory) {
        this.fileManager = fileManagerFactory.getFileManager();
    }

    @Inject(value = XWorkConstants.RELOAD_XML_CONFIGURATION, required = false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    public synchronized List<Validator> getValidators(Class clazz, String context) {
        return getValidators(clazz, context, null);
    }

    public synchronized List<Validator> getValidators(Class clazz, String context, String method) {
        final String validatorKey = buildValidatorKey(clazz, context);

        if (validatorCache.containsKey(validatorKey)) {
            if (reloadingConfigs) {
                validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        }
        ValueStack stack = ActionContext.getContext().getValueStack();

        
        List<ValidatorConfig> cfgs = validatorCache.get(validatorKey);

        
        ArrayList<Validator> validators = new ArrayList<>(cfgs.size());
        for (ValidatorConfig cfg : cfgs) {
            if (method == null || method.equals(cfg.getParams().get("methodName"))) {
                Validator validator = validatorFactory.getValidator(cfg);
                validator.setValidatorType(cfg.getType());
                validator.setValueStack(stack);
                validators.add(validator);
            }
        }
        return validators;
    }

    public void validate(Object object, String context) throws ValidationException {
        validate(object, context, (String) null);
    }

    public void validate(Object object, String context, String method) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object);
        validate(object, context, validatorContext, method);
    }

    public void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        validate(object, context, validatorContext, null);
    }

    public void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException {
        List<Validator> validators = getValidators(object.getClass(), context, method);
        Set<String> shortcircuitedFields = null;

        for (final Validator validator : validators) {
            try {
                validator.setValidatorContext(validatorContext);

                LOG.debug("Running validator: {} for object {} and method {}", validator, object, method);

                FieldValidator fValidator = null;
                String fullFieldName = null;

                if (validator instanceof FieldValidator) {
                    fValidator = (FieldValidator) validator;
                    fullFieldName = fValidator.getValidatorContext().getFullFieldName(fValidator.getFieldName());

                    if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fullFieldName)) {
                        LOG.debug("Short-circuited, skipping");
                        continue;
                    }
                }

                if (validator instanceof ShortCircuitableValidator && ((ShortCircuitableValidator) validator).isShortCircuit()) {
                    
                    List<String> errs = null;

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection<String> fieldErrors = validatorContext.getFieldErrors().get(fullFieldName);

                            if (fieldErrors != null) {
                                errs = new ArrayList<>(fieldErrors);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> actionErrors = validatorContext.getActionErrors();

                        if (actionErrors != null) {
                            errs = new ArrayList<String>(actionErrors);
                        }
                    }

                    validator.validate(object);

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection<String> errCol = validatorContext.getFieldErrors().get(fullFieldName);

                            if ((errCol != null) && !errCol.equals(errs)) {
                                LOG.debug("Short-circuiting on field validation");

                                if (shortcircuitedFields == null) {
                                    shortcircuitedFields = new TreeSet<>();
                                }

                                shortcircuitedFields.add(fullFieldName);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> errCol = validatorContext.getActionErrors();

                        if ((errCol != null) && !errCol.equals(errs)) {
                            LOG.debug("Short-circuiting");
                            break;
                        }
                    }
                    continue;
                }

                validator.validate(object);
            }
            finally {
                validator.setValidatorContext(null);
            }
        }
    }

    
    protected static String buildValidatorKey(Class clazz, String context) {
        StringBuilder sb = new StringBuilder(clazz.getName());
        sb.append("/");
        sb.append(context);
        return sb.toString();
    }

    private List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    private List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }

    
    private List<ValidatorConfig> buildValidatorConfigs(Class clazz, String context, boolean checkFile, Set<String> checked) {
        List<ValidatorConfig> validatorConfigs = new ArrayList<>();

        if (checked == null) {
            checked = new TreeSet<String>();
        } else if (checked.contains(clazz.getName())) {
            return validatorConfigs;
        }

        if (clazz.isInterface()) {
            for (Class anInterface : clazz.getInterfaces()) {
                validatorConfigs.addAll(buildValidatorConfigs(anInterface, context, checkFile, checked));
             }
        } else {
            if (!clazz.equals(Object.class)) {
                validatorConfigs.addAll(buildValidatorConfigs(clazz.getSuperclass(), context, checkFile, checked));
            }
        }

        
        for (Class anInterface1 : clazz.getInterfaces()) {
            if (checked.contains(anInterface1.getName())) {
                continue;
            }

            validatorConfigs.addAll(buildClassValidatorConfigs(anInterface1, checkFile));

            if (context != null) {
                validatorConfigs.addAll(buildAliasValidatorConfigs(anInterface1, context, checkFile));
            }

            checked.add(anInterface1.getName());
        }

        validatorConfigs.addAll(buildClassValidatorConfigs(clazz, checkFile));

        if (context != null) {
            validatorConfigs.addAll(buildAliasValidatorConfigs(clazz, context, checkFile));
        }

        checked.add(clazz.getName());

        return validatorConfigs;
    }

    private List<ValidatorConfig> loadFile(String fileName, Class clazz, boolean checkFile) {
        List<ValidatorConfig> retList = Collections.emptyList();
        URL fileUrl = ClassLoaderUtil.getResource(fileName, clazz);
        if ((checkFile && fileManager.fileNeedsReloading(fileUrl)) || !validatorFileCache.containsKey(fileName)) {
            try (InputStream is = fileManager.loadFile(fileUrl)) {
                if (is != null) {
                    retList = new ArrayList<>(validatorFileParser.parseActionValidatorConfigs(validatorFactory, is, fileName));
                }
            } catch (IOException e) {
                LOG.error("Caught exception while loading file {}", fileName, e);
            }

            validatorFileCache.put(fileName, retList);
        } else {
            retList = validatorFileCache.get(fileName);
        }

        return retList;
    }
}
