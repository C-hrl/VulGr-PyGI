
package com.opensymphony.xwork2.config.providers;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.LinkedHashMap;
import java.util.Map;



public class XmlHelper {


    
    public static Map<String, String> getParams(Element paramsElement) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();

        if (paramsElement == null) {
            return params;
        }

        NodeList childNodes = paramsElement.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if ((childNode.getNodeType() == Node.ELEMENT_NODE) && "param".equals(childNode.getNodeName())) {
                Element paramElement = (Element) childNode;
                String paramName = paramElement.getAttribute("name");

                String val = getContent(paramElement);
                if (val.length() > 0) {
                    params.put(paramName, val);
                }
            }
        }

        return params;
    }

    
    public static String getContent(Element element) {
        StringBuilder paramValue = new StringBuilder();
        NodeList childNodes = element.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node currentNode = childNodes.item(j);
            if (currentNode != null && currentNode.getNodeType() == Node.TEXT_NODE) {
                String val = currentNode.getNodeValue();
                if (val != null) {
                    paramValue.append(val.trim());
                }
            }
        }
        return paramValue.toString().trim();
    }

    
     public static Integer getLoadOrder(Document doc) {
        Element rootElement = doc.getDocumentElement();
        String number = rootElement.getAttribute("order");
        if (StringUtils.isNotBlank(number)) {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {
                return Integer.MAX_VALUE;
            }
        } else {
            
            return Integer.MAX_VALUE;
        }
    }
}
