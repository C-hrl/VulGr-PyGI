

package org.apache.struts2.views.xslt;

import com.opensymphony.xwork2.util.DomHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


public class StringAdapter extends AbstractAdapterElement {

    private Logger log = LogManager.getLogger(this.getClass());
    boolean parseStringAsXML;

    public StringAdapter() {
    }

    public StringAdapter(AdapterFactory adapterFactory, AdapterNode parent, String propertyName, String value) {
        setContext(adapterFactory, parent, propertyName, value);
    }

    
    protected String getStringValue() {
        return getPropertyValue().toString();
    }

    protected List<Node> buildChildAdapters() {
        Node node;
        if (getParseStringAsXML()) {
            log.debug("parsing string as xml: {}", getStringValue());
            
            node = DomHelper.parse(new InputSource(new StringReader(getStringValue())));
            node = getAdapterFactory().proxyNode(this, node);
        } else {
            log.debug("using string as is: {}", getStringValue());
            
            node = new SimpleTextNode(getAdapterFactory(), this, "text", getStringValue());
        }

        List<Node> children = new ArrayList<>();
        children.add(node);
        return children;
    }

    
    public boolean getParseStringAsXML() {
        return parseStringAsXML;
    }

    
    public void setParseStringAsXML(boolean parseStringAsXML) {
        this.parseStringAsXML = parseStringAsXML;
    }

}
