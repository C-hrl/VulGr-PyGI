package org.apache.struts2.views.jsp;



import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.components.Number;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class NumberTag extends ContextBeanTag {

    private static final long serialVersionUID = -6216963123295613440L;

    private String name;
    private String currency;
    private String type;
    private Boolean groupingUsed;
    private Integer maximumFractionDigits;
    private Integer maximumIntegerDigits;
    private Integer minimumFractionDigits;
    private Integer minimumIntegerDigits;
    private Boolean parseIntegerOnly;
    private String roundingMode;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new Number(stack);
    }

    protected void populateParams() {
        super.populateParams();
        Number n = (Number) component;
        n.setName(name);
        n.setCurrency(currency);
        n.setType(type);
        n.setGroupingUsed(groupingUsed);
        n.setMaximumFractionDigits(maximumFractionDigits);
        n.setMaximumIntegerDigits(maximumIntegerDigits);
        n.setMinimumFractionDigits(minimumFractionDigits);
        n.setMinimumIntegerDigits(minimumIntegerDigits);
        n.setParseIntegerOnly(parseIntegerOnly);
        n.setRoundingMode(roundingMode);

    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public void setGroupingUsed(Boolean groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    
    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    
    public void setMaximumIntegerDigits(Integer maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
    }

    
    public void setMinimumFractionDigits(Integer minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    
    public void setMinimumIntegerDigits(Integer minimumIntegerDigits) {
        this.minimumIntegerDigits = minimumIntegerDigits;
    }

    
    public void setParseIntegerOnly(Boolean parseIntegerOnly) {
        this.parseIntegerOnly = parseIntegerOnly;
    }

    
    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }

}
