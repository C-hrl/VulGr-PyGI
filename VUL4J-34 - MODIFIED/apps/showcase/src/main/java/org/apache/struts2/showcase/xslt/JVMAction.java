
package org.apache.struts2.showcase.xslt;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Properties;

public class JVMAction implements ServletRequestAware {

	private ImportantInfo info;
	private Map<String, String> environment;

	
	private HttpServletRequest servletRequest;


	public String execute() {
		environment = System.getenv();
		Properties props = System.getProperties();

		String classpath = environment.get("CLASSPATH");
		info = new ImportantInfo(classpath, props);

		return ActionSupport.SUCCESS;
	}


	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public Map<String, String> getEnvironment() {
		return environment;
	}

	public void setEnvironment(Map<String, String> environment) {
		this.environment = environment;
	}


	public ImportantInfo getInfo() {
		return info;
	}

	public void setInfo(ImportantInfo info) {
		this.info = info;
	}

	public class ImportantInfo {
		private String classpath;
		private Properties systemProperties;


		public ImportantInfo(String classpath, Properties properties) {
			this.classpath = classpath;
			this.systemProperties = properties;
		}

		public String getClasspath() {
			return classpath;
		}

		public void setClasspath(String classpath) {
			this.classpath = classpath;
		}

		public Properties getSystemProperties() {
			return systemProperties;
		}

		public void setSystemProperties(Properties systemProperties) {
			this.systemProperties = systemProperties;
		}
	}
}
