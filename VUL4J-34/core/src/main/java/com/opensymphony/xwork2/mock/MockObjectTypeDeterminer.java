
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import java.util.Map;


public class MockObjectTypeDeterminer implements ObjectTypeDeterminer {

    private Class keyClass;
    private Class elementClass;
    private String keyProperty;
    private boolean shouldCreateIfNew;

    public MockObjectTypeDeterminer() {}


    
    public MockObjectTypeDeterminer(Class keyClass, Class elementClass,
                                    String keyProperty, boolean shouldCreateIfNew) {
        super();
        this.keyClass = keyClass;
        this.elementClass = elementClass;
        this.keyProperty = keyProperty;
        this.shouldCreateIfNew = shouldCreateIfNew;
    }

    public Class getKeyClass(Class parentClass, String property) {
        return getKeyClass();
    }

    public Class getElementClass(Class parentClass, String property, Object key) {
        return getElementClass();
    }

    public String getKeyProperty(Class parentClass, String property) {
        return getKeyProperty();
    }

    public boolean shouldCreateIfNew(Class parentClass, String property,
                                     Object target, String keyProperty, boolean isIndexAccessed) {
        try {
            System.out.println("ognl:"+OgnlRuntime.getPropertyAccessor(Map.class)+" this:"+this);
        } catch (OgnlException e) {
            
            e.printStackTrace();
        }
        return isShouldCreateIfNew();
    }

    
    public Class getElementClass() {
        return elementClass;
    }
    
    public void setElementClass(Class elementClass) {
        this.elementClass = elementClass;
    }
    
    public Class getKeyClass() {
        return keyClass;
    }
    
    public void setKeyClass(Class keyClass) {
        this.keyClass = keyClass;
    }
    
    public String getKeyProperty() {
        return keyProperty;
    }
    
    public void setKeyProperty(String keyProperty) {
        this.keyProperty = keyProperty;
    }
    
    public boolean isShouldCreateIfNew() {
        return shouldCreateIfNew;
    }
    
    public void setShouldCreateIfNew(boolean shouldCreateIfNew) {
        this.shouldCreateIfNew = shouldCreateIfNew;
    }
}
