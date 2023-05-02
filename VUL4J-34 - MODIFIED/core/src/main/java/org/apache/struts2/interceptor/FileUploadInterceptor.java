

package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.TextParseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.util.ContentTypeMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.NumberFormat;
import java.util.*;


public class FileUploadInterceptor extends AbstractInterceptor {

    private static final long serialVersionUID = -4764627478894962478L;

    protected static final Logger LOG = LogManager.getLogger(FileUploadInterceptor.class);

    protected Long maximumSize;
    protected Set<String> allowedTypesSet = Collections.emptySet();
    protected Set<String> allowedExtensionsSet = Collections.emptySet();

    private ContentTypeMatcher matcher;
    private Container container;

    @Inject
    public void setMatcher(ContentTypeMatcher matcher) {
        this.matcher = matcher;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    
    public void setAllowedExtensions(String allowedExtensions) {
        allowedExtensionsSet = TextParseUtil.commaDelimitedStringToSet(allowedExtensions);
    }

    
    public void setAllowedTypes(String allowedTypes) {
        allowedTypesSet = TextParseUtil.commaDelimitedStringToSet(allowedTypes);
    }

    
    public void setMaximumSize(Long maximumSize) {
        this.maximumSize = maximumSize;
    }

    

    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();

        HttpServletRequest request = (HttpServletRequest) ac.get(ServletActionContext.HTTP_REQUEST);

        if (!(request instanceof MultiPartRequestWrapper)) {
            if (LOG.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                LOG.debug(getTextMessage("struts.messages.bypass.request", new String[]{proxy.getNamespace(), proxy.getActionName()}));
            }

            return invocation.invoke();
        }

        ValidationAware validation = null;

        Object action = invocation.getAction();

        if (action instanceof ValidationAware) {
            validation = (ValidationAware) action;
        }

        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper) request;

        if (multiWrapper.hasErrors()) {
            for (String error : multiWrapper.getErrors()) {
                if (validation != null) {
                    validation.addActionError(error);
                }
            }
        }

        
        Enumeration fileParameterNames = multiWrapper.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            
            String inputName = (String) fileParameterNames.nextElement();

            
            String[] contentType = multiWrapper.getContentTypes(inputName);

            if (isNonEmpty(contentType)) {
                
                String[] fileName = multiWrapper.getFileNames(inputName);

                if (isNonEmpty(fileName)) {
                    
                    File[] files = multiWrapper.getFiles(inputName);
                    if (files != null && files.length > 0) {
                        List<File> acceptedFiles = new ArrayList<>(files.length);
                        List<String> acceptedContentTypes = new ArrayList<>(files.length);
                        List<String> acceptedFileNames = new ArrayList<>(files.length);
                        String contentTypeName = inputName + "ContentType";
                        String fileNameName = inputName + "FileName";

                        for (int index = 0; index < files.length; index++) {
                            if (acceptFile(action, files[index], fileName[index], contentType[index], inputName, validation)) {
                                acceptedFiles.add(files[index]);
                                acceptedContentTypes.add(contentType[index]);
                                acceptedFileNames.add(fileName[index]);
                            }
                        }

                        if (!acceptedFiles.isEmpty()) {
                            Map<String, Object> params = ac.getParameters();

                            params.put(inputName, acceptedFiles.toArray(new File[acceptedFiles.size()]));
                            params.put(contentTypeName, acceptedContentTypes.toArray(new String[acceptedContentTypes.size()]));
                            params.put(fileNameName, acceptedFileNames.toArray(new String[acceptedFileNames.size()]));
                        }
                    }
                } else {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn(getTextMessage(action, "struts.messages.invalid.file", new String[]{inputName}));
                    }
                }
            } else {
                if (LOG.isWarnEnabled()) {
                    LOG.warn(getTextMessage(action, "struts.messages.invalid.content.type", new String[]{inputName}));
                }
            }
        }

        
        return invocation.invoke();
    }

    
    protected boolean acceptFile(Object action, File file, String filename, String contentType, String inputName, ValidationAware validation) {
        boolean fileIsAcceptable = false;

        
        if (file == null) {
            String errMsg = getTextMessage(action, "struts.messages.error.uploading", new String[]{inputName});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else if (maximumSize != null && maximumSize < file.length()) {
            String errMsg = getTextMessage(action, "struts.messages.error.file.too.large", new String[]{inputName, filename, file.getName(), "" + file.length(), getMaximumSizeStr(action)});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else if ((!allowedTypesSet.isEmpty()) && (!containsItem(allowedTypesSet, contentType))) {
            String errMsg = getTextMessage(action, "struts.messages.error.content.type.not.allowed", new String[]{inputName, filename, file.getName(), contentType});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else if ((!allowedExtensionsSet.isEmpty()) && (!hasAllowedExtension(allowedExtensionsSet, filename))) {
            String errMsg = getTextMessage(action, "struts.messages.error.file.extension.not.allowed", new String[]{inputName, filename, file.getName(), contentType});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg);
            }

            if (LOG.isWarnEnabled()) {
                LOG.warn(errMsg);
            }
        } else {
            fileIsAcceptable = true;
        }

        return fileIsAcceptable;
    }

    private String getMaximumSizeStr(Object action) {
        return NumberFormat.getNumberInstance(getLocaleProvider(action).getLocale()).format(maximumSize);
    }

    
    private boolean hasAllowedExtension(Collection<String> extensionCollection, String filename) {
        if (filename == null) {
            return false;
        }

        String lowercaseFilename = filename.toLowerCase();
        for (String extension : extensionCollection) {
            if (lowercaseFilename.endsWith(extension)) {
                return true;
            }
        }

        return false;
    }

    
    private boolean containsItem(Collection<String> itemCollection, String item) {
        for (String pattern : itemCollection)
            if (matchesWildcard(pattern, item))
                return true;
        return false;
    }

    private boolean matchesWildcard(String pattern, String text) {
        Object o = matcher.compilePattern(pattern);
        return matcher.match(new HashMap<String, String>(), text, o);
    }

    private boolean isNonEmpty(Object[] objArray) {
        boolean result = false;
        for (int index = 0; index < objArray.length && !result; index++) {
            if (objArray[index] != null) {
                result = true;
            }
        }
        return result;
    }

    protected String getTextMessage(String messageKey, String[] args) {
        return getTextMessage(this, messageKey, args);
    }

    protected String getTextMessage(Object action, String messageKey, String[] args) {
        if (action instanceof TextProvider) {
            return ((TextProvider) action).getText(messageKey, args);
        }
        return getTextProvider(action).getText(messageKey, args);
    }

    private TextProvider getTextProvider(Object action) {
        TextProviderFactory tpf = new TextProviderFactory();
        if (container != null) {
            container.inject(tpf);
        }
        LocaleProvider localeProvider = getLocaleProvider(action);
        return tpf.createInstance(action.getClass(), localeProvider);
    }

    private LocaleProvider getLocaleProvider(Object action) {
        LocaleProvider localeProvider;
        if (action instanceof LocaleProvider) {
            localeProvider = (LocaleProvider) action;
        } else {
            localeProvider = container.getInstance(LocaleProvider.class);
        }
        return localeProvider;
    }

}
