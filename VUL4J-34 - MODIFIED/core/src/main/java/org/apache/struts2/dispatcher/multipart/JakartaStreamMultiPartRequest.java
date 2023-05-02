package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;


public class JakartaStreamMultiPartRequest implements MultiPartRequest {

    static final Logger LOG = LogManager.getLogger(JakartaStreamMultiPartRequest.class);

    
    private static final int BUFFER_SIZE = 10240;

    
    private Map<String, List<FileInfo>> fileInfos = new HashMap<>();

    
    private Map<String, List<String>> parameters = new HashMap<>();

    
    private List<String> errors = new ArrayList<>();

    
    private List<String> messages = new ArrayList<>();

    
    private Long maxSize;

    
    private int bufferSize = BUFFER_SIZE;

    
    private Locale defaultLocale = Locale.ENGLISH;

    
    @Inject(StrutsConstants.STRUTS_MULTIPART_MAXSIZE)
    public void setMaxSize(String maxSize) {
        this.maxSize = Long.parseLong(maxSize);
    }

    
    @Inject(value = StrutsConstants.STRUTS_MULTIPART_BUFFERSIZE, required = false)
    public void setBufferSize(String bufferSize) {
        this.bufferSize = Integer.parseInt(bufferSize);
    }

    
    @Inject
    public void setLocaleProvider(LocaleProvider provider) {
        defaultLocale = provider.getLocale();
    }

    
    public void cleanUp() {
        LOG.debug("Performing File Upload temporary storage cleanup.");
        for (String fieldName : fileInfos.keySet()) {
            for (FileInfo fileInfo : fileInfos.get(fieldName)) {
                File file = fileInfo.getFile();
                LOG.debug("Deleting file '{}'.", file.getName());
                if (!file.delete()) {
                    LOG.warn("There was a problem attempting to delete file '{}'.", file.getName());
                }
            }
        }
    }

    
    public String[] getContentType(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<String> types = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            types.add(fileInfo.getContentType());
        }

