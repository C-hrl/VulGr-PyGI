

package org.apache.struts2.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.Action;



public class MergeIteratorFilter extends IteratorFilterSupport implements Iterator, Action {

    List iterators = new ArrayList();

    
    List sources = new ArrayList();
    int idx = 0;


    
    public void setSource(Object anIterator) {
        sources.add(anIterator);
    }

    
    public String execute() {
        
        for (int i = 0; i < sources.size(); i++) {
            Object source = sources.get(i);
            iterators.add(getIterator(source));
        }

        return SUCCESS;
    }

    
    public boolean hasNext() {
        while (iterators.size() > 0) {
            if (((Iterator) iterators.get(idx)).hasNext()) {
                return true;
            } else {
                iterators.remove(idx);

                if (iterators.size() > 0) {
                    idx = idx % iterators.size();
                }
            }
        }

        return false;
    }

    public Object next() {
        try {
            return ((Iterator) iterators.get(idx)).next();
        } finally {
            idx = (idx + 1) % iterators.size();
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in MergeIteratorFilter.");
    }
}
