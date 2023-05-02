
package org.apache.struts2.showcase.source;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ClassLoaderUtil;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.util.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ViewSourceAction extends ActionSupport implements ServletContextAware {

	private String page;
	private String className;
	private String config;

	private List pageLines;
	private List classLines;
	private List configLines;

	private int configLine;
	private int padding = 10;

	private ServletContext servletContext;

	public String execute() throws MalformedURLException, IOException {

		if (page != null) {

			InputStream in = ClassLoaderUtil.getResourceAsStream(page.substring(page.indexOf("
			page = page.replace("

			if (in == null) {
				in = servletContext.getResourceAsStream(page);
				while (in == null && page.indexOf('/', 1) > 0) {
					page = page.substring(page.indexOf('/', 1));
					in = servletContext.getResourceAsStream(page);
				}
			}
			pageLines = read(in, -1);

			if (in != null) {
				in.close();
			}
		}

		if (className != null) {
			className = "/" + className.replace('.', '/') + ".java";
			InputStream in = getClass().getResourceAsStream(className);
			if (in == null) {
				in = servletContext.getResourceAsStream("/WEB-INF/src" + className);
			}
			classLines = read(in, -1);

			if (in != null) {
				in.close();
			}
		}

		String rootPath = ServletActionContext.getServletContext().getRealPath("/");

		if (config != null && (rootPath == null || config.startsWith(rootPath))) {
			int pos = config.lastIndexOf(':');
			configLine = Integer.parseInt(config.substring(pos + 1));
			config = config.substring(0, pos).replace("
			configLines = read(new URL(config).openStream(), configLine);
		}
		return SUCCESS;
	}


	
	public void setClassName(String className) {
		if (className != null && className.trim().length() > 0) {
			this.className = className;
		}
	}

	
	public void setConfig(String config) {
		if (config != null && config.trim().length() > 0) {
			this.config = config;
		}
	}

	
	public void setPage(String page) {
		if (page != null && page.trim().length() > 0) {
			this.page = page;
		}
	}

	
	public void setPadding(int padding) {
		this.padding = padding;
	}


	
	public List getClassLines() {
		return classLines;
	}

	
	public List getConfigLines() {
		return configLines;
	}

	
	public List getPageLines() {
		return pageLines;
	}

	
	public String getClassName() {
		return className;
	}

	
	public String getConfig() {
		return config;
	}

	
	public String getPage() {
		return page;
	}

	
	public int getConfigLine() {
		return configLine;
	}

	
	public int getPadding() {
		return padding;
	}

	
	private List read(InputStream in, int targetLineNumber) {
		List snippet = null;
		if (in != null) {
			snippet = new ArrayList();
			int startLine = 0;
			int endLine = Integer.MAX_VALUE;
			if (targetLineNumber > 0) {
				startLine = targetLineNumber - padding;
				endLine = targetLineNumber + padding;
			}
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				int lineno = 0;
				String line;
				while ((line = reader.readLine()) != null) {
					lineno++;
					if (lineno >= startLine && lineno <= endLine) {
						snippet.add(line);
					}
				}
			} catch (Exception ex) {
				
			}
		}
		return snippet;
	}

	public void setServletContext(ServletContext arg0) {
		this.servletContext = arg0;
	}


}
