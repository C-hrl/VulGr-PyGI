

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


@StrutsTag(name="optiontransferselect", tldTagClass="org.apache.struts2.views.jsp.ui.OptionTransferSelectTag", description="Renders an input form")
public class OptionTransferSelect extends DoubleListUIBean {

    private static final Logger LOG = LogManager.getLogger(OptionTransferSelect.class);

    private static final String TEMPLATE = "optiontransferselect";

    protected String allowAddToLeft;
    protected String allowAddToRight;
    protected String allowAddAllToLeft;
    protected String allowAddAllToRight;
    protected String allowSelectAll;
    protected String allowUpDownOnLeft;
    protected String allowUpDownOnRight;

    protected String leftTitle;
    protected String rightTitle;

    protected String buttonCssClass;
    protected String buttonCssStyle;

    protected String addToLeftLabel;
    protected String addToRightLabel;
    protected String addAllToLeftLabel;
    protected String addAllToRightLabel;
    protected String selectAllLabel;
    protected String leftUpLabel;
    protected String leftDownlabel;
    protected String rightUpLabel;
    protected String rightDownLabel;

    protected String addToLeftOnclick;
    protected String addToRightOnclick;
    protected String addAllToLeftOnclick;
    protected String addAllToRightOnclick;
    protected String selectAllOnclick;
    protected String upDownOnLeftOnclick;
    protected String upDownOnRightOnclick;


