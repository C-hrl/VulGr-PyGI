package com.opensymphony.xwork2;


public interface FileManagerFactory {

    void setReloadingConfigs(String reloadingConfigs);

    FileManager getFileManager();

}
