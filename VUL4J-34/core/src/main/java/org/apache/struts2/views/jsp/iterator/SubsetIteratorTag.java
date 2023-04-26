

package org.apache.struts2.views.jsp.iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.SubsetIteratorFilter;
import org.apache.struts2.util.SubsetIteratorFilter.Decider;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;

import javax.servlet.jsp.JspException;



@StrutsTag(name="subset", tldTagClass="org.apache.struts2.views.jsp.iterator.SubsetIteratorTag",
        description="Takes an iterator and outputs a subset of it.")
public class SubsetIteratorTag extends StrutsBodyTagSupport {

    private static final long serialVersionUID = -6252696081713080102L;

    private static final Logger LOG = LogManager.getLogger(SubsetIteratorTag.class);

    String countAttr;
    String sourceAttr;
    String startAttr;
    String deciderAttr;
    String var;
    SubsetIteratorFilter subsetIteratorFilter = null;


    @StrutsTagAttribute(type="Integer", description="Indicate the number of entries to be in the resulting subset iterator")
    public void setCount(String count) {
        countAttr = count;
    }

    @StrutsTagAttribute(description="Indicate the source of which the resulting subset iterator is to be derived base on")
    public void setSource(String source) {
        sourceAttr = source;
    }

    
    @StrutsTagAttribute(type="Integer",
            description="Indicate the starting index (eg. first entry is 0) of entries in the source to be available as the first entry in the resulting subset iterator")
    public void setStart(String start) {
        startAttr = start;
    }

    @StrutsTagAttribute(type="org.apache.struts2.util.SubsetIteratorFilter.Decider",
            description="Extension to plug-in a decider to determine if that particular entry is to be included in the resulting subset iterator")
    public void setDecider(String decider) {
        deciderAttr = decider;
    }

    @StrutsTagAttribute(description="The name to store the resultant iterator into page context, if such name is supplied")
    public void setVar(String var) {
        this.var = var;
    }

    public int doStartTag() throws JspException {

        
        Object source = null;
        if (sourceAttr == null || sourceAttr.length() == 0) {
            source = findValue("top");
        } else {
            source = findValue(sourceAttr);
        }

        
        int count = -1;
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
                    LOG.warn("unable to convert count attribute [{}] to number, ignore count attribute", countObj, e);
                }
            }
        }

        
        int start = 0;
        if (startAttr != null && startAttr.length() > 0) {
            Object startObj = findValue(startAttr);
            if (startObj instanceof Integer) {
                start = ((Integer)startObj).intValue();
            }
            else if (startObj instanceof Float) {
                start = ((Float)startObj).intValue();
            }
            else if (startObj instanceof Long) {
                start = ((Long)startObj).intValue();
            }
            else if (startObj instanceof Double) {
                start = ((Double)startObj).intValue();
            }
            else if (startObj instanceof String) {
                try {
                    start = Integer.parseInt((String)startObj);
                }
                catch(NumberFormatException e) {
                    LOG.warn("unable to convert count attribute [{}] to number, ignore count attribute", startObj, e);
                }
            }
        }

        
        Decider decider = null;
        if (deciderAttr != null && deciderAttr.length() > 0) {
            Object deciderObj = findValue(deciderAttr);
            if (! (deciderObj instanceof Decider)) {
                throw new JspException("decider found from stack ["+deciderObj+"] does not implement "+Decider.class);
            }
            decider = (Decider) deciderObj;
        }


        subsetIteratorFilter = new SubsetIteratorFilter();
        subsetIteratorFilter.setCount(count);
        subsetIteratorFilter.setDecider(decider);
        subsetIteratorFilter.setSource(source);
        subsetIteratorFilter.setStart(start);
        subsetIteratorFilter.execute();

        getStack().push(subsetIteratorFilter);
        if (var != null && var.length() > 0) {
            pageContext.setAttribute(var, subsetIteratorFilter);
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {

        getStack().pop();

        subsetIteratorFilter = null;

        return EVAL_PAGE;
    }
}
