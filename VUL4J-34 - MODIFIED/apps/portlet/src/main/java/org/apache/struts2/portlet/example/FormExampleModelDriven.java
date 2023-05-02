
package org.apache.struts2.portlet.example;

import org.apache.struts2.portlet.example.model.Name;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;


public class FormExampleModelDriven extends ActionSupport implements ModelDriven<Name> {
    
	private Name name = new Name();

	public Name getModel() {
		return name;
	}
}
