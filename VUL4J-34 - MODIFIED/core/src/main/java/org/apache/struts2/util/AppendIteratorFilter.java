

package org.apache.struts2.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.Action;



public class AppendIteratorFilter extends IteratorFilterSupport implements Iterator, Action {

    List iterators = new ArrayList();

    
    List sources = new ArrayList();


    
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
        if (iterators.size() > 0) {
            return (((Iterator) iterators.get(0)).hasNext());
        } else {
            return false;
        }
    }

    public Object next() {
        try {
            return ((Iterator) iterators.get(0)).next();
        } finally {
            if (iterators.size() > 0) {
                if (!((Iterator) iterators.get(0)).hasNext()) {
                    iterators.remove(0);
                }
            }
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
