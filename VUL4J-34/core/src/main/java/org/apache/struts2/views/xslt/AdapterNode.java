

package org.apache.struts2.views.xslt;

import org.w3c.dom.Node;


public interface AdapterNode extends Node {

    
    AdapterFactory getAdapterFactory();

    
    void setAdapterFactory(AdapterFactory factory);

    
    AdapterNode getParent();

    
    void setParent(AdapterNode parent);

    
    Node getChildBefore(Node thisNode);

    
    Node getChildAfter(Node thisNode);

    
    String getPropertyName();

    
    void setPropertyName(String name);

    
    Object getPropertyValue();

    
    void setPropertyValue(Object prop );
}
