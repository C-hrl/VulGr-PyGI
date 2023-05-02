

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


public abstract class ProxyNodeAdapter extends AbstractAdapterNode {

    private Logger log = LogManager.getLogger(this.getClass());

    public ProxyNodeAdapter(AdapterFactory factory, AdapterNode parent, Node value) {
        setContext(factory, parent, "document", value);
        log.debug("Proxied node is: {}" + value);
        log.debug("Node class is: {}", value.getClass());
        log.debug("Node type is: {}", value.getNodeType());
        log.debug("Node name is: {}", value.getNodeName());
    }

    
    protected Node node() {
        return (Node) getPropertyValue();
    }

    
    protected Node wrap(Node node) {
        return getAdapterFactory().proxyNode(this, node);
    }

    protected NamedNodeMap wrap(NamedNodeMap nnm) {
        return getAdapterFactory().proxyNamedNodeMap(this, nnm);
    }
    

    
    
    

    

    public String getNodeName() {
        log.trace("getNodeName");
        return node().getNodeName();
    }

    public String getNodeValue() throws DOMException {
        log.trace("getNodeValue");
        return node().getNodeValue();
    }

    public short getNodeType() {
        if (log.isTraceEnabled()) {
            log.trace("getNodeType: " + getNodeName() + ": " + node().getNodeType());
        }
        return node().getNodeType();
    }

    public NamedNodeMap getAttributes() {
        NamedNodeMap nnm = wrap(node().getAttributes());
        if (log.isTraceEnabled()) {
            log.trace("getAttributes: " + nnm);
        }
        return nnm;
    }

    public boolean hasChildNodes() {
        log.trace("hasChildNodes");
        return node().hasChildNodes();
    }

    public boolean isSupported(String s, String s1) {
        log.trace("isSupported");
        
        return node().isSupported(s, s1);
    }

    public String getNamespaceURI() {
        log.trace("getNamespaceURI");
        return node().getNamespaceURI();
    }

    public String getPrefix() {
        log.trace("getPrefix");
        return node().getPrefix();
    }

    public String getLocalName() {
        log.trace("getLocalName");
        return node().getLocalName();
    }

    public boolean hasAttributes() {
        log.trace("hasAttributes");
        return node().hasAttributes();
    }

    

    public String toString() {
        return "ProxyNode for: " + node();
    }
}

