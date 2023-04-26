

package org.apache.struts2.views.xslt;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.TypeInfo;


public abstract class AbstractAdapterElement extends AbstractAdapterNode implements Element {

    private Map attributeAdapters;

    public AbstractAdapterElement() { }

    public void setAttribute(String string, String string1) throws DOMException {
        throw operationNotSupported();
    }

    protected Map getAttributeAdapters() {
        if ( attributeAdapters == null )
            attributeAdapters = buildAttributeAdapters();
        return attributeAdapters;
    }

    protected Map buildAttributeAdapters() {
        return new HashMap();
    }

    
    public String getAttribute(String string) {
        return "";
    }

    public void setAttributeNS(String string, String string1, String string2) throws DOMException {
        throw operationNotSupported();
    }

    public String getAttributeNS(String string, String string1) {
        return null;
    }

    public Attr setAttributeNode(Attr attr) throws DOMException {
        throw operationNotSupported();
    }

    public Attr getAttributeNode( String name ) {
        return (Attr)getAttributes().getNamedItem( name );
    }

    public Attr setAttributeNodeNS(Attr attr) throws DOMException {
        throw operationNotSupported();
    }

    public Attr getAttributeNodeNS(String string, String string1) {
        throw operationNotSupported();
    }

    public String getNodeName() {
        return getTagName();
    }

    public short getNodeType() {
        return Node.ELEMENT_NODE;
    }

    public String getTagName() {
        return getPropertyName();
    }

    public boolean hasAttribute(String string) {
        return false;
    }

    public boolean hasAttributeNS(String string, String string1) {
        return false;
    }

    public boolean hasChildNodes() {
        return getElementsByTagName("*").getLength() > 0;
    }

    public void removeAttribute(String string) throws DOMException {
        throw operationNotSupported();
    }

    public void removeAttributeNS(String string, String string1) throws DOMException {
        throw operationNotSupported();
    }

    public Attr removeAttributeNode(Attr attr) throws DOMException {
        throw operationNotSupported();
    }

    public void setIdAttributeNode(Attr attr, boolean b) throws DOMException {
        throw operationNotSupported();
    }

    public TypeInfo getSchemaTypeInfo() {
        throw operationNotSupported();
    }

    public void setIdAttribute(String string, boolean b) throws DOMException {
        throw operationNotSupported();
    }

    public void setIdAttributeNS(String string, String string1, boolean b) throws DOMException {
        throw operationNotSupported();
    }

}

