
package com.opensymphony.xwork2.util.classloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;



public final class FileResourceStore extends AbstractResourceStore {
    private static final Logger LOG = LogManager.getLogger(FileResourceStore.class);

    public FileResourceStore(final File file) {
        super(file);
    }

    public byte[] read(final String pResourceName) {
        FileInputStream fis = null;
        try {
            File file = getFile(pResourceName);
            byte[] data = new byte[(int) file.length()];
            fis = new FileInputStream(file);
            fis.read(data);

            return data;
        } catch (Exception e) {
            LOG.debug("Unable to read file [{}]", pResourceName, e);
            return null;
        } finally {
            closeQuietly(fis);
        }
    }

    private File getFile(final String pResourceName) {
        final String fileName = pResourceName.replace('/', File.separatorChar);
        return new File(file, fileName);
    }
}
