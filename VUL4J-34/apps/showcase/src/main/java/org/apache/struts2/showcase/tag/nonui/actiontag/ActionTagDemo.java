
package org.apache.struts2.showcase.tag.nonui.actiontag;

import com.opensymphony.xwork2.ActionSupport;


public class ActionTagDemo extends ActionSupport {

	private static final long serialVersionUID = -2749145880590245184L;

	public String show() throws Exception {
		return SUCCESS;
	}

	public String doInclude() throws Exception {
		return SUCCESS;
	}
}
