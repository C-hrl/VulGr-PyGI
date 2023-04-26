
package org.apache.struts2.showcase;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.showcase.ajax.tree.Category;

public class ShowAjaxDynamicTreeAction extends ActionSupport {
	private int nodeId = 1;

	public Category getCategory() {
		return Category.getById(nodeId);
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
}
