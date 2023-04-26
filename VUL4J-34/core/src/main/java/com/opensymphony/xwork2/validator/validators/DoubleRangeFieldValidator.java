

package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.commons.lang3.StringUtils;


public class DoubleRangeFieldValidator extends FieldValidatorSupport {
    
    private Double maxInclusive = null;
    private Double minInclusive = null;
    private Double minExclusive = null;
    private Double maxExclusive = null;

    private String minInclusiveExpression;
    private String maxInclusiveExpression;
    private String minExclusiveExpression;
    private String maxExclusiveExpression;

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Double value;
        try {
            Object obj = this.getFieldValue(fieldName, object);
            if (obj == null) {
                return;
            }
            value = Double.valueOf(obj.toString());
        } catch (NumberFormatException e) {
            return;
        }

        Double maxInclusiveToUse = getMaxInclusive();
        Double minInclusiveToUse = getMinInclusive();
        Double maxExclusiveToUse = getMaxExclusive();
        Double minExclusiveToUse = getMinExclusive();

        if ((maxInclusiveToUse != null && value.compareTo(maxInclusiveToUse) > 0) ||
                (minInclusiveToUse != null && value.compareTo(minInclusiveToUse) < 0) ||
                (maxExclusiveToUse != null && value.compareTo(maxExclusiveToUse) >= 0) ||
                (minExclusiveToUse != null && value.compareTo(minExclusiveToUse) <= 0)) {
            addFieldError(fieldName, object);
        }
    }

    public void setMaxInclusive(Double maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public Double getMaxInclusive() {
        if (maxInclusive != null) {
            return maxInclusive;
        } else if (StringUtils.isNotEmpty(maxInclusiveExpression)) {
            return (Double) parse(maxInclusiveExpression, Double.class);
        }
        return maxInclusive;
    }

    public void setMinInclusive(Double minInclusive) {
        this.minInclusive = minInclusive;
    }

    public Double getMinInclusive() {
        if (minInclusive != null) {
            return minInclusive;
        } else if (StringUtils.isNotEmpty(minInclusiveExpression)) {
            return (Double) parse(minInclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMinExclusive(Double minExclusive) {
        this.minExclusive = minExclusive;
    }

    public Double getMinExclusive() {
        if (minExclusive != null) {
            return minExclusive;
        } else if (StringUtils.isNotEmpty(minExclusiveExpression)) {
            return (Double) parse(minExclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMaxExclusive(Double maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public Double getMaxExclusive() {
        if (maxExclusive != null) {
            return maxExclusive;
        } else if (StringUtils.isNotEmpty(maxExclusiveExpression)) {
            return (Double) parse(maxExclusiveExpression, Double.class);
        }
        return null;
    }

    public void setMinInclusiveExpression(String minInclusiveExpression) {
        this.minInclusiveExpression = minInclusiveExpression;
    }

    public void setMaxInclusiveExpression(String maxInclusiveExpression) {
        this.maxInclusiveExpression = maxInclusiveExpression;
    }

    public void setMinExclusiveExpression(String minExclusiveExpression) {
        this.minExclusiveExpression = minExclusiveExpression;
    }

    public void setMaxExclusiveExpression(String maxExclusiveExpression) {
        this.maxExclusiveExpression = maxExclusiveExpression;
    }

}
