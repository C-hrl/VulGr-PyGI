
package org.apache.struts2.portlet.example.fileupload;

import java.io.File;

import org.apache.struts2.dispatcher.DefaultActionSupport;


public class FileUploadAction extends DefaultActionSupport {

    private static final long serialVersionUID = 5156288255337069381L;

    private String contentType;
    private File upload;
    private String fileName;
    private String caption;

    
    
    public String getUploadFileName() {
        return fileName;
    }
    public void setUploadFileName(String fileName) {
        this.fileName = fileName;
    }


    
    
    public String getUploadContentType() {
        return contentType;
    }
    public void setUploadContentType(String contentType) {
        this.contentType = contentType;
    }


    
    
    public File getUpload() {
        return upload;
    }
    public void setUpload(File upload) {
        this.upload = upload;
    }


    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String upload() throws Exception  {
        return SUCCESS;
    }

}
