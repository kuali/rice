/*
 * Copyright 2005-2008 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.xml;

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.XmlJotter;
import org.kuali.rice.core.xml.XmlException;
import org.kuali.rice.kew.edl.EDLXmlUtils;
import org.kuali.rice.kew.edl.bo.EDocLiteAssociation;
import org.kuali.rice.kew.edl.bo.EDocLiteDefinition;
import org.kuali.rice.kew.edl.bo.EDocLiteStyle;
import org.kuali.rice.kew.edl.service.EDocLiteService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


/**
 * An XML parser which parses EDocLite definitions.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EDocLiteXmlParser {

	private static final Logger LOG = Logger.getLogger(EDocLiteXmlParser.class);

    public static void loadXml(InputStream inputStream, String principalId) {
        DocumentBuilder db = EDLXmlUtils.getDocumentBuilder();
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        // parse and save EDocLiteDefinition, EDocLiteStyle, or EDocLiteAssociation xml from to-be-determined XML format
        //try {
        try {
            doc = db.parse(inputStream);
        } catch (Exception e) {
            throw generateException("Error parsing EDocLite XML file", e);
        }
            /*try {
                LOG.info(XmlHelper.writeNode(doc.getFirstChild(), true));
            } catch (TransformerException e) {
                LOG.warn("Error displaying document");
            }*/

            NodeList edls;
            try {
                edls = (NodeList) xpath.evaluate("//edoclite", doc.getFirstChild(), XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                throw generateException("Error evaluating XPath expression", e);
            }

            for (int i = 0; i < edls.getLength(); i++) {
                Node edl = edls.item(i);
                NodeList children = edl.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node node = children.item(j);
                    /*try {
                        LOG.info(XmlHelper.writeNode(node, true));
                    } catch (TransformerException te) {
                        LOG.warn("Error displaying node");
                    }*/
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) node;
                        if ("style".equals(node.getNodeName())) {
                            LOG.debug("Digesting EDocLiteStyle: " + e.getAttribute("name"));
                            EDocLiteStyle style = parseEDocLiteStyle(e);
                            getEDLService().saveEDocLiteStyle(style);
                        } else if ("edl".equals(node.getNodeName())) {
                            LOG.debug("Digesting EDocLiteDefinition: " + e.getAttribute("name"));
                            EDocLiteDefinition def = parseEDocLiteDefinition(e);
                            getEDLService().saveEDocLiteDefinition(def);
                        } else if ("association".equals(node.getNodeName())) {
                            LOG.debug("Digesting EDocLiteAssociation: " + e.getAttribute("name"));
                            EDocLiteAssociation assoc = parseEDocLiteAssociation(e);
                            getEDLService().saveEDocLiteAssociation(assoc);
                        } else {
                            // LOG.debug("Unrecognized element '" + node.getNodeName() + "' in EDocLite XML doc");
                        }
                    }
                }
            }
        //} catch (Exception e) {
        //    throw generateException("Error parsing EDocLite XML file", e);
        //}
    }

    private static WorkflowServiceErrorException generateException(String error, Throwable cause) {
        WorkflowServiceErrorException wsee = new WorkflowServiceErrorException(error, new WorkflowServiceErrorImpl(error, KEWConstants.XML_FILE_PARSE_ERROR));
        if (cause != null) {
            wsee.initCause(cause);
        }
        return wsee;
    }

    /**
     * Parses an EDocLiteAssocation
     *
     * @param e
     *            element to parse
     * @return an EDocLiteAssocation
     */
    private static EDocLiteAssociation parseEDocLiteAssociation(Element e) {
        String docType = EDLXmlUtils.getChildElementTextValue(e, "docType");
        if (docType == null) {
            throw generateMissingChildException("association", "docType");
        }
        EDocLiteAssociation assoc = new EDocLiteAssociation();
        assoc.setEdlName(docType);
        assoc.setDefinition(EDLXmlUtils.getChildElementTextValue(e, "definition"));
        assoc.setStyle(EDLXmlUtils.getChildElementTextValue(e, "style"));
        assoc.setActiveInd(Boolean.valueOf(EDLXmlUtils.getChildElementTextValue(e, "active")));
        return assoc;
    }

    /**
     * Parses an EDocLiteStyle
     *
     * @param e
     *            element to parse
     * @return an EDocLiteStyle
     */
    private static EDocLiteStyle parseEDocLiteStyle(Element e) {
        String name = e.getAttribute("name");
        if (name == null || name.length() == 0) {
            throw generateMissingAttribException("style", "name");
        }
        EDocLiteStyle style = new EDocLiteStyle();
        style.setName(name);
        Element stylesheet = null;
        NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            /*
             * LOG.debug("NodeName: " + child.getNodeName()); LOG.debug("LocalName: " + child.getLocalName()); LOG.debug("Prefix: " + child.getPrefix()); LOG.debug("NS URI: " + child.getNamespaceURI());
             */
            if (child.getNodeType() == Node.ELEMENT_NODE && "xsl:stylesheet".equals(child.getNodeName())) {
                stylesheet = (Element) child;
                break;
            }
        }
        if (stylesheet == null) {
            throw generateMissingChildException("style", "xsl:stylesheet");
        }
        try {
            style.setXmlContent(XmlJotter.jotNode(stylesheet, true));
        } catch (XmlException te) {
            throw generateSerializationException("style", te);
        }
        return style;
    }

    /**
     * Parses an EDocLiteDefinition
     *
     * @param e
     *            element to parse
     * @return an EDocLiteDefinition
     */
    private static EDocLiteDefinition parseEDocLiteDefinition(Element e) {
        EDocLiteDefinition def = new EDocLiteDefinition();
        String name = e.getAttribute("name");
        if (name == null || name.length() == 0) {
            throw generateMissingAttribException(EDLXmlUtils.EDL_E, "name");
        }
        def.setName(name);

        // do some validation to ensure that any attributes referenced actually exist
        // blow up if there is a problem

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList fields;
        try {
            fields = (NodeList) xpath.evaluate("fieldDef", e, XPathConstants.NODESET);
        } catch (XPathExpressionException xpee) {
            throw new RuntimeException("Invalid EDocLiteDefinition", xpee);
        }

        if (fields != null) {
            Collection invalidAttributes = new ArrayList(5);
            for (int i = 0; i < fields.getLength(); i++) {
                Node node = (Node) fields.item(i);
                // they should all be Element...
                if (node instanceof Element) {
                    Element field = (Element) node;
                    // rely on XML validation to ensure this is present
                    String fieldName = field.getAttribute("name");
                    String attribute = field.getAttribute("attributeName");
                    if (attribute != null && attribute.length() > 0) {
                        RuleAttribute ruleAttrib = KEWServiceLocator.getRuleAttributeService().findByName(attribute);
                        if (ruleAttrib == null) {
                            LOG.error("Invalid attribute referenced in EDocLite definition: " + attribute);
                            invalidAttributes.add("Attribute '" + attribute + "' referenced in field '" + fieldName + "' not found");
                        }
                    }
                }
            }
            if (invalidAttributes.size() > 0) {
                LOG.error("Invalid attributes referenced in EDocLite definition");
                StringBuffer message = new StringBuffer("EDocLite definition contains references to non-existent attributes;\n");
                Iterator it = invalidAttributes.iterator();
                while (it.hasNext()) {
                    message.append(it.next());
                    message.append("\n");
                }
                throw new RuntimeException(message.toString());
            }
        }

        try {
            def.setXmlContent(XmlJotter.jotNode(e, true));
        } catch (XmlException te) {
            throw generateSerializationException(EDLXmlUtils.EDL_E, te);
        }
        return def;
    }

    private static WorkflowServiceErrorException generateMissingAttribException(String element, String attrib) {
        return generateException("EDocLite '" + element + "' element must contain a '" + attrib + "' attribute", null);
    }

    private static WorkflowServiceErrorException generateMissingChildException(String element, String child) {
        return generateException("EDocLite '" + element + "' element must contain a '" + child + "' child element", null);
    }

    private static WorkflowServiceErrorException generateSerializationException(String element, XmlException cause) {
        return generateException("Error serializing EDocLite '" + element + "' element", cause);
    }

    private static EDocLiteService getEDLService() {
    	return KEWServiceLocator.getEDocLiteService();
    }
}
