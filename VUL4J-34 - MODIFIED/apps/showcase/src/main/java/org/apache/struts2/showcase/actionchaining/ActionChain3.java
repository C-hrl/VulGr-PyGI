
package org.apache.struts2.showcase.actionchaining;

import com.opensymphony.xwork2.ActionSupport;

public class ActionChain3 extends ActionSupport {

	private static final long serialVersionUID = -1456568865075250621L;

	private String actionChain1Property1;
	private String actionChain2Property1;
	private String actionChain3Property1 = "Property set in Action Chain 3";


	public String execute() throws Exception {
		return SUCCESS;
	}

	public String getActionChain1Property1() {
		return actionChain1Property1;
	}

	public void setActionChain1Property1(String actionChain1Property1) {
		this.actionChain1Property1 = actionChain1Property1;
	}


	public String getActionChain2Property1() {
		return actionChain2Property1;
	}

	public void setActionChain2Property1(String actionChain2Property1) {
		this.actionChain2Property1 = actionChain2Property1;
	}


	public String getActionChain3Property1() {
		return actionChain3Property1;
	}

	public void setActionChain3Property1(String actionChain3Property1) {
		this.actionChain3Property1 = actionChain3Property1;
	}
}
