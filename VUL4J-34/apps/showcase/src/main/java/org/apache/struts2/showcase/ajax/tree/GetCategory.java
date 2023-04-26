
package org.apache.struts2.showcase.ajax.tree;

import com.opensymphony.xwork2.ActionSupport;


public class GetCategory extends ActionSupport {
	private long catId;
	private Category category;

	public String execute() throws Exception {
		if (catId < 1) {
			
			catId = 1;
		}

		category = Category.getById(catId);

		return SUCCESS;
	}

	public void setCatId(long catId) {
		this.catId = catId;
	}

	public Category getCategory() {
		return category;
	}
}
