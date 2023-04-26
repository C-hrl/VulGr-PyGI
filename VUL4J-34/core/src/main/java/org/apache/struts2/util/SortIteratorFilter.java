

package org.apache.struts2.util;

import com.opensymphony.xwork2.Action;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;



public class SortIteratorFilter extends IteratorFilterSupport implements Iterator, Action {
    private static final Logger LOG = LogManager.getLogger(IteratorGenerator.class);

    Comparator comparator;
    Iterator iterator;
    List list;

    
    Object source;


    public void setComparator(Comparator aComparator) {
        this.comparator = aComparator;
    }

    public List getList() {
        return list;
    }

    
    public void setSource(Object anIterator) {
        source = anIterator;
    }

    
    public String execute() {
        if (source == null) {
            return ERROR;
        } else {
            try {
                if (!MakeIterator.isIterable(source)) {
                    LOG.warn("Cannot create SortIterator for source: {}", source);
                    return ERROR;
                }

                list = new ArrayList();

                Iterator i = MakeIterator.convert(source);

                while (i.hasNext()) {
                    list.add(i.next());
                }

                
                Collections.sort(list, comparator);
                iterator = list.iterator();

                return SUCCESS;
            } catch (Exception e) {
                LOG.warn("Error creating sort iterator.", e);
                return ERROR;
            }
        }
    }

    
    public boolean hasNext() {
        return (source == null) ? false : iterator.hasNext();
    }

    public Object next() {
        return iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in SortIteratorFilter.");
    }
}
