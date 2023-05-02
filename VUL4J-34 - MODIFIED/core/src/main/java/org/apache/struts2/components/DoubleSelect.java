

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.views.annotations.StrutsTag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@StrutsTag(name="doubleselect", tldTagClass="org.apache.struts2.views.jsp.ui.DoubleSelectTag", description="Renders two HTML select elements with second one changing displayed values depending on " +
                "selected entry of first one.")
public class DoubleSelect extends DoubleListUIBean {
    final public static String TEMPLATE = "doubleselect";


    public DoubleSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        StringBuilder onchangeParam = new StringBuilder();
        onchangeParam.append(getParameters().get("id")).append("Redirect(this.selectedIndex)");
        if(StringUtils.isNotEmpty(this.onchange)) {
        	onchangeParam.append(";").append(this.onchange);
        }
        
        addParameter("onchange", onchangeParam.toString());
    }
}
