

package com.opensymphony.xwork2.util.classloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class JarResourceStore extends AbstractResourceStore {
    private static final Logger LOG = LogManager.getLogger(JarResourceStore.class);

    public JarResourceStore(File file) {
        super(file);
    }

    public byte[] read(String pResourceName) {
        InputStream in = null;
        try {
            ZipFile jarFile = new ZipFile(file);
            ZipEntry entry = jarFile.getEntry(pResourceName);

            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            in = jarFile.getInputStream(entry);
            copy(in, out);

            return out.toByteArray();
        } catch (Exception e) {
            LOG.debug("Unable to read file [{}] from [{}]", pResourceName, file.getName(), e);
            return null;
        } finally {
            closeQuietly(in);
        }
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
