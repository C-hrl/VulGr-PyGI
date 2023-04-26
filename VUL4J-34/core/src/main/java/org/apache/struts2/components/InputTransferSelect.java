

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


@StrutsTag(name="inputtransferselect", tldTagClass="org.apache.struts2.views.jsp.ui.InputTransferSelectTag", description="Renders an input form")
public class InputTransferSelect extends ListUIBean {

    private static final Logger LOG = LogManager.getLogger(InputTransferSelect.class);

    private static final String TEMPLATE = "inputtransferselect";

    protected String size;
    protected String multiple;

    protected String allowRemoveAll;
    protected String allowUpDown;

    protected String leftTitle;
    protected String rightTitle;

    protected String buttonCssClass;
    protected String buttonCssStyle;

    protected String addLabel;
    protected String removeLabel;
    protected String removeAllLabel;
    protected String upLabel;
    protected String downLabel;

    protected String headerKey;
    protected String headerValue;


    public InputTransferSelect(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }


    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        if (StringUtils.isBlank(size)) {
            addParameter("size", "5");
        }

        if (StringUtils.isBlank(multiple)) {
            addParameter("multiple", Boolean.TRUE);
        }

        
        addParameter("allowUpDown", allowUpDown != null ? findValue(allowUpDown, Boolean.class) : Boolean.TRUE);

        
        addParameter("allowRemoveAll", allowRemoveAll != null ? findValue(allowRemoveAll, Boolean.class) : Boolean.TRUE);


        
        if (leftTitle != null) {
            addParameter("leftTitle", findValue(leftTitle, String.class));
        }

        
        if (rightTitle != null) {
            addParameter("rightTitle", findValue(rightTitle, String.class));
        }


        
        if (StringUtils.isNotBlank(buttonCssClass)) {
            addParameter("buttonCssClass", buttonCssClass);
        }

        
        if (StringUtils.isNotBlank(buttonCssStyle)) {
            addParameter("buttonCssStyle", buttonCssStyle);
        }

        
        addParameter("addLabel", addLabel != null ? findValue(addLabel, String.class) : "->" );

        
        addParameter("removeLabel", removeLabel != null ? findValue(removeLabel, String.class) : "<-");

        
        addParameter("removeAllLabel", removeAllLabel != null ? findValue(removeAllLabel, String.class) : "<<--");


        
        addParameter("upLabel", upLabel != null ? findValue(upLabel, String.class) : "^");


        
        addParameter("downLabel", downLabel != null ? findValue(downLabel, String.class) : "v");

        if ((headerKey != null) && (headerValue != null)) {
            addParameter("headerKey", findString(headerKey));
            addParameter("headerValue", findString(headerValue));
        }



        
        
        Form formAncestor = (Form) findAncestor(Form.class);
        if (formAncestor != null) {

            
            enableAncestorFormCustomOnsubmit();


            
            Map formInputtransferselectIds = (Map) formAncestor.getParameters().get("inputtransferselectIds");

            
            if (formInputtransferselectIds == null) {
                formInputtransferselectIds = new LinkedHashMap();
            }

            
            String tmpId = (String) getParameters().get("id");
            String tmpHeaderKey = (String) getParameters().get("headerKey");
            if (tmpId != null && (! formInputtransferselectIds.containsKey(tmpId))) {
                formInputtransferselectIds.put(tmpId, tmpHeaderKey);
            }

            formAncestor.getParameters().put("inputtransferselectIds", formInputtransferselectIds);

        }
        else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("form enclosing inputtransferselect "+this+" not found, auto select upon form submit of inputtransferselect will not work");
            }
        }
    }

    public String getSize() {
        return size;
    }

    @StrutsTagAttribute(description="the size of the select box")
    public void setSize(String size) {
        this.size = size;
    }

    public String getMultiple() {
        return multiple;
    }

    @StrutsTagAttribute(description="Determine whether or not multiple entries are shown")
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public String getAllowRemoveAll() {
        return allowRemoveAll;
    }

    @StrutsTagAttribute(description="Determine whether the remove all button will display")
    public void setAllowRemoveAll(String allowRemoveAll) {
        this.allowRemoveAll = allowRemoveAll;
    }

    public String getAllowUpDown() {
        return allowUpDown;
    }

    @StrutsTagAttribute(description="Determine whether items in the list can be reordered")
    public void setAllowUpDown(String allowUpDown) {
        this.allowUpDown = allowUpDown;
    }

    public String getLeftTitle() {
        return leftTitle;
    }

    @StrutsTagAttribute(description="the left hand title")
    public void setLeftTitle(String leftTitle) {
        this.leftTitle = leftTitle;
    }

    public String getRightTitle() {
        return rightTitle;
    }

    @StrutsTagAttribute(description="the right hand title")
    public void setRightTitle(String rightTitle) {
        this.rightTitle = rightTitle;
    }

    public String getButtonCssClass() {
        return buttonCssClass;
    }

    @StrutsTagAttribute(description="the css class used for rendering buttons")
    public void setButtonCssClass(String buttonCssClass) {
        this.buttonCssClass = buttonCssClass;
    }

    public String getButtonCssStyle() {
        return buttonCssStyle;
    }

    @StrutsTagAttribute(description="the css style used for rendering buttons")
    public void setButtonCssStyle(String buttonCssStyle) {
        this.buttonCssStyle = buttonCssStyle;
    }

    public String getAddLabel() {
        return addLabel;
    }

    @StrutsTagAttribute(description="the label used for the add button")
    public void setAddLabel(String addLabel) {
        this.addLabel = addLabel;
    }

    public String getRemoveLabel() {
        return removeLabel;
    }

    @StrutsTagAttribute(description="the label used for the remove button")
    public void setRemoveLabel(String removeLabel) {
        this.removeLabel = removeLabel;
    }

    public String getRemoveAllLabel() {
        return removeAllLabel;
    }

    @StrutsTagAttribute(description="the label used for the remove all button")
    public void setRemoveAllLabel(String removeAllLabel) {
        this.removeAllLabel = removeAllLabel;
    }

    public String getUpLabel() {
        return upLabel;
    }

    @StrutsTagAttribute(description="the label used for the up button")
    public void setUpLabel(String upLabel) {
        this.upLabel = upLabel;
    }

    public String getDownLabel() {
        return downLabel;
    }

    @StrutsTagAttribute(description="the label used for the down button")
    public void setDownLabel(String downLabel) {
        this.downLabel = downLabel;
    }

    public String getHeaderKey() {
        return headerKey;
    }

    @StrutsTagAttribute(description="the header key of the select box")
    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    @StrutsTagAttribute(description="the header value of the select box")
    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }
}
