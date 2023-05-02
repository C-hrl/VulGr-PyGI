

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;


@StrutsTag(name="updownselect", tldTagClass="org.apache.struts2.views.jsp.ui.UpDownSelectTag", 
        description="Create a Select component with buttons to move the elements in the select component up and down")
public class UpDownSelect extends Select {

    private static final Logger LOG = LogManager.getLogger(UpDownSelect.class);


    final public static String TEMPLATE = "updownselect";

    protected String allowMoveUp;
    protected String allowMoveDown;
    protected String allowSelectAll;

    protected String moveUpLabel;
    protected String moveDownLabel;
    protected String selectAllLabel;


    public String getDefaultTemplate() {
        return TEMPLATE;
    }

    public UpDownSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public void evaluateParams() {
        super.evaluateParams();


        
        if (StringUtils.isBlank(size)) {
            addParameter("size", "5");
        }
        if (StringUtils.isBlank(multiple)) {
            addParameter("multiple", Boolean.TRUE);
        }

        if (allowMoveUp != null) {
            addParameter("allowMoveUp", findValue(allowMoveUp, Boolean.class));
        }
        if (allowMoveDown != null) {
            addParameter("allowMoveDown", findValue(allowMoveDown, Boolean.class));
        }
        if (allowSelectAll != null) {
            addParameter("allowSelectAll", findValue(allowSelectAll, Boolean.class));
        }

        if (moveUpLabel != null) {
            addParameter("moveUpLabel", findString(moveUpLabel));
        }
        if (moveDownLabel != null) {
            addParameter("moveDownLabel", findString(moveDownLabel));
        }
        if (selectAllLabel != null) {
            addParameter("selectAllLabel", findString(selectAllLabel));
        }

        
        
        Form ancestorForm = (Form) findAncestor(Form.class);
        if (ancestorForm != null) {

            
            enableAncestorFormCustomOnsubmit();

            Map m = (Map) ancestorForm.getParameters().get("updownselectIds");
            if (m == null) {
                
                m = new LinkedHashMap();
            }
            m.put(getParameters().get("id"), getParameters().get("headerKey"));
            ancestorForm.getParameters().put("updownselectIds", m);
        }
        else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("no ancestor form found for updownselect "+this+", therefore autoselect of all elements upon form submission will not work ");
            }
        }
    }


    public String getAllowMoveUp() {
        return allowMoveUp;
    }

    @StrutsTagAttribute(description="Whether move up button should be displayed", type="Boolean", defaultValue="true")
    public void setAllowMoveUp(String allowMoveUp) {
        this.allowMoveUp = allowMoveUp;
    }



    public String getAllowMoveDown() {
        return allowMoveDown;
    }

    @StrutsTagAttribute(description="Whether move down button should be displayed", type="Boolean", defaultValue="true")
    public void setAllowMoveDown(String allowMoveDown) {
        this.allowMoveDown = allowMoveDown;
    }



    public String getAllowSelectAll() {
        return allowSelectAll;
    }

    @StrutsTagAttribute(description="Whether or not select all button should be displayed", type="Boolean", defaultValue="true")
    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }


    public String getMoveUpLabel() {
        return moveUpLabel;
    }

    @StrutsTagAttribute(description="Text to display on the move up button", defaultValue="^")
    public void setMoveUpLabel(String moveUpLabel) {
        this.moveUpLabel = moveUpLabel;
    }



    public String getMoveDownLabel() {
        return moveDownLabel;
    }

    @StrutsTagAttribute(description="Text to display on the move down button", defaultValue="v")
    public void setMoveDownLabel(String moveDownLabel) {
        this.moveDownLabel = moveDownLabel;
    }



    public String getSelectAllLabel() {
        return selectAllLabel;
    }

    @StrutsTagAttribute(description="Text to display on the select all button", defaultValue="*")
    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }
}
