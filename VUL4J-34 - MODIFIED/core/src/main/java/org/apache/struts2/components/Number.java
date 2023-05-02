
package org.apache.struts2.components;



import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Currency;


@StrutsTag(name = "number", tldBodyContent = "empty", tldTagClass = "org.apache.struts2.views.jsp.NumberTag", description = "Render a formatted number.")
public class Number extends ContextBean {

    private static final Logger LOG = LogManager.getLogger(Number.class);
    
    public static final String NUMBERTAG_PROPERTY = "struts.number.format";

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

    public Number(ValueStack stack) {
        super(stack);
    }

    public boolean end(Writer writer, String body) {
        java.lang.Number number = findNumberName();

        if (number != null) {

            NumberFormat format = getNumberFormat();
            findCurrency(format);
            setNumberFormatParameters(format);
            setRoundingMode(format);

            String msg = format.format(number);
            if (msg != null) {
                try {
                    if (getVar() == null) {
                        writer.write(msg);
                    } else {
                        putInContext(msg);
                    }
                } catch (IOException e) {
                    LOG.error("Could not write out Number tag", e);
                }
            }
        }
        return super.end(writer, "");
    }

    
    private void findCurrency(NumberFormat format) {
        if (currency != null) {
            Object currencyValue = findValue(currency);
            if (currencyValue != null) {
                currency = currencyValue.toString();
            }
            try {
                format.setCurrency(Currency.getInstance(currency));
            } catch (IllegalArgumentException iae) {
                LOG.error("Could not recognise a currency of [" + currency + "]");
            }
        }
    }

    private void setNumberFormatParameters(NumberFormat format) {
        if (groupingUsed != null) {
            format.setGroupingUsed(groupingUsed);
        }
        if (maximumFractionDigits != null) {
            format.setMaximumFractionDigits(maximumFractionDigits);
        }
        if (maximumIntegerDigits != null) {
            format.setMaximumIntegerDigits(maximumIntegerDigits);
        }
        if (minimumFractionDigits != null) {
            format.setMinimumFractionDigits(minimumFractionDigits);
        }
        if (minimumIntegerDigits != null) {
            format.setMinimumIntegerDigits(minimumIntegerDigits);
        }
        if (parseIntegerOnly != null) {
            format.setParseIntegerOnly(parseIntegerOnly);
        }
    }

    private java.lang.Number findNumberName() {
        java.lang.Number number = null;
        
        try {
            
            Object numberObject = findValue(name);
            if (numberObject instanceof java.lang.Number) {
                number = (java.lang.Number) numberObject;
            }
        } catch (Exception e) {
            LOG.error("Could not convert object with key [" + name + "] to a java.lang.Number instance");
        }
        return number;
    }

    private void setRoundingMode(NumberFormat format) {
    
    }

    private NumberFormat getNumberFormat() {
        NumberFormat format = null;
        if (type == null) {
            try {
                type = findString(NUMBERTAG_PROPERTY);
            } catch (Exception e) {
                LOG.error("Could not find [" + NUMBERTAG_PROPERTY + "] on the stack!", e);
            }
        }
        if (type != null) {
            type = findString(type);
            if ("currency".equals(type)) {
                format = NumberFormat.getCurrencyInstance(ActionContext.getContext().getLocale());
            } else if ("integer".equals(type)) {
                format = NumberFormat.getIntegerInstance(ActionContext.getContext().getLocale());
            } else if ("number".equals(type)) {
                format = NumberFormat.getNumberInstance(ActionContext.getContext().getLocale());
            } else if ("percent".equals(type)) {
                format = NumberFormat.getPercentInstance(ActionContext.getContext().getLocale());
            }
        }
        if (format == null) {
            format = NumberFormat.getInstance(ActionContext.getContext().getLocale());
        }
        return format;
    }

    @StrutsTagAttribute(description = "Type of number formatter (currency, integer, number or percent, default is number)", rtexprvalue = false)
    public void setType(String type) {
        this.type = type;
    }

    @StrutsTagAttribute(description = "The currency to use for a currency format", type = "String", defaultValue = "")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    
    public String getName() {
        return name;
    }

    @StrutsTagAttribute(description = "The number value to format", required = true)
    public void setName(String name) {
        this.name = name;
    }

    
    public String getType() {
        return type;
    }

    
    public String getCurrency() {
        return currency;
    }

    @StrutsTagAttribute(description = "Whether grouping is used", type = "Boolean")
    public void setGroupingUsed(Boolean groupingUsed) {
        this.groupingUsed = groupingUsed;
    }

    
    public Boolean isGroupingUsed() {
        return groupingUsed;
    }

    
    public Integer getMaximumFractionDigits() {
        return maximumFractionDigits;
    }

    
    @StrutsTagAttribute(description = "Maximum fraction digits", type = "Integer")
    public void setMaximumFractionDigits(Integer maximumFractionDigits) {
        this.maximumFractionDigits = maximumFractionDigits;
    }

    
    public Integer getMaximumIntegerDigits() {
        return maximumIntegerDigits;
    }

    
    @StrutsTagAttribute(description = "Maximum integer digits", type = "Integer")
    public void setMaximumIntegerDigits(Integer maximumIntegerDigits) {
        this.maximumIntegerDigits = maximumIntegerDigits;
    }

    
    public Integer getMinimumFractionDigits() {
        return minimumFractionDigits;
    }

    
    @StrutsTagAttribute(description = "Minimum fraction digits", type = "Integer")
    public void setMinimumFractionDigits(Integer minimumFractionDigits) {
        this.minimumFractionDigits = minimumFractionDigits;
    }

    
    public Integer getMinimumIntegerDigits() {
        return minimumIntegerDigits;
    }

    
    @StrutsTagAttribute(description = "Maximum integer digits", type = "Integer")
    public void setMinimumIntegerDigits(Integer minimumIntegerDigits) {
        this.minimumIntegerDigits = minimumIntegerDigits;
    }

    
    public Boolean isParseIntegerOnly() {
        return parseIntegerOnly;
    }

    
    @StrutsTagAttribute(description = "Parse integer only", type = "Boolean")
    public void setParseIntegerOnly(Boolean parseIntegerOnly) {
        this.parseIntegerOnly = parseIntegerOnly;
    }

    
    public String getRoundingMode() {
        return roundingMode;
    }

    
    @StrutsTagAttribute(description = "The rounding mode to use - not implemented yet as this required Java 1.6", type = "String")
    public void setRoundingMode(String roundingMode) {
        this.roundingMode = roundingMode;
    }

}
