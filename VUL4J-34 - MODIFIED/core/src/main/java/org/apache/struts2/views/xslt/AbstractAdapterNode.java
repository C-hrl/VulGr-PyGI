

package org.apache.struts2.views.xslt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.w3c.dom.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractAdapterNode implements AdapterNode {

    private static final NamedNodeMap EMPTY_NAMEDNODEMAP =
            new NamedNodeMap() {
                public int getLength() {
                    return 0;
                }

                public Node item(int index) {
                    return null;
                }

                public Node getNamedItem(String name) {
                    return null;
                }

                public Node removeNamedItem(String name) throws DOMException {
                    return null;
                }

                public Node setNamedItem(Node arg) throws DOMException {
                    return null;
                }

                public Node setNamedItemNS(Node arg) throws DOMException {
                    return null;
                }

                public Node getNamedItemNS(String namespaceURI, String localName) {
                    return null;
                }

                public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
                    return null;
                }
            };

    private List<Node> childAdapters;
    private Logger log = LogManager.getLogger(this.getClass());

    
    private Object propertyValue;
    private String propertyName;
    private AdapterNode parent;
    private AdapterFactory adapterFactory;


    public AbstractAdapterNode() {
        if (LogManager.getLogger(getClass()).isDebugEnabled()) {
            LogManager.getLogger(getClass()).debug("Creating " + this);
        }
    }

    
    protected void setContext(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setAdapterFactory(adapterFactory);
        setParent(parent);
        setPropertyName(propertyName);
        setPropertyValue(value);
    }

    
    protected List<Node> buildChildAdapters() {
        return new ArrayList<Node>();
    }

    
    protected List<Node> getChildAdapters() {
        if (childAdapters == null) {
            childAdapters = buildChildAdapters();
        }
        return childAdapters;
    }

    public Node getChildBeforeOrAfter(Node child, boolean before) {
        log.debug("getChildBeforeOrAfter: ");
        List adapters = getChildAdapters();
        if (log.isDebugEnabled()) {
            log.debug("childAdapters = {}", adapters);
            log.debug("child = {}", child);
        }
        int index = adapters.indexOf(child);
        if (index < 0)
            throw new StrutsException(child + " is no child of " + this);
        int siblingIndex = before ? index - 1 : index + 1;
        return ((0 < siblingIndex) && (siblingIndex < adapters.size())) ?
                ((Node) adapters.get(siblingIndex)) : null;
    }

    public Node getChildAfter(Node child) {
        log.trace("getChildAfter");
        return getChildBeforeOrAfter(child, false);
    }

    public Node getChildBefore(Node child) {
        log.trace("getChildBefore");
        return getChildBeforeOrAfter(child, true);
    }

    public NodeList getElementsByTagName(String tagName) {
        if (tagName.equals("*")) {
            return getChildNodes();
        } else {
            LinkedList<Node> filteredChildren = new LinkedList<>();

            for (Node adapterNode : getChildAdapters()) {
                if (adapterNode.getNodeName().equals(tagName)) {
                    filteredChildren.add(adapterNode);
                }
            }

            return new SimpleNodeList(filteredChildren);
        }
    }

    public NodeList getElementsByTagNameNS(String string, String string1) {
        
        return null;
    }

    

    public NamedNodeMap getAttributes() {
        return EMPTY_NAMEDNODEMAP;
    }

    public NodeList getChildNodes() {
        NodeList nl = new SimpleNodeList(getChildAdapters());
        log.debug("getChildNodes for tag: {} num children: {}", getNodeName(), nl.getLength());
        return nl;
    }

    public Node getFirstChild() {
        return (getChildNodes().getLength() > 0) ? getChildNodes().item(0) : null;
    }

    public Node getLastChild() {
        return (getChildNodes().getLength() > 0) ? getChildNodes().item(getChildNodes().getLength() - 1) : null;
    }


    public String getLocalName() {
        return null;
    }

    public String getNamespaceURI() {
        return null;
    }

    public void setNodeValue(String string) throws DOMException {
        throw operationNotSupported();
    }

    public String getNodeValue() throws DOMException {
        throw operationNotSupported();
    }

    public Document getOwnerDocument() {
        return null;
    }

    public Node getParentNode() {
        log.trace("getParentNode");
        return getParent();
    }

    public AdapterNode getParent() {
        return parent;
    }

    public void setParent(AdapterNode parent) {
        this.parent = parent;
    }

    public Object getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(Object prop) {
        this.propertyValue = prop;
    }

    public void setPrefix(String string) throws DOMException {
        throw operationNotSupported();
    }

    public String getPrefix() {
        return null;
    }

    public Node getNextSibling() {
        Node next = getParent().getChildAfter(this);
        if (log.isTraceEnabled()) {
            log.trace("getNextSibling on " + getNodeName() + ": "
                    + ((next == null) ? "null" : next.getNodeName()));
        }

        return next;
    }

    public Node getPreviousSibling() {
        return getParent().getChildBefore(this);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String name) {
        this.propertyName = name;
    }

    public AdapterFactory getAdapterFactory() {
        return adapterFactory;
    }

    public void setAdapterFactory(AdapterFactory adapterFactory) {
        this.adapterFactory = adapterFactory;
    }

    public boolean isSupported(String string, String string1) {
        throw operationNotSupported();
    }

    public Node appendChild(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public Node cloneNode(boolean b) {
        log.trace("cloneNode");
        throw operationNotSupported();
    }

    public boolean hasAttributes() {
        return false;
    }

    public boolean hasChildNodes() {
        return false;
    }

    public Node insertBefore(Node node, Node node1) throws DOMException {
        throw operationNotSupported();
    }

    public void normalize() {
        log.trace("normalize");
        throw operationNotSupported();
    }

    public Node removeChild(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public Node replaceChild(Node node, Node node1) throws DOMException {
        throw operationNotSupported();
    }

    

    public boolean isDefaultNamespace(String string) {
        throw operationNotSupported();
    }

    public String lookupNamespaceURI(String string) {
        throw operationNotSupported();
    }

    public String getNodeName() {
        throw operationNotSupported();
    }

    public short getNodeType() {
        throw operationNotSupported();
    }

    public String getBaseURI() {
        throw operationNotSupported();
    }

    public short compareDocumentPosition(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public String getTextContent() throws DOMException {
        throw operationNotSupported();
    }

    public void setTextContent(String string) throws DOMException {
        throw operationNotSupported();

    }

    public boolean isSameNode(Node node) {
        throw operationNotSupported();
    }

    public String lookupPrefix(String string) {
        throw operationNotSupported();
    }

    public boolean isEqualNode(Node node) {
        throw operationNotSupported();
    }

    public Object getFeature(String string, String string1) {
        throw operationNotSupported();
    }

    public Object setUserData(String string, Object object, UserDataHandler userDataHandler) {
        throw operationNotSupported();
    }

    public Object getUserData(String string) {
        throw operationNotSupported();
    }

    

    protected StrutsException operationNotSupported() {
        return new StrutsException("Operation not supported.");
    }

    public String toString() {
        return getClass() + ": " + getNodeName() + " parent=" + getParentNode();
    }
}
