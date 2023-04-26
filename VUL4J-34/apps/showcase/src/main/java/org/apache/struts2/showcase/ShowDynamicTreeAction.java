
package org.apache.struts2.showcase;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.showcase.ajax.tree.Category;



public class ShowDynamicTreeAction extends ActionSupport {

	public Category getTreeRootNode() {
		return Category.getById(1);
	}
}



