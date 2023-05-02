
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.providers.XmlHelper;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.DomHelper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.*;



public class DefaultValidatorFileParser implements ValidatorFileParser {

    private static Logger LOG = LogManager.getLogger(DefaultValidatorFileParser.class);

    static final String DEFAULT_MULTI_TEXTVALUE_SEPARATOR = " ";
    static final String MULTI_TEXTVALUE_SEPARATOR_CONFIG_KEY = "xwork.validatorfileparser.multi_textvalue_separator";

    private ObjectFactory objectFactory;
    private String multiTextvalueSeparator=DEFAULT_MULTI_TEXTVALUE_SEPARATOR;

    @Inject(value=MULTI_TEXTVALUE_SEPARATOR_CONFIG_KEY, required = false)
    public void setMultiTextvalueSeparator(String type) {
        multiTextvalueSeparator = type;
    }

    public String getMultiTextvalueSeparator() {
        return multiTextvalueSeparator;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    public List<ValidatorConfig> parseActionValidatorConfigs(ValidatorFactory validatorFactory, InputStream is, final String resourceName) {
        List<ValidatorConfig> validatorCfgs = new ArrayList<>();

        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);

        Map<String, String> dtdMappings = new HashMap<>();
        dtdMappings.put("-
        dtdMappings.put("-
        dtdMappings.put("-
        dtdMappings.put("-

        Document doc = DomHelper.parse(in, dtdMappings);

        if (doc != null) {
            NodeList fieldNodes = doc.getElementsByTagName("field");

            
            
            
            {
                NodeList validatorNodes = doc.getElementsByTagName("validator");
                addValidatorConfigs(validatorFactory, validatorNodes, new HashMap<String, Object>(), validatorCfgs);
            }

            for (int i = 0; i < fieldNodes.getLength(); i++) {
                Element fieldElement = (Element) fieldNodes.item(i);
                String fieldName = fieldElement.getAttribute("name");
                Map<String, Object> extraParams = new HashMap<String, Object>();
                extraParams.put("fieldName", fieldName);

                NodeList validatorNodes = fieldElement.getElementsByTagName("field-validator");
                addValidatorConfigs(validatorFactory, validatorNodes, extraParams, validatorCfgs);
            }
        }

        return validatorCfgs;
    }


    public void parseValidatorDefinitions(Map<String, String> validators, InputStream is, String resourceName) {

        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);

        Map<String, String> dtdMappings = new HashMap<>();
        dtdMappings.put("-
        dtdMappings.put("-

        Document doc = DomHelper.parse(in, dtdMappings);

        if (doc != null) {
            NodeList nodes = doc.getElementsByTagName("validator");

            for (int i = 0; i < nodes.getLength(); i++) {
                Element validatorElement = (Element) nodes.item(i);
                String name = validatorElement.getAttribute("name");
                String className = validatorElement.getAttribute("class");

                try {
                    
                    objectFactory.buildValidator(className, new HashMap<String, Object>(), ActionContext.getContext().getContextMap());
                    validators.put(name, className);
                } catch (Exception e) {
                    throw new ConfigurationException("Unable to load validator class " + className, e, validatorElement);
                }
            }
        }
    }

    
    public String getTextValue(Element valueEle) {
        StringBuilder value = new StringBuilder();
        NodeList nl = valueEle.getChildNodes();
        boolean firstCDataFound = false;
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                final String nodeValue = item.getNodeValue();
                if (nodeValue != null) {
                    if (firstCDataFound) {
                        value.append(getMultiTextvalueSeparator());
                    } else {
                        firstCDataFound = true;
                    }
                    value.append(nodeValue.trim());
                }
            }
        }
        return value.toString().trim();
    }

    private void addValidatorConfigs(ValidatorFactory factory, NodeList validatorNodes, Map<String, Object> extraParams, List<ValidatorConfig> validatorCfgs) {
        for (int j = 0; j < validatorNodes.getLength(); j++) {
            Element validatorElement = (Element) validatorNodes.item(j);
            String validatorType = validatorElement.getAttribute("type");
            Map<String, Object> params = new HashMap<String, Object>(extraParams);

            params.putAll(XmlHelper.getParams(validatorElement));

            
            try {
                factory.lookupRegisteredValidatorType(validatorType);
            } catch (IllegalArgumentException ex) {
                throw new ConfigurationException("Invalid validation type: " + validatorType, validatorElement);
            }

            ValidatorConfig.Builder vCfg = new ValidatorConfig.Builder(validatorType)
                    .addParams(params)
                    .location(DomHelper.getLocationObject(validatorElement))
                    .shortCircuit(Boolean.valueOf(validatorElement.getAttribute("short-circuit")));

            NodeList messageNodes = validatorElement.getElementsByTagName("message");
            Element messageElement = (Element) messageNodes.item(0);

            final Node defaultMessageNode = messageElement.getFirstChild();
            String defaultMessage = (defaultMessageNode == null) ? "" : defaultMessageNode.getNodeValue();
            vCfg.defaultMessage(defaultMessage);

            Map<String, String> messageParams = XmlHelper.getParams(messageElement);
            String key = messageElement.getAttribute("key");


            if ((key != null) && (key.trim().length() > 0)) {
                vCfg.messageKey(key);

                
                
                
                
                
                
                
                
                
                

                if (messageParams.containsKey("defaultMessage")) {
                    vCfg.defaultMessage(messageParams.get("defaultMessage"));
                }

                
                
                TreeMap<Integer, String> sortedMessageParameters = new TreeMap<Integer, String>();
                for (Map.Entry<String, String> messageParamEntry : messageParams.entrySet()) {

                    try {
                        int _order = Integer.parseInt(messageParamEntry.getKey());
                        sortedMessageParameters.put(_order, messageParamEntry.getValue());
                    }
                    catch (NumberFormatException e) {
                        
                    }
                }
                vCfg.messageParams(sortedMessageParameters.values().toArray(new String[sortedMessageParameters.values().size()]));
            } else {
                if (messageParams != null && (messageParams.size() > 0)) {
                    
                    
                    if (LOG.isWarnEnabled()) {
                	LOG.warn("validator of type ["+validatorType+"] have i18n message parameters defined but no i18n message key, it's parameters will be ignored");
                    }
                }
            }

            validatorCfgs.add(vCfg.build());
        }
    }
}
