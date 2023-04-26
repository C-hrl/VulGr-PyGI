
package com.opensymphony.xwork2.conversion;


public interface ObjectTypeDeterminer {

    public Class getKeyClass(Class parentClass, String property);

    public Class getElementClass(Class parentClass, String property, Object key);

    public String getKeyProperty(Class parentClass, String property);
    
    public boolean shouldCreateIfNew(Class parentClass,  String property,  Object target, String keyProperty, boolean isIndexAccessed);

}
