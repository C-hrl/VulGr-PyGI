

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Param.UnnamedParametric;
import org.apache.struts2.util.AppendIteratorFilter;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@StrutsTag(name="append", tldTagClass="org.apache.struts2.views.jsp.iterator.AppendIteratorTag", description="Append the values of a list of iterators to one iterator")
public class AppendIterator extends ContextBean implements UnnamedParametric {

    private static final Logger LOG = LogManager.getLogger(AppendIterator.class);

    private AppendIteratorFilter appendIteratorFilter= null;
    private List _parameters;

    public AppendIterator(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        _parameters = new ArrayList();
        appendIteratorFilter = new AppendIteratorFilter();

        return super.start(writer);
    }

    public boolean end(Writer writer, String body) {

        for (Iterator paramEntries = _parameters.iterator(); paramEntries.hasNext(); ) {

            Object iteratorEntryObj = paramEntries.next();
            if (! MakeIterator.isIterable(iteratorEntryObj)) {
                LOG.warn("param with value resolved as {} cannot be make as iterator, it will be ignored and hence will not appear in the merged iterator", iteratorEntryObj);
                continue;
            }
            appendIteratorFilter.setSource(MakeIterator.convert(iteratorEntryObj));
        }

        appendIteratorFilter.execute();

        putInContext(appendIteratorFilter);

        appendIteratorFilter = null;

        return super.end(writer, body);
    }

    
    public void addParameter(Object value) {
        _parameters.add(value);
    }

    @StrutsTagAttribute(description="The name of which if supplied will have the resultant appended iterator stored under in the stack's context")
    public void setVar(String var) {
        super.setVar(var);
    }
}


