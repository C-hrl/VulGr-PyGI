

package org.apache.struts2.views.jsp.iterator;

import java.util.Comparator;

import javax.servlet.jsp.JspException;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.util.SortIteratorFilter;
import org.apache.struts2.views.jsp.StrutsBodyTagSupport;



@StrutsTag(name="sort", tldTagClass="org.apache.struts2.views.jsp.iterator.SortIteratorTag", 
        description="Sort a List using a Comparator both passed in as the tag attribute.")
public class SortIteratorTag extends StrutsBodyTagSupport {

    private static final long serialVersionUID = -7835719609764092235L;

    String comparatorAttr;
    String sourceAttr;
    String var;

    SortIteratorFilter sortIteratorFilter = null;

    @StrutsTagAttribute(required=true,type="java.util.Comparator", description="The comparator to use")
    public void setComparator(String comparator) {
        comparatorAttr = comparator;
    }

    @StrutsTagAttribute(description="The iterable source to sort")
    public void setSource(String source) {
        sourceAttr = source;
    }
    
    @StrutsTagAttribute(description="The name to store the resultant iterator into page context, if such name is supplied")
    public void setVar(String var) {
        this.var = var;
    }

    public int doStartTag() throws JspException {
        
        Object srcToSort;
        if (sourceAttr == null) {
            srcToSort = findValue("top");
        } else {
            srcToSort = findValue(sourceAttr);
        }
        if (! MakeIterator.isIterable(srcToSort)) { 
            throw new JspException("source ["+srcToSort+"] is not iteratable");
        }

        
        Object comparatorObj = findValue(comparatorAttr);
        if (! (comparatorObj instanceof Comparator)) {
            throw new JspException("comparator ["+comparatorObj+"] does not implements Comparator interface");
        }
        Comparator c = (Comparator) findValue(comparatorAttr);

        
        sortIteratorFilter = new SortIteratorFilter();
        sortIteratorFilter.setComparator(c);
        sortIteratorFilter.setSource(srcToSort);
        sortIteratorFilter.execute();

        
        getStack().push(sortIteratorFilter);
        if (var != null && var.length() > 0) {
            pageContext.setAttribute(var, sortIteratorFilter);
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        int returnVal =  super.doEndTag();

        
        getStack().pop();
        sortIteratorFilter = null;

        return returnVal;
    }
}
