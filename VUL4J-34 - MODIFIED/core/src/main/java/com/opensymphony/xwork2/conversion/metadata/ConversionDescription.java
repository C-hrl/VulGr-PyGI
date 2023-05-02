
package com.opensymphony.xwork2.conversion.metadata;

import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.conversion.impl.DefaultObjectTypeDeterminer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.PrintWriter;
import java.io.StringWriter;


public class ConversionDescription {

    
    protected static Logger log = null;


    public static final String KEY_PREFIX = "Key_";
    public static final String ELEMENT_PREFIX = "Element_";
    public static final String KEY_PROPERTY_PREFIX = "KeyProperty_";
    public static final String DEPRECATED_ELEMENT_PREFIX = "Collection_";

    
    String MAP_PREFIX = "Map_";

    public String property;
    public String typeConverter = "";
    public String rule = "";
    public String value = "";
    public String fullQualifiedClassName;
    public String type = null;

    public ConversionDescription() {
        log = LogManager.getLogger(this.getClass());
    }

    
    public ConversionDescription(String property) {
        this.property = property;
        log = LogManager.getLogger(this.getClass());
    }

    
    public void setProperty(String property) {
        this.property = property;
    }

    
    public void setTypeConverter(String typeConverter) {
        this.typeConverter = typeConverter;
    }

    
    public void setRule(String rule) {
        if (rule != null && rule.length() > 0) {
            if (rule.equals(ConversionRule.COLLECTION.toString())) {
                this.rule = DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX;
            } else if (rule.equals(ConversionRule.ELEMENT.toString())) {
                this.rule = DefaultObjectTypeDeterminer.ELEMENT_PREFIX;
            } else if (rule.equals(ConversionRule.KEY.toString())) {
                this.rule = DefaultObjectTypeDeterminer.KEY_PREFIX;
            } else if (rule.equals(ConversionRule.KEY_PROPERTY.toString())) {
                this.rule = DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX;
            } else if (rule.equals(ConversionRule.MAP.toString())) {
                this.rule = MAP_PREFIX;
            }
        }
    }


    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
    public String asProperty() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(sw);
            writer.print(rule);
            writer.print(property);
            writer.print("=");
            if ( rule.startsWith(DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX) && value != null && value.length() > 0 ) {
                writer.print(value);
            } else {
                writer.print(typeConverter);
            }
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        return sw.toString();

    }

    
    public String getFullQualifiedClassName() {
        return fullQualifiedClassName;
    }

    
    public void setFullQualifiedClassName(String fullQualifiedClassName) {
        this.fullQualifiedClassName = fullQualifiedClassName;
    }
}
