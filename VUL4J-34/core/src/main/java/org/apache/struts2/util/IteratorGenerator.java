

package org.apache.struts2.util;

import com.opensymphony.xwork2.Action;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;



public class IteratorGenerator implements Iterator, Action {

    private static final Logger LOG = LogManager.getLogger(IteratorGenerator.class);

    List values;
    Object value;
    String separator;
    Converter converter;

    
    int count = 0;
    int currentCount = 0;


    public void setCount(int aCount) {
        this.count = aCount;
    }

    public boolean getHasNext() {
        return hasNext();
    }

    public Object getNext() {
        return next();
    }

    public void setSeparator(String aChar) {
        separator = aChar;
    }

    public void setConverter(Converter aConverter) {
        converter = aConverter;
    }

    
    public void setValues(Object aValue) {
        value = aValue;
    }

    
    public String execute() {
        if (value == null) {
            return ERROR;
        } else {
            values = new ArrayList();

            if (separator != null) {
                StringTokenizer tokens = new StringTokenizer(value.toString(), separator);

                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken().trim();
                    if (converter != null) {
                        try {
                            Object convertedObj = converter.convert(token);
                            values.add(convertedObj);
                        }
                        catch(Exception e) { 
                            LOG.warn("Unable to convert [{}], skipping this token, it will not appear in the generated iterator", token, e);
                        }
                    }
                    else {
                        values.add(token);
                    }
                }
            } else {
                values.add(value.toString());
            }

            
            if (count == 0) {
                count = values.size();
            }

            return SUCCESS;
        }
    }

    
    public boolean hasNext() {
        return (value == null) ? false : ((currentCount < count) || (count == -1));
    }

    public Object next() {
        try {
            return values.get(currentCount % values.size());
        } finally {
            currentCount++;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported in IteratorGenerator.");
    }


    
    
    public static interface Converter {
        Object convert(String token) throws Exception;
    }
}
