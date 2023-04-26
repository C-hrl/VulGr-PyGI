
package org.apache.struts2.showcase.tag.nonui.iteratortag;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Validateable;



public class MergeIteratorTagDemo extends ActionSupport implements Validateable {

	private static final long serialVersionUID = 4401107963952961695L;

	private String iteratorValue1;
	private String iteratorValue2;


	public void validate() {
		if (iteratorValue1 == null || iteratorValue1.trim().length() <= 0) {
			addFieldError("iteratorValue1", "iterator value 1 cannot be empty");
		} else if (iteratorValue1.trim().indexOf(",") <= 0) {
			addFieldError("iteratorValue1", "iterator value 1 needs to be comma separated");
		}
		if (iteratorValue2 == null || iteratorValue2.trim().length() <= 0) {
			addFieldError("iteratorValue2", "iterator value 2 cannot be empty");
		} else if (iteratorValue2.trim().indexOf(",") <= 0) {
			addFieldError("iteratorValue2", "iterator value 2 needs to be comma separated");
		}
	}


	public String getIteratorValue1() {
		return this.iteratorValue1;
	}

	public void setIteratorValue1(String iteratorValue1) {
		this.iteratorValue1 = iteratorValue1;
	}


	public String getIteratorValue2() {
		return this.iteratorValue2;
	}

	public void setIteratorValue2(String iteratorValue2) {
		this.iteratorValue2 = iteratorValue2;
	}


	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		return SUCCESS;
	}
}
