/*
 * Copyright 2007-2009 The Kuali Foundation
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
import org.kuali.rice.edl.impl.bo.EDocLiteStyle;
import org.kuali.rice.edl.impl.service.StyleService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.util.KEWConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;


/**
 * Parser for Style content type, managed by StyleService
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleXmlParser {
	private static final Logger LOG = Logger.getLogger(StyleXmlParser.class);

    private static ThreadLocal DOCUMENT_BUILDER = new ThreadLocal() {
        protected Object initialValue() {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                // well folks, there is not much we can do if we get a ParserConfigurationException
                // so might as well isolate the evilness here, and just balk if this occurs
                String message = "Error obtaining document builder";
                LOG.error(message, pce);
                return new RuntimeException(message, pce);
            }
        }
    };

    /**
     * Returns a valid DocumentBuilder
     * @return a valid DocumentBuilder
     */
    private static DocumentBuilder getDocumentBuilder() {
        return (DocumentBuilder) DOCUMENT_BUILDER.get();
    }

    public static void loadXml(StyleService styleService, InputStream inputStream, String principalId) {
        DocumentBuilder db = getDocumentBuilder();
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        // parse and save EDocLiteDefinition, EDocLiteStyle, or EDocLiteAssociation xml from to-be-determined XML format
        //try {
        try {
            doc = db.parse(inputStream);
        } catch (Exception e) {
            throw generateException("Error parsing Style XML file", e);
        }
            /*try {
                LOG.info(XmlHelper.writeNode(doc.getFirstChild(), true));
            } catch (TransformerException e) {
                LOG.warn("Error displaying document");
            }*/

            NodeList styles;
            try {
                styles = (NodeList) xpath.evaluate("//" + XmlConstants.STYLE_STYLES, doc.getFirstChild(), XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                throw generateException("Error evaluating XPath expression", e);
            }

            LOG.info("Styles: " + styles);
            for (int i = 0; i < styles.getLength(); i++) {
                Node edl = styles.item(i);
                NodeList children = edl.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node node = children.item(j);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) node;
                        if (XmlConstants.STYLE_STYLE.equals(node.getNodeName())) {
                            LOG.debug("Digesting style: " + e.getAttribute("name"));
                            EDocLiteStyle style = parseStyle(e);
                            styleService.saveStyle(style);
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
     * Parses an EDocLiteStyle
     *
     * @param e
     *            element to parse
     * @return an EDocLiteStyle
     */
    private static EDocLiteStyle parseStyle(Element e) {
        String name = e.getAttribute("name");
        if (name == null || name.length() == 0) {
            throw generateMissingAttribException(XmlConstants.STYLE_STYLE, "name");
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
            throw generateMissingChildException(XmlConstants.STYLE_STYLE, "xsl:stylesheet");
        }
        try {
            style.setXmlContent(XmlJotter.jotNode(stylesheet, true));
        } catch (XmlException te) {
            throw generateSerializationException(XmlConstants.STYLE_STYLE, te);
        }
        return style;
    }

    private static WorkflowServiceErrorException generateMissingAttribException(String element, String attrib) {
        return generateException("Style '" + element + "' element must contain a '" + attrib + "' attribute", null);
    }

    private static WorkflowServiceErrorException generateMissingChildException(String element, String child) {
        return generateException("Style '" + element + "' element must contain a '" + child + "' child element", null);
    }

    private static WorkflowServiceErrorException generateSerializationException(String element, XmlException cause) {
        return generateException("Error serializing Style '" + element + "' element", cause);
    }
}
