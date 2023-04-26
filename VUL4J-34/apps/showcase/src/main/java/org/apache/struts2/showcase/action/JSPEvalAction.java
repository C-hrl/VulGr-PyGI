
package org.apache.struts2.showcase.action;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.interceptor.annotations.After;
import org.apache.struts2.ServletActionContext;

import java.io.*;
import java.net.URL;


public class JSPEvalAction extends ExampleAction {
	private String jsp;
	private final static String FILE = "/interactive/demo.jsp";

	public String execute() throws IOException {
		if (jsp != null) {
			
			URL url = ServletActionContext.getServletContext().getResource(FILE);
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(url
					.getFile())));
			try {
				
				writer.write("<%@ taglib prefix=\"s\" uri=\"/struts-tags\" %>");
				writer.write(jsp);
			} finally {
				if (writer != null)
					writer.close();
			}
		}
		return Action.SUCCESS;
	}

	@After
	public void cleanUp() throws IOException {
		URL url = ServletActionContext.getServletContext().getResource(FILE);
		FileOutputStream out = new FileOutputStream(new File(url.getFile()));
		try {
			out.getChannel().truncate(0);
		} finally {
			if (out != null)
				out.close();
		}
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
	}

}
