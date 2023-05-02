

package org.apache.struts2.views.xslt;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public class ProxyNamedNodeMap implements NamedNodeMap {

    private NamedNodeMap nodes;
    private AdapterFactory adapterFactory;
    private AdapterNode parent;

    public ProxyNamedNodeMap(AdapterFactory factory, AdapterNode parent, NamedNodeMap nodes) {
        this.nodes = nodes;
        this.adapterFactory = factory;
        this.parent = parent;
    }

    protected Node wrap(Node node) {
        return adapterFactory.proxyNode(parent, node);
    }

    public int getLength() {
        return nodes.getLength();
    }

    public Node item(int index) {
        return wrap(nodes.item(index));
    }

    public Node getNamedItem(String name) {
        return wrap(nodes.getNamedItem(name));
    }

    public Node removeNamedItem(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node setNamedItem(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public Node getNamedItemNS(String namespaceURI, String localName) {
        return wrap(nodes.getNamedItemNS(namespaceURI, localName));
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }
}
