
package org.apache.struts2.showcase.actionchaining;

import com.opensymphony.xwork2.ActionSupport;

public class ActionChain1 extends ActionSupport {

	private static final long serialVersionUID = -6811701750042275153L;

	private String actionChain1Property1 = "Property Set In Action Chain 1";

	public String input() throws Exception {
		return SUCCESS;
	}

	public String getActionChain1Property1() {
		return actionChain1Property1;
	}

	public void setActionChain1Property1(String actionChain1Property1) {
		this.actionChain1Property1 = actionChain1Property1;
	}
}
