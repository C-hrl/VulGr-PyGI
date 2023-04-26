
package org.apache.struts2.showcase.tag.nonui.iteratortag;

import com.opensymphony.xwork2.ActionSupport;


public class IteratorGeneratorTagDemo extends ActionSupport {

	private static final long serialVersionUID = 6893616642389337039L;

	private String value;
	private Integer count;
	private String separator;


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}


	public String getSeparator() {
		return this.separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}


	public String submit() throws Exception {
		return SUCCESS;
	}


	public String input() throws Exception {
		return SUCCESS;
	}
}
