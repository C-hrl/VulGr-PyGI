
package org.apache.struts2.showcase.modelDriven;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;


public class ModelDrivenAction extends ActionSupport implements ModelDriven {

	private static final long serialVersionUID = 1271130427666936592L;

	public String input() throws Exception {
		return SUCCESS;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}

	public Object getModel() {
		return new Gangster();
	}
}
