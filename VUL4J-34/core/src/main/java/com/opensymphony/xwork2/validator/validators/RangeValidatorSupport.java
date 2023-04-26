
package com.opensymphony.xwork2.validator.validators;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;


public abstract class RangeValidatorSupport<T extends Comparable> extends FieldValidatorSupport {

    private static final Logger LOG = LogManager.getLogger(RangeValidatorSupport.class);

    private final Class<T> type;

    private T min;
    private String minExpression;
    private T max;
    private String maxExpression;

    protected RangeValidatorSupport(Class<T> type) {
        this.type = type;
    }

    public void validate(Object object) throws ValidationException {
        Object obj = getFieldValue(getFieldName(), object);
        Comparable<T> value = (Comparable<T>) obj;

        
        
        if (value == null) {
            return;
        }

        
        T minComparatorValue = getMin();
        if ((minComparatorValue != null) && (value.compareTo(minComparatorValue) < 0)) {
            addFieldError(getFieldName(), object);
        }

        
        T maxComparatorValue = getMax();
        if ((maxComparatorValue != null) && (value.compareTo(maxComparatorValue) > 0)) {
            addFieldError(getFieldName(), object);
        }
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMin() {
        if (min != null) {
            return min;
        } else if (StringUtils.isNotEmpty(minExpression)) {
            return (T) parse(minExpression, type);
        } else {
            return null;
        }
    }

    public void setMinExpression(String minExpression) {
        LOG.debug("${minExpression} was defined as [{}]", minExpression);
        this.minExpression = minExpression;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public T getMax() {
        if (max != null) {
            return max;
        } else if (StringUtils.isNotEmpty(maxExpression)) {
            return (T) parse(maxExpression, type);
        } else {
            return null;
        }
    }

    public void setMaxExpression(String maxExpression) {
        LOG.debug("${maxExpression} was defined as [{}]", maxExpression);
        this.maxExpression = maxExpression;
    }

}
