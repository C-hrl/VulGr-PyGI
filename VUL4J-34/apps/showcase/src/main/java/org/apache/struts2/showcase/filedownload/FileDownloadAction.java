
package org.apache.struts2.showcase.filedownload;

import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;

import java.io.InputStream;


public class FileDownloadAction implements Action {

	private String inputPath;

	public String execute() throws Exception {
		return SUCCESS;
	}

	public void setInputPath(String value) {
		inputPath = sanitizeInputPath(value);
	}

	
	String sanitizeInputPath( String value ) {
		if (value != null && value.toUpperCase().contains("WEB-INF")) {
			return null;
		}
		return value;
	}

	public InputStream getInputStream() throws Exception {
		return ServletActionContext.getServletContext().getResourceAsStream(inputPath);
	}
}
