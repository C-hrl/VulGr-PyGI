

package org.apache.struts2.util;

import java.util.Enumeration;
import java.util.Iterator;



public abstract class IteratorFilterSupport {

    
    protected Object getIterator(Object source) {
        return MakeIterator.convert(source);
    }


    
    public static class EnumerationIterator implements Iterator {
        Enumeration enumeration;

        public EnumerationIterator(Enumeration aEnum) {
            enumeration = aEnum;
        }

        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        public Object next() {
            return enumeration.nextElement();
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported in IteratorFilterSupport.");
        }
    }
}
