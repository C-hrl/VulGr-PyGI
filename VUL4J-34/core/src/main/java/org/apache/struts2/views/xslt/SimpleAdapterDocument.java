

package org.apache.struts2.views.xslt;

import java.util.Arrays;
import java.util.List;

import org.apache.struts2.StrutsException;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;


public class SimpleAdapterDocument extends AbstractAdapterNode implements Document {

    private Element rootElement;

    public SimpleAdapterDocument(
            AdapterFactory adapterFactory, AdapterNode parent, String propertyName, Object value) {
        setContext(adapterFactory, parent, propertyName, value);

    }

    public void setPropertyValue(Object prop) {
        super.setPropertyValue(prop);
        rootElement = null; 
    }

    
    private Element getRootElement() {
        if (rootElement != null)
            return rootElement;

        Node node = getAdapterFactory().adaptNode(
                this, getPropertyName(), getPropertyValue());
        if (node instanceof Element)
            rootElement = (Element) node;
        else
            throw new StrutsException(
                    "Document adapter expected to wrap an Element type.  Node is not an element:" + node);

        return rootElement;
    }

    protected List<Node> getChildAdapters() {
        return Arrays.asList(new Node[]{getRootElement()});
    }

    public NodeList getChildNodes() {
        return new NodeList() {
            public Node item(int i) {
                return getRootElement();
            }

            public int getLength() {
                return 1;
            }
        };
    }

    public DocumentType getDoctype() {
        return null;
    }

    public Element getDocumentElement() {
        return getRootElement();
    }

    public Element getElementById(String string) {
        return null;
    }

    public NodeList getElementsByTagName(String string) {
        return null;
    }

    public NodeList getElementsByTagNameNS(String string, String string1) {
        return null;
    }

    public Node getFirstChild() {
        return getRootElement();
    }

    public DOMImplementation getImplementation() {
        return null;
    }

    public Node getLastChild() {
        return getRootElement();
    }

    public String getNodeName() {
        return "#document";
    }

    public short getNodeType() {
        return Node.DOCUMENT_NODE;
    }

    public Attr createAttribute(String string) throws DOMException {
        return null;
    }

    public Attr createAttributeNS(String string, String string1) throws DOMException {
        return null;
    }

    public CDATASection createCDATASection(String string) throws DOMException {
        return null;
    }

    public Comment createComment(String string) {
        return null;
    }

    public DocumentFragment createDocumentFragment() {
        return null;
    }

    public Element createElement(String string) throws DOMException {
        return null;
    }

    public Element createElementNS(String string, String string1) throws DOMException {
        return null;
    }

    public EntityReference createEntityReference(String string) throws DOMException {
        return null;
    }

    public ProcessingInstruction createProcessingInstruction(String string, String string1) throws DOMException {
        return null;
    }

    public Text createTextNode(String string) {
        return null;
    }

    public boolean hasChildNodes() {
        return true;
    }

    public Node importNode(Node node, boolean b) throws DOMException {
        return null;
    }

    public Node getChildAfter(Node child) {
        return null;
    }

    public Node getChildBefore(Node child) {
        return null;
    }

    

    public String getInputEncoding() {
        throw operationNotSupported();
    }

    public String getXmlEncoding() {
        throw operationNotSupported();
    }

    public boolean getXmlStandalone() {
        throw operationNotSupported();
    }

    public void setXmlStandalone(boolean b) throws DOMException {
        throw operationNotSupported();
    }

    public String getXmlVersion() {
        throw operationNotSupported();
    }

    public void setXmlVersion(String string) throws DOMException {
        throw operationNotSupported();
    }

    public boolean getStrictErrorChecking() {
        throw operationNotSupported();
    }

    public void setStrictErrorChecking(boolean b) {
        throw operationNotSupported();
    }

    public String getDocumentURI() {
        throw operationNotSupported();
    }

    public void setDocumentURI(String string) {
        throw operationNotSupported();
    }

    public Node adoptNode(Node node) throws DOMException {
        throw operationNotSupported();
    }

    public DOMConfiguration getDomConfig() {
        throw operationNotSupported();
    }

    public void normalizeDocument() {
        throw operationNotSupported();
    }

    public Node renameNode(Node node, String string, String string1) throws DOMException {
        return null;
    }
    
}