    public OptionTransferSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }


    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        Object doubleValue = null;

        
        if (doubleList != null) {
            doubleValue = findValue(doubleList);
            addParameter("doubleList", doubleValue);
        }
        if (StringUtils.isBlank(size)) {
            addParameter("size", "15");
        }
        if (StringUtils.isBlank(doubleSize)) {
            addParameter("doubleSize", "15");
        }
        if (StringUtils.isBlank(multiple)) {
            addParameter("multiple", Boolean.TRUE);
        }
        if (StringUtils.isBlank(doubleMultiple)) {
            addParameter("doubleMultiple", Boolean.TRUE);
        }

        
        if (StringUtils.isNotBlank(buttonCssClass)) {
            addParameter("buttonCssClass", buttonCssClass);
        }

        
        if (StringUtils.isNotBlank(buttonCssStyle)) {
            addParameter("buttonCssStyle", buttonCssStyle);
        }

        
        addParameter("allowSelectAll",
                allowSelectAll != null ? findValue(allowSelectAll, Boolean.class) : Boolean.TRUE);

        
        addParameter("allowAddToLeft",
                allowAddToLeft != null ? findValue(allowAddToLeft, Boolean.class) : Boolean.TRUE);

        
        addParameter("allowAddToRight",
                allowAddToRight != null ? findValue(allowAddToRight, Boolean.class) : Boolean.TRUE);

        
        addParameter("allowAddAllToLeft",
                allowAddAllToLeft != null ? findValue(allowAddAllToLeft, Boolean.class) : Boolean.TRUE);

        
        addParameter("allowAddAllToRight",
                allowAddAllToRight != null ? findValue(allowAddAllToRight, Boolean.class) : Boolean.TRUE);

        
        addParameter("allowUpDownOnLeft",
                allowUpDownOnLeft != null ? findValue(allowUpDownOnLeft, Boolean.class) : Boolean.TRUE);

        
        addParameter("allowUpDownOnRight",
                allowUpDownOnRight != null ? findValue(allowUpDownOnRight, Boolean.class) : Boolean.TRUE);


        
        if (leftTitle != null) {
            addParameter("leftTitle", findValue(leftTitle, String.class));
        }

        
        if (rightTitle != null) {
            addParameter("rightTitle", findValue(rightTitle, String.class));
        }


        
        addParameter("addToLeftLabel",
                addToLeftLabel != null ? findValue(addToLeftLabel, String.class) : "<-" );

        
        addParameter("addToRightLabel",
                addToRightLabel != null ? findValue(addToRightLabel, String.class) : "->");

        
        addParameter("addAllToLeftLabel",
                addAllToLeftLabel != null ? findValue(addAllToLeftLabel, String.class) : "<<--");

        
        addParameter("addAllToRightLabel",
                addAllToRightLabel != null ? findValue(addAllToRightLabel, String.class) : "-->>");

        
        addParameter("selectAllLabel",
                selectAllLabel != null ? findValue(selectAllLabel, String.class) : "<*>");

        
        addParameter("leftUpLabel",
                leftUpLabel != null ? findValue(leftUpLabel, String.class) : "^");


        
        addParameter("leftDownLabel",
                leftDownlabel != null ? findValue(leftDownlabel, String.class) : "v");


        
        addParameter("rightUpLabel",
                rightUpLabel != null ? findValue(rightUpLabel, String.class) : "^");


        
        addParameter("rightDownLabel",
                rightDownLabel != null ? findValue(rightDownLabel, String.class) : "v");


        
        addParameter("selectAllOnclick",
                selectAllOnclick != null ? findValue(selectAllOnclick, String.class) : "");

        
        addParameter("addToLeftOnclick",
                addToLeftOnclick != null ? findValue(addToLeftOnclick, String.class) : "");

        
        addParameter("addToRightOnclick",
                addToRightOnclick != null ? findValue(addToRightOnclick, String.class) : "");

        
        addParameter("addAllToLeftOnclick",
                addAllToLeftOnclick != null ? findValue(addAllToLeftOnclick, String.class) : "");

        
        addParameter("addAllToRightOnclick",
                addAllToRightOnclick != null ? findValue(addAllToRightOnclick, String.class) : "");

        
        addParameter("upDownOnLeftOnclick",
                upDownOnLeftOnclick != null ? findValue(upDownOnLeftOnclick, String.class) : "");

        
        addParameter("upDownOnRightOnclick",
                upDownOnRightOnclick != null ? findValue(upDownOnRightOnclick, String.class) : "");



        
        
        Form formAncestor = (Form) findAncestor(Form.class);
        if (formAncestor != null) {

            
            enableAncestorFormCustomOnsubmit();


            
            Map formOptiontransferselectIds = (Map) formAncestor.getParameters().get("optiontransferselectIds");
            Map formOptiontransferselectDoubleIds = (Map) formAncestor.getParameters().get("optiontransferselectDoubleIds");

            
            if (formOptiontransferselectIds == null) {
                formOptiontransferselectIds = new LinkedHashMap();
            }
            if (formOptiontransferselectDoubleIds == null) {
                formOptiontransferselectDoubleIds = new LinkedHashMap();
            }


            
            String tmpId = (String) getParameters().get("id");
            String tmpHeaderKey = (String) getParameters().get("headerKey");
            if (tmpId != null && (! formOptiontransferselectIds.containsKey(tmpId))) {
                formOptiontransferselectIds.put(tmpId, tmpHeaderKey);
            }

            
            String tmpDoubleId = (String) getParameters().get("doubleId");
            String tmpDoubleHeaderKey = (String) getParameters().get("doubleHeaderKey");
            if (tmpDoubleId != null && (! formOptiontransferselectDoubleIds.containsKey(tmpDoubleId))) {
                formOptiontransferselectDoubleIds.put(tmpDoubleId, tmpDoubleHeaderKey);
            }

            formAncestor.getParameters().put("optiontransferselectIds", formOptiontransferselectIds);
            formAncestor.getParameters().put("optiontransferselectDoubleIds", formOptiontransferselectDoubleIds);

        }
        else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("form enclosing optiontransferselect "+this+" not found, auto select upon form submit of optiontransferselect will not work");
            }
        }
    }



    public String getAddAllToLeftLabel() {
        return addAllToLeftLabel;
    }

    @StrutsTagAttribute(description="Set Add To Left button label")
    public void setAddAllToLeftLabel(String addAllToLeftLabel) {
        this.addAllToLeftLabel = addAllToLeftLabel;
    }

    public String getAddAllToRightLabel() {
        return addAllToRightLabel;
    }

    @StrutsTagAttribute(description="Set Add All To Right button label")
    public void setAddAllToRightLabel(String addAllToRightLabel) {
        this.addAllToRightLabel = addAllToRightLabel;
    }

    public String getAddToLeftLabel() {
        return addToLeftLabel;
    }

    @StrutsTagAttribute(description="Set Add To Left button label")
    public void setAddToLeftLabel(String addToLeftLabel) {
        this.addToLeftLabel = addToLeftLabel;
    }

    public String getAddToRightLabel() {
        return addToRightLabel;
    }

    @StrutsTagAttribute(description="Set Add To Right button label")
    public void setAddToRightLabel(String addToRightLabel) {
        this.addToRightLabel = addToRightLabel;
    }

    public String getAllowAddAllToLeft() {
        return allowAddAllToLeft;
    }

    @StrutsTagAttribute(description="Enable Add All To Left button")
    public void setAllowAddAllToLeft(String allowAddAllToLeft) {
        this.allowAddAllToLeft = allowAddAllToLeft;
    }

    public String getAllowAddAllToRight() {
        return allowAddAllToRight;
    }

    @StrutsTagAttribute(description="Enable Add All To Right button")
    public void setAllowAddAllToRight(String allowAddAllToRight) {
        this.allowAddAllToRight = allowAddAllToRight;
    }

    public String getAllowAddToLeft() {
        return allowAddToLeft;
    }

    @StrutsTagAttribute(description="Enable Add To Left button")
    public void setAllowAddToLeft(String allowAddToLeft) {
        this.allowAddToLeft = allowAddToLeft;
    }

    public String getAllowAddToRight() {
        return allowAddToRight;
    }

    @StrutsTagAttribute(description="Enable Add To Right button")
    public void setAllowAddToRight(String allowAddToRight) {
        this.allowAddToRight = allowAddToRight;
    }

    public String getLeftTitle() {
        return leftTitle;
    }

    @StrutsTagAttribute(description="Enable up / down on the left side")
    public void setAllowUpDownOnLeft(String allowUpDownOnLeft) {
        this.allowUpDownOnLeft = allowUpDownOnLeft;
    }

    public String getAllowUpDownOnLeft() {
        return this.allowUpDownOnLeft;
    }

    @StrutsTagAttribute(description="Enable up / down on the right side")
    public void setAllowUpDownOnRight(String allowUpDownOnRight) {
        this.allowUpDownOnRight = allowUpDownOnRight;
    }

    public String getAllowUpDownOnRight() {
        return this.allowUpDownOnRight;
    }

    @StrutsTagAttribute(description="Set Left title")
    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return rightTitle;
    }

    @StrutsTagAttribute(description="Set Right title")
    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    @StrutsTagAttribute(description="Enable Select All button")
    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }

    public String getAllowSelectAll() {
        return this.allowSelectAll;
    }

    @StrutsTagAttribute(description="Set Select All button label")
    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }

    public String getSelectAllLabel() {
        return this.selectAllLabel;
    }

    @StrutsTagAttribute(description="Set buttons css class")
    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssClass() {
        return buttonCssClass;
    }

    @StrutsTagAttribute(description="Set button css style")
    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getButtonCssStyle() {
        return this.buttonCssStyle;
    }

    @StrutsTagAttribute(description="Up label for the left side")
    public void setLeftUpLabel(String leftUpLabel) {
        this.leftUpLabel = leftUpLabel;
    }
    public String getLeftUpLabel() {
        return this.leftUpLabel;
    }

    @StrutsTagAttribute(description="Down label for the left side.")
    public void setLeftDownLabel(String leftDownLabel) {
        this.leftDownlabel = leftDownLabel;
    }
    public String getLeftDownLabel() {
        return this.leftDownlabel;
    }

    @StrutsTagAttribute(description="Up label for the right side.")
    public void setRightUpLabel(String rightUpLabel) {
        this.rightUpLabel = rightUpLabel;
    }
    public String getRightUpLabel() {
        return this.rightUpLabel;
    }

    @StrutsTagAttribute(description="Down label for the left side.")
    public void setRightDownLabel(String rightDownlabel) {
        this.rightDownLabel = rightDownlabel;
    }
    public String getRightDownLabel() {
        return rightDownLabel;
    }

    public String getAddAllToLeftOnclick() {
        return addAllToLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add All To Left button pressed")
    public void setAddAllToLeftOnclick(String addAllToLeftOnclick) {
        this.addAllToLeftOnclick = addAllToLeftOnclick;
    }

    public String getAddAllToRightOnclick() {
        return addAllToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add All To Right button pressed")
    public void setAddAllToRightOnclick(String addAllToRightOnclick) {
        this.addAllToRightOnclick = addAllToRightOnclick;
    }

    public String getAddToLeftOnclick() {
        return addToLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add To Left button pressed")
    public void setAddToLeftOnclick(String addToLeftOnclick) {
        this.addToLeftOnclick = addToLeftOnclick;
    }

    public String getAddToRightOnclick() {
        return addToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Add To Right button pressed")
    public void setAddToRightOnclick(String addToRightOnclick) {
        this.addToRightOnclick = addToRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after up / down on the left side buttons pressed")
    public void setUpDownOnLeftOnclick(String upDownOnLeftOnclick) {
        this.upDownOnLeftOnclick = upDownOnLeftOnclick;
    }

    public String getUpDownOnLeftOnclick() {
        return this.upDownOnLeftOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after up / down on the right side buttons pressed")
    public void setUpDownOnRightOnclick(String upDownOnRightOnclick) {
        this.upDownOnRightOnclick = upDownOnRightOnclick;
    }

    public String getUpDownOnRightOnclick() {
        return this.upDownOnRightOnclick;
    }

    @StrutsTagAttribute(description="Javascript to run after Select All button pressed")
    public void setSelectAllOnclick(String selectAllOnclick) {
        this.selectAllOnclick = selectAllOnclick;
    }

    public String getSelectAllOnclick() {
        return this.selectAllOnclick;
    }

}
