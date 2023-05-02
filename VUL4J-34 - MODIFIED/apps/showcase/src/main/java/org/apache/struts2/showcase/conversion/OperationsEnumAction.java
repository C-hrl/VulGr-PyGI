
package org.apache.struts2.showcase.conversion;

import com.opensymphony.xwork2.ActionSupport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class OperationsEnumAction extends ActionSupport {

	private static final long serialVersionUID = -2229489704988870318L;

	private List<OperationsEnum> selectedOperations = new LinkedList<OperationsEnum>();

	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		return SUCCESS;
	}

	public List<OperationsEnum> getSelectedOperations() {
		return this.selectedOperations;
	}

	public void setSelectedOperations(List<OperationsEnum> selectedOperations) {
		this.selectedOperations = selectedOperations;
	}


	public List<OperationsEnum> getAvailableOperations() {
		return Arrays.asList(OperationsEnum.values());
	}
}

