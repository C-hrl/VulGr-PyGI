

package org.apache.struts2.views.jsp.iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.IteratorGenerator;
import org.apache.struts2.util.IteratorGenerator.Converter;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import javax.servlet.jsp.JspException;



@StrutsTag(name="generator", tldTagClass="org.apache.struts2.views.jsp.iterator.IteratorGeneratorTag",
        description="Generate an iterator for a iterable source.")
public class IteratorGeneratorTag extends StrutsBodyTagSupport {

    private static final long serialVersionUID = 2968037295463973936L;

    public static final String DEFAULT_SEPARATOR = ",";

    private static final Logger LOG = LogManager.getLogger(IteratorGeneratorTag.class);

    String countAttr;
    String separatorAttr;
    String valueAttr;
    String converterAttr;
    String var;
    IteratorGenerator iteratorGenerator = null;

    @StrutsTagAttribute(type="Integer",description="The max number entries to be in the iterator")
    public void setCount(String count) {
        countAttr = count;
    }

    
    @StrutsTagAttribute(required=true, description="The separator to be used in separating the <i>val</i> into entries of the iterator")
    public void setSeparator(String separator) {
        separatorAttr = separator;
    }

    
    @StrutsTagAttribute(required=true, description="The source to be parsed into an iterator")
    public void setVal(String val) {
        valueAttr = val;
    }

    @StrutsTagAttribute(type="org.apache.struts2.util.IteratorGenerator.Converter",
            description="The converter to convert the String entry parsed from <i>val</i> into an object")
    public void setConverter(String aConverter) {
        converterAttr = aConverter;
    }

    @StrutsTagAttribute(description="The name to store the resultant iterator into page context, if such name is supplied")
    public void setVar(String var) {
        this.var = var;
    }

    public int doStartTag() throws JspException {

        
        Object value = findValue(valueAttr);

        
        String separator = DEFAULT_SEPARATOR;
        if (separatorAttr != null && separatorAttr.length() > 0) {
            separator = findString(separatorAttr);
        }

        
        
        int count = 0;
        if (countAttr != null && countAttr.length() > 0) {
            Object countObj = findValue(countAttr);
            if (countObj instanceof Number) {
                count = ((Number)countObj).intValue();
            }
            else if (countObj instanceof String) {
                try {
                    count = Integer.parseInt((String)countObj);
                }
                catch(NumberFormatException e) {
                    LOG.warn("Unable to convert count attribute [{}] to number, ignore count attribute", countObj, e);
                }
            }
        }

        
        Converter converter = null;
        if (converterAttr != null && converterAttr.length() > 0) {
            converter = (Converter) findValue(converterAttr);
        }


        iteratorGenerator = new IteratorGenerator();
        iteratorGenerator.setValues(value);
        iteratorGenerator.setCount(count);
        iteratorGenerator.setSeparator(separator);
        iteratorGenerator.setConverter(converter);

        iteratorGenerator.execute();

        
        
        getStack().push(iteratorGenerator);
        if (var != null && var.length() > 0) {
            getStack().getContext().put(var, iteratorGenerator);
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        
        getStack().pop();
        iteratorGenerator = null; 

        return EVAL_PAGE;
    }
}
