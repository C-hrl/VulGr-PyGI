
package com.opensymphony.xwork2.util.location;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


public class LocationAttributes {
    
    public static final String PREFIX = "loc";
    
    public static final String URI = "http:

    
    public static final String SRC_ATTR  = "src";
    
    public static final String LINE_ATTR = "line";
    
    public static final String COL_ATTR  = "column";

    
    public static final String Q_SRC_ATTR  = "loc:src";
    
    public static final String Q_LINE_ATTR = "loc:line";
    
    public static final String Q_COL_ATTR  = "loc:column";
    
    
    private LocationAttributes() {
        
    }
    
    
    public static Attributes addLocationAttributes(Locator locator, Attributes attrs) {
        if (locator == null || attrs.getIndex(URI, SRC_ATTR) != -1) {
            
            return attrs;
        }
        
        
        AttributesImpl newAttrs = attrs instanceof AttributesImpl ?
            (AttributesImpl)attrs : new AttributesImpl(attrs);

        newAttrs.addAttribute(URI, SRC_ATTR, Q_SRC_ATTR, "CDATA", locator.getSystemId());
        newAttrs.addAttribute(URI, LINE_ATTR, Q_LINE_ATTR, "CDATA", Integer.toString(locator.getLineNumber()));
        newAttrs.addAttribute(URI, COL_ATTR, Q_COL_ATTR, "CDATA", Integer.toString(locator.getColumnNumber()));
        
        return newAttrs;
    }
    
    
    public static Location getLocation(Attributes attrs, String description) {
        String src = attrs.getValue(URI, SRC_ATTR);
        if (src == null) {
            return Location.UNKNOWN;
        }
        
        return new LocationImpl(description, src, getLine(attrs), getColumn(attrs));
    }

    
    public static String getLocationString(Attributes attrs) {
        String src = attrs.getValue(URI, SRC_ATTR);
        if (src == null) {
            return LocationUtils.UNKNOWN_STRING;
        }
        
        return src + ":" + attrs.getValue(URI, LINE_ATTR) + ":" + attrs.getValue(URI, COL_ATTR);
    }
    
    
    public static String getURI(Attributes attrs) {
        String src = attrs.getValue(URI, SRC_ATTR);
        return src != null ? src : LocationUtils.UNKNOWN_STRING;
    }
    
    
    public static int getLine(Attributes attrs) {
        String line = attrs.getValue(URI, LINE_ATTR);
        return line != null ? Integer.parseInt(line) : -1;
    }
    
    
    public static int getColumn(Attributes attrs) {
        String col = attrs.getValue(URI, COL_ATTR);
        return col != null ? Integer.parseInt(col) : -1;
    }
    
    
    public static Location getLocation(Element elem, String description) {
        Attr srcAttr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        if (srcAttr == null) {
            return Location.UNKNOWN;
        }

        return new LocationImpl(description == null ? elem.getNodeName() : description,
                srcAttr.getValue(), getLine(elem), getColumn(elem));
    }
    
    
    public static Location getLocation(Element elem) {
        return getLocation(elem, null);
    }
   

    
    public static String getLocationString(Element elem) {
        Attr srcAttr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        if (srcAttr == null) {
            return LocationUtils.UNKNOWN_STRING;
        }
        
        return srcAttr.getValue() + ":" + elem.getAttributeNS(URI, LINE_ATTR) + ":" + elem.getAttributeNS(URI, COL_ATTR);
    }
    
    
    public static String getURI(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, SRC_ATTR);
        return attr != null ? attr.getValue() : LocationUtils.UNKNOWN_STRING;
    }

    
    public static int getLine(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, LINE_ATTR);
        return attr != null ? Integer.parseInt(attr.getValue()) : -1;
    }

    
    public static int getColumn(Element elem) {
        Attr attr = elem.getAttributeNodeNS(URI, COL_ATTR);
        return attr != null ? Integer.parseInt(attr.getValue()) : -1;
    }
    
    
    public static void remove(Element elem, boolean recurse) {
        elem.removeAttributeNS(URI, SRC_ATTR);
        elem.removeAttributeNS(URI, LINE_ATTR);
        elem.removeAttributeNS(URI, COL_ATTR);
        if (recurse) {
            NodeList children = elem.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    remove((Element)child, recurse);
                }
            }
        }
    }

    
    public static class Pipe implements ContentHandler {
        
        private Locator locator;
        
        private ContentHandler nextHandler;
        
        
        public Pipe() {
        }

        
        public Pipe(ContentHandler next) {
            nextHandler = next;
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
            nextHandler.setDocumentLocator(locator);
        }
        
        public void startDocument() throws SAXException {
            nextHandler.startDocument();
            nextHandler.startPrefixMapping(LocationAttributes.PREFIX, LocationAttributes.URI);
        }
        
        public void endDocument() throws SAXException {
            endPrefixMapping(LocationAttributes.PREFIX);
            nextHandler.endDocument();
        }

        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            
            nextHandler.startElement(uri, loc, raw, LocationAttributes.addLocationAttributes(locator, attrs));
        }

        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            nextHandler.endElement(arg0, arg1, arg2);
        }

        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            nextHandler.startPrefixMapping(arg0, arg1);
        }

        public void endPrefixMapping(String arg0) throws SAXException {
            nextHandler.endPrefixMapping(arg0);
        }

        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.characters(arg0, arg1, arg2);
        }

        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.ignorableWhitespace(arg0, arg1, arg2);
        }

        public void processingInstruction(String arg0, String arg1) throws SAXException {
            nextHandler.processingInstruction(arg0, arg1);
        }

        public void skippedEntity(String arg0) throws SAXException {
            nextHandler.skippedEntity(arg0);
        }
    }
}
