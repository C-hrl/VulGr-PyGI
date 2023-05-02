
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationAttributes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import java.util.Map;


public class DomHelper {

    private static final Logger LOG = LogManager.getLogger(DomHelper.class);
    
    public static final String XMLNS_URI = "http:

    public static Location getLocationObject(Element element) {
        return LocationAttributes.getLocation(element);
    }

    
    
    public static Document parse(InputSource inputSource) {
        return parse(inputSource, null);
    }
    
    
    
    public static Document parse(InputSource inputSource, Map<String, String> dtdMappings) {
                
        SAXParserFactory factory = null;
        String parserProp = System.getProperty("xwork.saxParserFactory");
        if (parserProp != null) {
            try {
                ObjectFactory objectFactory = ActionContext.getContext().getContainer().getInstance(ObjectFactory.class);
                Class clazz = objectFactory.getClassInstance(parserProp);
                factory = (SAXParserFactory) clazz.newInstance();
            } catch (Exception e) {
                LOG.error("Unable to load saxParserFactory set by system property 'xwork.saxParserFactory': {}", parserProp, e);
            }
        }

        if (factory == null) {
            factory = SAXParserFactory.newInstance();
        }

        factory.setValidating((dtdMappings != null));
        factory.setNamespaceAware(true);

        SAXParser parser;
        try {
            parser = factory.newSAXParser();
        } catch (Exception ex) {
            throw new XWorkException("Unable to create SAX parser", ex);
        }
        
        
        DOMBuilder builder = new DOMBuilder();

        
        ContentHandler locationHandler = new LocationAttributes.Pipe(builder);
        
        try {
            parser.parse(inputSource, new StartHandler(locationHandler, dtdMappings));
        } catch (Exception ex) {
            throw new XWorkException(ex);
        }
        
        return builder.getDocument();
    }
    
    
    static public class DOMBuilder implements ContentHandler {
    
        
        protected static SAXTransformerFactory FACTORY;
    
        
        protected SAXTransformerFactory factory;
    
        
        protected DOMResult result;
    
        
        protected Node parentNode;
        
        protected ContentHandler nextHandler;
    
        static {
            String parserProp = System.getProperty("xwork.saxTransformerFactory");
            if (parserProp != null) {
                try {
                    ObjectFactory objectFactory = ActionContext.getContext().getContainer().getInstance(ObjectFactory.class);
                    Class clazz = objectFactory.getClassInstance(parserProp);
                    FACTORY = (SAXTransformerFactory) clazz.newInstance();
                } catch (Exception e) {
                    LOG.error("Unable to load SAXTransformerFactory set by system property 'xwork.saxTransformerFactory': {}", parserProp, e);
                }
            }

            if (FACTORY == null) {
                 FACTORY = (SAXTransformerFactory) TransformerFactory.newInstance();
            }
        }

        
        public DOMBuilder() {
            this((Node) null);
        }
    
        
        public DOMBuilder(SAXTransformerFactory factory) {
            this(factory, null);
        }
    
        
        public DOMBuilder(Node parentNode) {
            this(null, parentNode);
        }
    
        
        public DOMBuilder(SAXTransformerFactory factory, Node parentNode) {
            this.factory = factory == null? FACTORY: factory;
            this.parentNode = parentNode;
            setup();
        }
    
        
        private void setup() {
            try {
                TransformerHandler handler = this.factory.newTransformerHandler();
                nextHandler = handler;
                if (this.parentNode != null) {
                    this.result = new DOMResult(this.parentNode);
                } else {
                    this.result = new DOMResult();
                }
                handler.setResult(this.result);
            } catch (javax.xml.transform.TransformerException local) {
                throw new XWorkException("Fatal-Error: Unable to get transformer handler", local);
            }
        }
    
        
        public Document getDocument() {
            if (this.result == null || this.result.getNode() == null) {
                return null;
            } else if (this.result.getNode().getNodeType() == Node.DOCUMENT_NODE) {
                return (Document) this.result.getNode();
            } else {
                return this.result.getNode().getOwnerDocument();
            }
        }
    
        public void setDocumentLocator(Locator locator) {
            nextHandler.setDocumentLocator(locator);
        }
        
        public void startDocument() throws SAXException {
            nextHandler.startDocument();
        }
        
        public void endDocument() throws SAXException {
            nextHandler.endDocument();
        }
    
        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            nextHandler.startElement(uri, loc, raw, attrs);
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
    
    public static class StartHandler extends DefaultHandler {
        
        private ContentHandler nextHandler;
        private Map<String, String> dtdMappings;
        
        
        public StartHandler(ContentHandler next, Map<String, String> dtdMappings) {
            nextHandler = next;
            this.dtdMappings = dtdMappings;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            nextHandler.setDocumentLocator(locator);
        }
        
        @Override
        public void startDocument() throws SAXException {
            nextHandler.startDocument();
        }
        
        @Override
        public void endDocument() throws SAXException {
            nextHandler.endDocument();
        }

        @Override
        public void startElement(String uri, String loc, String raw, Attributes attrs) throws SAXException {
            nextHandler.startElement(uri, loc, raw, attrs);
        }

        @Override
        public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            nextHandler.endElement(arg0, arg1, arg2);
        }

        @Override
        public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            nextHandler.startPrefixMapping(arg0, arg1);
        }

        @Override
        public void endPrefixMapping(String arg0) throws SAXException {
            nextHandler.endPrefixMapping(arg0);
        }

        @Override
        public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.characters(arg0, arg1, arg2);
        }

        @Override
        public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            nextHandler.ignorableWhitespace(arg0, arg1, arg2);
        }

        @Override
        public void processingInstruction(String arg0, String arg1) throws SAXException {
            nextHandler.processingInstruction(arg0, arg1);
        }

        @Override
        public void skippedEntity(String arg0) throws SAXException {
            nextHandler.skippedEntity(arg0);
        }
        
        @Override
        public InputSource resolveEntity(String publicId, String systemId) {
            if (dtdMappings != null && dtdMappings.containsKey(publicId)) {
                String dtdFile = dtdMappings.get(publicId);
                return new InputSource(ClassLoaderUtil.getResourceAsStream(dtdFile, DomHelper.class));
            } else {
                LOG.warn("Local DTD is missing for publicID: {} - defined mappings: {}", publicId, dtdMappings);
            }
            return null;
        }
        
        @Override
        public void warning(SAXParseException exception) {
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            LOG.error("{} at ({}:{}:{})", exception.getMessage(), exception.getPublicId(), exception.getLineNumber(), exception.getColumnNumber(), exception);
            throw exception;
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            LOG.fatal("{} at ({}:{}:{})", exception.getMessage(), exception.getPublicId(), exception.getLineNumber(), exception.getColumnNumber(), exception);
            throw exception;
        }
    }

}
