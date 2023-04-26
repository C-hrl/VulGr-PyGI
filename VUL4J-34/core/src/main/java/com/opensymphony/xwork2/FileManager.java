package com.opensymphony.xwork2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;


public interface FileManager {

    
    void setReloadingConfigs(boolean reloadingConfigs);

    
    boolean fileNeedsReloading(String fileName);

    
    boolean fileNeedsReloading(URL fileUrl);

    
    InputStream loadFile(URL fileUrl);

    
    void monitorFile(URL fileUrl);

    
    URL normalizeToFileProtocol(URL url);

    
    boolean support();

    
    boolean internal();

    Collection<? extends URL> getAllPhysicalUrls(URL url) throws IOException;

}
