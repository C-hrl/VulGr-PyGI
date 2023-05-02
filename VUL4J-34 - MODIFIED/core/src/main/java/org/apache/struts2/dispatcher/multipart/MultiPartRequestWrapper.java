

package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;



public class MultiPartRequestWrapper extends StrutsRequestWrapper {

    protected static final Logger LOG = LogManager.getLogger(MultiPartRequestWrapper.class);

    private Collection<String> errors;
    private MultiPartRequest multi;
    private Locale defaultLocale = Locale.ENGLISH;

    
    public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request,
                                   String saveDir, LocaleProvider provider,
                                   boolean disableRequestAttributeValueStackLookup) {
        super(request, disableRequestAttributeValueStackLookup);
        errors = new ArrayList<>();
        multi = multiPartRequest;
        defaultLocale = provider.getLocale();
        setLocale(request);
        try {
            multi.parse(request, saveDir);
            for (String error : multi.getErrors()) {
                addError(error);
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
            addError(buildErrorMessage(e, new Object[] {e.getMessage()}));
        } 
    }

    public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request, String saveDir, LocaleProvider provider) {
        this(multiPartRequest, request, saveDir, provider, false);
    }

    protected void setLocale(HttpServletRequest request) {
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
    }

    protected String buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.messages.upload.error." + e.getClass().getSimpleName();
        LOG.debug("Preparing error message for key: [{}]", errorKey);
        return LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, e.getMessage(), args);
    }

    
    public Enumeration<String> getFileParameterNames() {
        if (multi == null) {
            return null;
        }

        return multi.getFileParameterNames();
    }

    
    public String[] getContentTypes(String name) {
        if (multi == null) {
            return null;
        }

        return multi.getContentType(name);
    }

    
    public File[] getFiles(String fieldName) {
        if (multi == null) {
            return null;
        }

        return multi.getFile(fieldName);
    }

    
    public String[] getFileNames(String fieldName) {
        if (multi == null) {
            return null;
        }

        return multi.getFileNames(fieldName);
    }

    
    public String[] getFileSystemNames(String fieldName) {
        if (multi == null) {
            return null;
        }

        return multi.getFilesystemName(fieldName);
    }

    
    public String getParameter(String name) {
        return ((multi == null) || (multi.getParameter(name) == null)) ? super.getParameter(name) : multi.getParameter(name);
    }

    
    public Map getParameterMap() {
        Map<String, String[]> map = new HashMap<>();
        Enumeration enumeration = getParameterNames();

        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            map.put(name, this.getParameterValues(name));
        }

        return map;
    }

    
    public Enumeration getParameterNames() {
        if (multi == null) {
            return super.getParameterNames();
        } else {
            return mergeParams(multi.getParameterNames(), super.getParameterNames());
        }
    }

    
    public String[] getParameterValues(String name) {
        return ((multi == null) || (multi.getParameterValues(name) == null)) ? super.getParameterValues(name) : multi.getParameterValues(name);
    }

    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    
    public Collection<String> getErrors() {
        return errors;
    }

    
    protected void addError(String anErrorMessage) {
        if (!errors.contains(anErrorMessage)) {
            errors.add(anErrorMessage);
        }
    }

    
    protected Enumeration mergeParams(Enumeration params1, Enumeration params2) {
        Vector temp = new Vector();

        while (params1.hasMoreElements()) {
            temp.add(params1.nextElement());
        }

        while (params2.hasMoreElements()) {
            temp.add(params2.nextElement());
        }

        return temp.elements();
    }

    public void cleanUp() {
        if (multi != null) {
            multi.cleanUp();
        }
    }

}
