
package org.apache.struts2.showcase.wait;

import com.opensymphony.xwork2.ActionSupport;


public class LongProcessAction extends ActionSupport {

	private static final long serialVersionUID = 2471910747833998708L;

	private int time;

	public String execute() throws Exception {
		Thread.sleep(time);

		return SUCCESS;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}
}