        return types.toArray(new String[types.size()]);
    }

    
    public List<String> getErrors() {
        return errors;
    }

    
    public List<String> getMesssages() {
        return messages;
    }

    
    public File[] getFile(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<File> files = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            files.add(fileInfo.getFile());
        }

        return files.toArray(new File[files.size()]);
    }

    
    public String[] getFileNames(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<String> names = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            names.add(getCanonicalName(fileInfo.getOriginalName()));
        }

        return names.toArray(new String[names.size()]);
    }

    
    public Enumeration<String> getFileParameterNames() {
        return Collections.enumeration(fileInfos.keySet());
    }

    
    public String[] getFilesystemName(String fieldName) {
        List<FileInfo> infos = fileInfos.get(fieldName);
        if (infos == null) {
            return null;
        }

        List<String> names = new ArrayList<>(infos.size());
        for (FileInfo fileInfo : infos) {
            names.add(fileInfo.getFile().getName());
        }

        return names.toArray(new String[names.size()]);
    }

    
    public String getParameter(String name) {
        List<String> values = parameters.get(name);
        if (values != null && values.size() > 0) {
            return values.get(0);
        }
        return null;
    }

    
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(parameters.keySet());
    }

    
    public String[] getParameterValues(String name) {
        List<String> values = parameters.get(name);
        if (values != null && values.size() > 0) {
            return values.toArray(new String[values.size()]);
        }
        return null;
    }

    
    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        try {
            setLocale(request);
            processUpload(request, saveDir);
        } catch (Exception e) {
            LOG.warn("Error occurred during parsing of multi part request", e);
            String errorMessage = buildErrorMessage(e, new Object[]{});
            if (!errors.contains(errorMessage)) {
                errors.add(errorMessage);
            }
        }
    }

    
    protected void setLocale(HttpServletRequest request) {
        if (defaultLocale == null) {
            defaultLocale = request.getLocale();
        }
    }

    
    private void processUpload(HttpServletRequest request, String saveDir) throws Exception {

        
        if (ServletFileUpload.isMultipartContent(request)) {

            
            boolean requestSizePermitted = isRequestSizePermitted(request);

            
            
            ServletFileUpload servletFileUpload = new ServletFileUpload();
            FileItemIterator i = servletFileUpload.getItemIterator(request);

            
            while (i.hasNext()) {
                try {
                    FileItemStream itemStream = i.next();

                    
                    
                    if (itemStream.isFormField()) {
                        processFileItemStreamAsFormField(itemStream);
                    }

                    
                    
                    
                    
                    else {

                        
                        
                        if (!requestSizePermitted) {
                            addFileSkippedError(itemStream.getName(), request);
                            LOG.warn("Skipped stream '{}', request maximum size ({}) exceeded.", itemStream.getName(), maxSize);
                            continue;
                        }

                        processFileItemStreamAsFileField(itemStream, saveDir);
                    }
                } catch (IOException e) {
                    LOG.warn("Error occurred during process upload", e);
                }
            }
        }
    }

    
    private boolean isRequestSizePermitted(HttpServletRequest request) {
        
        
        
        if (maxSize == -1 || request == null) {
            return true;
        }

        return request.getContentLength() < maxSize;
    }

    
    private long getRequestSize(HttpServletRequest request) {
        long requestSize = 0;
        if (request != null) {
            requestSize = request.getContentLength();
        }

        return requestSize;
    }

    
    private void addFileSkippedError(String fileName, HttpServletRequest request) {
        String exceptionMessage = "Skipped file " + fileName + "; request size limit exceeded.";
        FileSizeLimitExceededException exception = new FileUploadBase.FileSizeLimitExceededException(exceptionMessage, getRequestSize(request), maxSize);
        String message = buildErrorMessage(exception, new Object[]{fileName, getRequestSize(request), maxSize});
        if (!errors.contains(message)) {
            errors.add(message);
        }
    }

    
    private void processFileItemStreamAsFormField(FileItemStream itemStream) {
        String fieldName = itemStream.getFieldName();
        try {
            List<String> values;
            String fieldValue = Streams.asString(itemStream.openStream());
            if (!parameters.containsKey(fieldName)) {
                values = new ArrayList<>();
                parameters.put(fieldName, values);
            } else {
                values = parameters.get(fieldName);
            }
            values.add(fieldValue);
        } catch (IOException e) {
            LOG.warn("Failed to handle form field '{}'.", fieldName, e);
        }
    }

    
    private void processFileItemStreamAsFileField(FileItemStream itemStream, String location) {
        
        if (itemStream.getName() == null || itemStream.getName().trim().length() < 1) {
            LOG.debug("No file has been uploaded for the field: {}", itemStream.getFieldName());
            return;
        }

        File file = null;
        try {
            
            file = createTemporaryFile(itemStream.getName(), location);

            if (streamFileToDisk(itemStream, file)) {
                createFileInfoFromItemStream(itemStream, file);
            }
        } catch (IOException e) {
            if (file != null) {
                try {
                    file.delete();
                } catch (SecurityException se) {
                    LOG.warn("Failed to delete '{}' due to security exception above.", file.getName(), se);
                }
            }
        }
    }

    
    private File createTemporaryFile(String fileName, String location) throws IOException {
        String name = fileName
                .substring(fileName.lastIndexOf('/') + 1)
                .substring(fileName.lastIndexOf('\\') + 1);

        String prefix = name;
        String suffix = "";

        if (name.contains(".")) {
            prefix = name.substring(0, name.lastIndexOf('.'));
            suffix = name.substring(name.lastIndexOf('.'));
        }

        if (prefix.length() < 3) {
            prefix = UUID.randomUUID().toString();
        }

        File file = File.createTempFile(prefix + "_", suffix, new File(location));
        LOG.debug("Creating temporary file '{}' (originally '{}').", file.getName(), fileName);
        return file;
    }

    
    private boolean streamFileToDisk(FileItemStream itemStream, File file) throws IOException {
        boolean result = false;
        try (InputStream input = itemStream.openStream();
                OutputStream output = new BufferedOutputStream(new FileOutputStream(file), bufferSize)) {
            byte[] buffer = new byte[bufferSize];
            LOG.debug("Streaming file using buffer size {}.", bufferSize);
            for (int length = 0; ((length = input.read(buffer)) > 0); ) {
                output.write(buffer, 0, length);
            }
            result = true;
        }
        return result;
    }

    
    private void createFileInfoFromItemStream(FileItemStream itemStream, File file) {
        
        String fileName = itemStream.getName();
        String fieldName = itemStream.getFieldName();
        
        FileInfo fileInfo = new FileInfo(file, itemStream.getContentType(), fileName);
        
        if (!fileInfos.containsKey(fieldName)) {
            List<FileInfo> infos = new ArrayList<>();
            infos.add(fileInfo);
            fileInfos.put(fieldName, infos);
        } else {
            fileInfos.get(fieldName).add(fileInfo);
        }
    }

    
    private String getCanonicalName(String fileName) {
        int forwardSlash = fileName.lastIndexOf("/");
        int backwardSlash = fileName.lastIndexOf("\\");
        if (forwardSlash != -1 && forwardSlash > backwardSlash) {
            fileName = fileName.substring(forwardSlash + 1, fileName.length());
        } else {
            fileName = fileName.substring(backwardSlash + 1, fileName.length());
        }
        return fileName;
    }

    
    private String buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.message.upload.error." + e.getClass().getSimpleName();
        LOG.debug("Preparing error message for key: [{}]", errorKey);
        return LocalizedTextUtil.findText(this.getClass(), errorKey, defaultLocale, e.getMessage(), args);
    }

    
    private String buildMessage(Throwable e, Object[] args) {
        String messageKey = "struts.message.upload.message." + e.getClass().getSimpleName();
        LOG.debug("Preparing message for key: [{}]", messageKey);
        return LocalizedTextUtil.findText(this.getClass(), messageKey, defaultLocale, e.getMessage(), args);
    }

    
    private static class FileInfo implements Serializable {

        private static final long serialVersionUID = 1083158552766906037L;

        private File file;
        private String contentType;
        private String originalName;

        
        public FileInfo(File file, String contentType, String originalName) {
            this.file = file;
            this.contentType = contentType;
            this.originalName = originalName;
        }

        
        public File getFile() {
            return file;
        }

        
        public String getContentType() {
            return contentType;
        }

        
        public String getOriginalName() {
            return originalName;
        }
    }

}
