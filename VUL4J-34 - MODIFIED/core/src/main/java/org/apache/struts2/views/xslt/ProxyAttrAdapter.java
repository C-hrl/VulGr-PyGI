

package org.apache.struts2.views.xslt;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;


public class ProxyAttrAdapter extends ProxyNodeAdapter implements Attr {

    public ProxyAttrAdapter(AdapterFactory factory, AdapterNode parent, Attr value) {
        super(factory, parent, value);
    }

    
    protected Attr attr() {
        return (Attr) getPropertyValue();
    }

    

    public String getName() {
        return attr().getName();
    }

    public boolean getSpecified() {
        return attr().getSpecified();
    }

    public String getValue() {
        return attr().getValue();
    }

    public void setValue(String string) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Element getOwnerElement() {
        return (Element) getParent();
    }

    

    public TypeInfo getSchemaTypeInfo() {
        throw operationNotSupported();
    }

    public boolean isId() {
        throw operationNotSupported();
    }

    

    

    public String toString() {
        return "ProxyAttribute for: " + attr();
    }
}

