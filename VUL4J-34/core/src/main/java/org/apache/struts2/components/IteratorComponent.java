

package org.apache.struts2.components;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.util.MakeIterator;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.jsp.IteratorStatus;

import java.io.Writer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


@StrutsTag(name="iterator", tldTagClass="org.apache.struts2.views.jsp.IteratorTag", description="Iterate over a iterable value")
public class IteratorComponent extends ContextBean {
    private static final Logger LOG = LogManager.getLogger(IteratorComponent.class);

    protected Iterator iterator;
    protected IteratorStatus status;
    protected Object oldStatus;
    protected IteratorStatus.StatusState statusState;
    protected String statusAttr;
    protected String value;
    protected String beginStr;
    protected Integer begin;
    protected String endStr;
    protected Integer end;
    protected String stepStr;
    protected Integer step;

    public IteratorComponent(ValueStack stack) {
        super(stack);
    }

    public boolean start(Writer writer) {
        
        if (statusAttr != null) {
            statusState = new IteratorStatus.StatusState();
            status = new IteratorStatus(statusState);
        }

        if (beginStr != null)
            begin = (Integer) findValue(beginStr,  Integer.class);

        if (endStr != null)
            end = (Integer) findValue(endStr,  Integer.class);

        if (stepStr != null)
            step = (Integer) findValue(stepStr,  Integer.class);

        ValueStack stack = getStack();

        if (value == null && begin == null && end == null) {
            value = "top";
        }
        Object iteratorTarget = findValue(value);
        iterator = MakeIterator.convert(iteratorTarget);

        if (begin != null) {
            
            if (step == null)
                step = 1;

            if (iterator == null) {
                
                iterator = new CounterIterator(begin, end, step, null);
            } else {
                
                if (iteratorTarget.getClass().isArray()) {
                    Object[] values = (Object[]) iteratorTarget;
                    if (end == null)
                        end = step > 0 ? values.length - 1 : 0;
                    iterator = new CounterIterator(begin, end, step, Arrays.asList(values));
                } else if (iteratorTarget instanceof List) {
                    List values = (List) iteratorTarget;
                    if (end == null)
                        end = step > 0 ? values.size() - 1 : 0;
                    iterator = new CounterIterator(begin, end, step, values);
                } else {
                    
                    LOG.error("Incorrect use of the iterator tag. When 'begin' is set, 'value' must be" +
                            " an Array or a List, or not set at all. 'begin', 'end' and 'step' will be ignored");
                }
            }
        }

        
        if ((iterator != null) && iterator.hasNext()) {
            Object currentValue = iterator.next();
            stack.push(currentValue);

            String var = getVar();

            if ((var != null)) {
                putInContext(currentValue);
            }

            
            if (statusAttr != null) {
                statusState.setLast(!iterator.hasNext());
                oldStatus = stack.getContext().get(statusAttr);
                stack.getContext().put(statusAttr, status);
            }

            return true;
        } else {
            super.end(writer, "");
            return false;
        }
    }

    public boolean end(Writer writer, String body) {
        ValueStack stack = getStack();
        if (iterator != null) {
            stack.pop();
        }

        if (iterator!=null && iterator.hasNext()) {
            Object currentValue = iterator.next();
            stack.push(currentValue);

            putInContext(currentValue);

            
            if (status != null) {
                statusState.next(); 
                statusState.setLast(!iterator.hasNext());
            }

            return true;
        } else {
            
            if (status != null) {
                if (oldStatus == null) {
                    stack.getContext().put(statusAttr, null);
                } else {
                    stack.getContext().put(statusAttr, oldStatus);
                }
            }
            super.end(writer, "");
            return false;
        }
    }

    static class CounterIterator implements Iterator<Object> {
        private int step;
        private int end;
        private int currentIndex;
        private List<Object> values;

        CounterIterator(Integer begin, Integer end, Integer step, List<Object> values) {
            this.end = end;
            if (step != null)
                this.step = step;
            this.currentIndex = begin - this.step;
            this.values = values;
        }

        public boolean hasNext() {
            int next = peekNextIndex();
            return step > 0 ? next <= end : next >= end;
        }

        public Object next() {
            if (hasNext()) {
                int nextIndex = peekNextIndex();
                currentIndex += step;
                return values != null ? values.get(nextIndex) : nextIndex;
            } else {
                throw new IndexOutOfBoundsException("Index " + ( currentIndex + step) + " must be less than or equal to " + end);
            }
        }

        private int peekNextIndex() {
           return currentIndex + step;
        }

        public void remove() {
            throw new UnsupportedOperationException("Values cannot be removed from this iterator");
        }
    }

    @StrutsTagAttribute(description="If specified, an instanceof IteratorStatus will be pushed into stack upon each iteration",
        type="Boolean", defaultValue="false")
    public void setStatus(String status) {
        this.statusAttr = status;
    }

    @StrutsTagAttribute(description="the iteratable source to iterate over, else an the object itself will be put into a newly created List")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="if specified the iteration will start on that index", type="Integer", defaultValue="0")
    public void setBegin(String begin) {
        this.beginStr = begin;
    }

    @StrutsTagAttribute(description="if specified the iteration will end on that index(inclusive)", type="Integer",
            defaultValue="Size of the 'values' List or array, or 0 if 'step' is negative")
    public void setEnd(String end) {
        this.endStr = end;
    }

    @StrutsTagAttribute(description="if specified the iteration index will be increased by this value on each iteration. It can be a negative " +
            "value, in which case 'begin' must be greater than 'end'", type="Integer", defaultValue="1")
    public void setStep(String step) {
        this.stepStr = step;
    }
}
