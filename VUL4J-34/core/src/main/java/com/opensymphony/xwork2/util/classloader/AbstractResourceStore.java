
package com.opensymphony.xwork2.util.classloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public abstract class AbstractResourceStore implements ResourceStore {
    private static final Logger log = LogManager.getLogger(JarResourceStore.class);
    protected final File file;

    public AbstractResourceStore(final File file) {
        this.file = file;
    }

    protected void closeQuietly(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            log.error("Unable to close file input stream", e);
        }
    }

    public void write(String pResourceName, byte[] pResourceData) {
    }

    public String toString() {
        return this.getClass().getName() + file.toString();
    }
}
