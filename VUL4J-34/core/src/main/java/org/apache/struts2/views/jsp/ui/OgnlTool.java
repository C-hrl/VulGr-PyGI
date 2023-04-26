

package org.apache.struts2.views.jsp.ui;

import ognl.Ognl;
import ognl.OgnlException;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;



public class OgnlTool {

    private OgnlUtil ognlUtil;
    
    public OgnlTool() {
    }
    
    @Inject
    public void setOgnlUtil(OgnlUtil ognlUtil) {
        this.ognlUtil = ognlUtil;
    }
    
    


    public Object findValue(String expr, Object context) {
        try {
            return Ognl.getValue(ognlUtil.compile(expr), context);
        } catch (OgnlException e) {
            return null;
        }
    }
}
