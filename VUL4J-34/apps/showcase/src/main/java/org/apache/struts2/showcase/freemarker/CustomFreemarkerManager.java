
package org.apache.struts2.showcase.freemarker;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.ScopesHashModel;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class CustomFreemarkerManager extends FreemarkerManager {

	private CustomFreemarkerManagerUtil util;

	public CustomFreemarkerManager(CustomFreemarkerManagerUtil util) {
		this.util = util;
	}

	protected void populateContext(ScopesHashModel model, ValueStack stack, Object action, HttpServletRequest request, HttpServletResponse response) {
		super.populateContext(model, stack, action, request, response);
		model.put("customFreemarkerManagerUtil", util);
	}
}
