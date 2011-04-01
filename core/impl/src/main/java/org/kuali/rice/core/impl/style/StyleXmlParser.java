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
package org.kuali.rice.core.impl.style;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.impex.xml.XmlConstants;
import org.kuali.rice.core.api.impex.xml.XmlIngestionException;
import org.kuali.rice.core.api.style.StyleService;
import org.kuali.rice.core.framework.impex.xml.XmlLoader;
import org.kuali.rice.core.util.XmlJotter;
import org.kuali.rice.core.xml.XmlException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Parser for Style content type, managed by StyleService
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StyleXmlParser implements XmlLoader {
	private static final Logger LOG = Logger.getLogger(StyleXmlParser.class);

	private StyleService styleService;
	
    private static ThreadLocal<DocumentBuilder> DOCUMENT_BUILDER = new ThreadLocal<DocumentBuilder>() {
        protected DocumentBuilder initialValue() {
            try {
                return DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                // well folks, there is not much we can do if we get a ParserConfigurationException
                // so might as well isolate the evilness here, and just balk if this occurs
                String message = "Error obtaining document builder";
                LOG.error(message, pce);
                throw new RuntimeException(message, pce);
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

    public void loadXml(InputStream inputStream, String principalId) {
        DocumentBuilder db = getDocumentBuilder();
        XPath xpath = XPathFactory.newInstance().newXPath();
        Document doc;
        try {
            doc = db.parse(inputStream);
        } catch (Exception e) {
            throw generateException("Error parsing Style XML file", e);
        }
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
                            StyleBo style = parseStyle(e);
                            styleService.saveStyle(StyleBo.to(style));
                        }
                    }
                }
            }
    }
    /**
     * Parses an EDocLiteStyle
     *
     * @param e
     *            element to parse
     * @return an EDocLiteStyle
     */
    private static StyleBo parseStyle(Element e) {
        String name = e.getAttribute("name");
        if (name == null || name.length() == 0) {
            throw generateMissingAttribException(XmlConstants.STYLE_STYLE, "name");
        }
        StyleBo style = new StyleBo();
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

    private static XmlIngestionException generateMissingAttribException(String element, String attrib) {
        return generateException("Style '" + element + "' element must contain a '" + attrib + "' attribute", null);
    }

    private static XmlIngestionException generateMissingChildException(String element, String child) {
        return generateException("Style '" + element + "' element must contain a '" + child + "' child element", null);
    }

    private static XmlIngestionException generateSerializationException(String element, XmlException cause) {
        return generateException("Error serializing Style '" + element + "' element", cause);
    }
    
    private static XmlIngestionException generateException(String error, Throwable cause) {
    	return new XmlIngestionException(error, cause);
    }
    
    public void setStyleService(StyleService styleService) {
    	this.styleService = styleService;
    }

}
