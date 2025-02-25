
package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DefaultFileManager implements FileManager {

    private static Logger LOG = LogManager.getLogger(DefaultFileManager.class);

    private static final Pattern JAR_PATTERN = Pattern.compile("^(jar:|wsjar:|zip:|vfsfile:|code-source:)?(file:)?(.*?)(\\!/|\\.jar/)(.*)");
    private static final int JAR_FILE_PATH = 3;

    protected static Map<String, Revision> files = Collections.synchronizedMap(new HashMap<String, Revision>());

    protected boolean reloadingConfigs = false;

    public DefaultFileManager() {
    }

    public void setReloadingConfigs(boolean reloadingConfigs) {
        this.reloadingConfigs = reloadingConfigs;
    }

    public boolean fileNeedsReloading(URL fileUrl) {
        return fileUrl != null && fileNeedsReloading(fileUrl.toString());
    }

    public boolean fileNeedsReloading(String fileName) {
        Revision revision = files.get(fileName);
        if (revision == null) {
            
            
            return reloadingConfigs;
        }
        return revision.needsReloading();
    }

    public InputStream loadFile(URL fileUrl) {
        if (fileUrl == null) {
            return null;
        }
        InputStream is = openFile(fileUrl);
        monitorFile(fileUrl);
        return is;
    }

    private InputStream openFile(URL fileUrl) {
        try {
            InputStream is = fileUrl.openStream();
            if (is == null) {
                throw new IllegalArgumentException("No file '" + fileUrl + "' found as a resource");
            }
            return is;
        } catch (IOException e) {
            throw new IllegalArgumentException("No file '" + fileUrl + "' found as a resource");
        }
    }

    public void monitorFile(URL fileUrl) {
        String fileName = fileUrl.toString();
        Revision revision;
        LOG.debug("Creating revision for URL: {}", fileName);
        if (isJarURL(fileUrl)) {
            revision = JarEntryRevision.build(fileUrl, this);
        } else {
            revision = FileRevision.build(fileUrl);
        }
        if (revision == null) {
            files.put(fileName, Revision.build(fileUrl));
        } else {
            files.put(fileName, revision);
        }
    }

    
    protected boolean isJarURL(URL fileUrl) {
        Matcher jarMatcher = JAR_PATTERN.matcher(fileUrl.getPath());
        return jarMatcher.matches();
    }

    public URL normalizeToFileProtocol(URL url) {
        String fileName = url.toExternalForm();
        Matcher jarMatcher = JAR_PATTERN.matcher(fileName);
        try {
            if (jarMatcher.matches()) {
                String path = jarMatcher.group(JAR_FILE_PATH);
                return new URL("file", "", path);
            } else if ("file".equals(url.getProtocol())) {
                return url; 
            } else {
                LOG.warn("Could not normalize URL [{}] to file protocol!", url);
                return null;
            }
        } catch (MalformedURLException e) {
            LOG.warn("Error normalizing URL [{}] to file protocol!", url, e);
            return null;
        }
    }

    public boolean support() {
        return false; 
    }

    public boolean internal() {
        return true;
    }

    public Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException {
        return Arrays.asList(url);
    }

}
