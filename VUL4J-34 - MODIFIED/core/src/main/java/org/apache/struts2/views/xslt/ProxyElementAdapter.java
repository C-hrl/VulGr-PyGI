

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.List;


public class ProxyElementAdapter extends ProxyNodeAdapter implements Element {

    private Logger log = LogManager.getLogger(this.getClass());

    public ProxyElementAdapter(AdapterFactory factory, AdapterNode parent, Element value) {
        super(factory, parent, value);
    }

    
    protected Element element() {
        return (Element) getPropertyValue();
    }

    protected List<Node> buildChildAdapters() {
        List<Node> adapters = new ArrayList<>();
        NodeList children = node().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            Node adapter = wrap(child);
            if (adapter != null) {
                log.debug("Wrapped child node: {}", child.getNodeName());
                adapters.add(adapter);
            }
        }
        return adapters;
    }

    

    public String getTagName() {
        return element().getTagName();
    }

    public boolean hasAttribute(String name) {
        return element().hasAttribute(name);
    }

    public String getAttribute(String name) {
        return element().getAttribute(name);
    }

    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return element().hasAttributeNS(namespaceURI, localName);
    }

    public Attr getAttributeNode(String name) {
        log.debug("Wrapping attribute: {}", name);
        return (Attr) wrap(element().getAttributeNode(name));
    }

    
    public NodeList getElementsByTagName(String name) {
        return super.getElementsByTagName(name);
    }

    public String getAttributeNS(String namespaceURI, String localName) {
        return element().getAttributeNS(namespaceURI, localName);
    }

    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        return (Attr) wrap(element().getAttributeNodeNS(namespaceURI, localName));
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return super.getElementsByTagNameNS(namespaceURI, localName);
    }

    

    public void removeAttribute(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(String name, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new UnsupportedOperationException();
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

    public void setIdAttributeNode(Attr attr, boolean b) throws DOMException {
        throw operationNotSupported();
    }

    

    public String toString() {
        return "ProxyElement for: " + element();
    }
}
