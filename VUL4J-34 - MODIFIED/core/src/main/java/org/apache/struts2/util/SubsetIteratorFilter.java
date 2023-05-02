

package org.apache.struts2.util;

import com.opensymphony.xwork2.Action;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class SubsetIteratorFilter extends IteratorFilterSupport implements Iterator, Action {

    private static final Logger LOG = LogManager.getLogger(SubsetIteratorFilter.class);

    Iterator iterator;
    Object source;
    int count = -1;
    int currentCount = 0;

    Decider decider;

    
    int start = 0;


    public void setCount(int aCount) {
        this.count = aCount;
    }

    
    public void setSource(Object anIterator) {
        source = anIterator;
    }

    public void setStart(int aStart) {
        this.start = aStart;
    }

    public void setDecider(Decider aDecider) {
        this.decider = aDecider;
    }

    
    public String execute() {
        if (source == null) {
            LogManager.getLogger(SubsetIteratorFilter.class.getName()).warn("Source is null returning empty set.");

            return ERROR;
        }

        
        source = getIterator(source);

        
        if (source instanceof Iterator) {
            iterator = (Iterator) source;


            
            for (int i = 0; (i < start) && iterator.hasNext(); i++) {
                iterator.next();
            }


            
            if (decider != null) {
                List list = new ArrayList();
                while(iterator.hasNext()) {
                    Object currentElement = iterator.next();
                    if (decide(currentElement)) {
                        list.add(currentElement);
                    }
                }
                iterator = list.iterator();
            }

        } else if (source.getClass().isArray()) {
            ArrayList list = new ArrayList(((Object[]) source).length);
            Object[] objects = (Object[]) source;
            int len = objects.length;

            if (count >= 0) {
                len = start + count;
                if (len > objects.length) {
                    len = objects.length;
                }
            }

            for (int j = start; j < len; j++) {
                if (decide(objects[j])) {
                    list.add(objects[j]);
                }
            }

            count = -1; 
            iterator = list.iterator();
        }

        if (iterator == null) {
            throw new IllegalArgumentException("Source is not an iterator:" + source);
        }

        return SUCCESS;
    }

    
    public boolean hasNext() {
        return (iterator == null) ? false : (iterator.hasNext() && ((count < 0) || (currentCount < count)));
    }

    public Object next() {
        currentCount++;

        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }

    
    
    public static interface Decider {

        
        boolean decide(Object element) throws Exception;
    }

    
    protected boolean decide(Object element) {
        if (decider != null) {
            try {
                boolean okToAdd = decider.decide(element);
                return okToAdd;
            }
            catch(Exception e) {
                LOG.warn("Decider [{}] encountered an error while decide adding element [{}], element will be ignored, it will not appeared in subseted iterator",
                            decider, element, e);
                return false;
            }
        }
        return true;
    }
}
