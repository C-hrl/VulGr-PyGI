

package org.apache.struts2.views.xslt;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;


public class ProxyTextNodeAdapter extends ProxyNodeAdapter implements Text {

    public ProxyTextNodeAdapter(AdapterFactory factory, AdapterNode parent, Text value) {
        super(factory, parent, value);
    }

    
    Text text() {
        return (Text) getPropertyValue();
    }

    public String toString() {
        return "ProxyTextNode for: " + text();
    }

    public Text splitText(int offset) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public int getLength() {
        return text().getLength();
    }

    public void deleteData(int offset, int count) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public String getData() throws DOMException {
        return text().getData();
    }

    public String substringData(int offset, int count) throws DOMException {
        return text().substringData(offset, count);
    }

    public void replaceData(int offset, int count, String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void insertData(int offset, String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void appendData(String arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public void setData(String data) throws DOMException {
        throw new UnsupportedOperationException();
    }

    

    public boolean isElementContentWhitespace() {
        throw operationNotSupported();
    }

    public String getWholeText() {
        throw operationNotSupported();
    }

    public Text replaceWholeText(String string) throws DOMException {
        throw operationNotSupported();
    }
}

