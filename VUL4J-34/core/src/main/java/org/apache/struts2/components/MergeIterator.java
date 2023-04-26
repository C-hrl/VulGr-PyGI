

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.components.Param.UnnamedParametric;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.util.MergeIteratorFilter;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


@StrutsTag(name="merge", tldTagClass="org.apache.struts2.views.jsp.iterator.MergeIteratorTag", description="Merge the values " +
                "of a list of iterators into one iterator")
public class MergeIterator extends ContextBean implements UnnamedParametric {

    private static final Logger LOG = LogManager.getLogger(MergeIterator.class);

    private MergeIteratorFilter mergeIteratorFilter = null;
    private List _parameters;

    public MergeIterator(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {

        mergeIteratorFilter = new MergeIteratorFilter();
        _parameters = new ArrayList();

        return super.start(writer);
    }

    public boolean end(Writer writer, String body) {

        for (Object iteratorEntryObj : _parameters) {
            if (! MakeIterator.isIterable(iteratorEntryObj)) {
                LOG.warn("param with value resolved as {} cannot be make as iterator, it will be ignored and hence will not appear in the merged iterator", iteratorEntryObj);
                continue;
            }
            mergeIteratorFilter.setSource(MakeIterator.convert(iteratorEntryObj));
        }

        mergeIteratorFilter.execute();

        
        putInContext(mergeIteratorFilter);

        mergeIteratorFilter = null;

        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="The name where the resultant merged iterator will be stored in the stack's context")
    public void setVar(String var) {
        super.setVar(var);
    }

    
    public void addParameter(Object value) {
        _parameters.add(value);
    }
}
