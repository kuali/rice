/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.xmlrouting.XPathHelper;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * Parses {@link RuleAttribute}s from XML.
 *
 * @see RuleAttribute
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleAttributeXmlParser implements XmlConstants {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleAttributeXmlParser.class);

    
    private static final String XPATH_RULE_ATTRIBUTES = "//ruleAttributes/ruleAttribute";
	private static final String NAME = "name";
	private static final String CLASS_NAME = "className";
	private static final String LABEL = "label";
	private static final String DESCRIPTION = "description";
	private static final String TYPE = "type";
	private static final String ROUTING_CONFIG = "routingConfig";
	private static final String SEARCHING_CONFIG = "searchingConfig";
	private static final String SEARCH_RESULT_CONFIG = "searchResultConfig";
	
	public List parseRuleAttributes(InputStream input) throws IOException, InvalidXmlException {
		try {
			Element root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input)).getDocumentElement();
			return parseRuleAttributes(root);
		} catch (Exception e) {
			throw new InvalidXmlException("error parsing xml data", e);
		}
	}
	
	public List parseRuleAttributes(Element element) throws InvalidXmlException {
		List ruleAttributes = new ArrayList();
		try {
			XPath xpath = XPathHelper.newXPath();
			NodeList nodeList = (NodeList)xpath.evaluate(XPATH_RULE_ATTRIBUTES, element, XPathConstants.NODESET);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node ruleAttributeNode = nodeList.item(i);
				ruleAttributes.add(parseRuleAttribute(ruleAttributeNode));
			}
			
			for (Iterator iterator = ruleAttributes.iterator(); iterator.hasNext();) {
				RuleAttribute ruleAttribute = (RuleAttribute) iterator.next();
				try {
                    RuleAttribute existingAttribute = KEWServiceLocator.getRuleAttributeService().findByName(ruleAttribute.getName());
                    if (existingAttribute != null) {
                        ruleAttribute.setRuleAttributeId(existingAttribute.getRuleAttributeId());
                        ruleAttribute.setLockVerNbr(existingAttribute.getLockVerNbr());
                    }
				    KEWServiceLocator.getRuleAttributeService().save(ruleAttribute);
				} catch (Exception e) {
	                LOG.error("Error saving rule attribute entered by XML", e);
				}
			}
		} catch (XPathExpressionException e1) {
			throw new InvalidXmlException("Could not find a rule attribute.", e1);
		}
		return ruleAttributes;
	}
	
	private RuleAttribute parseRuleAttribute(Node ruleAttributeNode) throws InvalidXmlException {
		String name = "";
		String className = "";
		String label = "";
		String description = "";
		String type = "";
		String messageEntity = null;
		Node xmlConfig = null;
		for (int i = 0; i < ruleAttributeNode.getChildNodes().getLength(); i++) {
			Node childNode = ruleAttributeNode.getChildNodes().item(i);
			if(NAME.equals(childNode.getNodeName())){
				name = childNode.getFirstChild().getNodeValue();
			} else if(CLASS_NAME.equals(childNode.getNodeName())){
				className = childNode.getFirstChild().getNodeValue();
			} else if(LABEL.equals(childNode.getNodeName())){
				label = childNode.getFirstChild().getNodeValue();
			} else if(DESCRIPTION.equals(childNode.getNodeName())){
				description = childNode.getFirstChild().getNodeValue();
			} else if(TYPE.equals(childNode.getNodeName())){
				type = childNode.getFirstChild().getNodeValue();
			} else if(ROUTING_CONFIG.equals(childNode.getNodeName()) || SEARCHING_CONFIG.equals(childNode.getNodeName()) || 
					SEARCH_RESULT_CONFIG.equals(childNode.getNodeName())){
				xmlConfig = childNode;
			} else if (MESSAGE_ENTITY.equals(childNode.getNodeName())) {
				messageEntity = childNode.getFirstChild().getNodeValue();
			}
		}
		if (Utilities.isEmpty(name)) {
			throw new InvalidXmlException("RuleAttribute must have a name");
		}
		if (Utilities.isEmpty(className)) {
			throw new InvalidXmlException("RuleAttribute must have a className");
		}
		if (Utilities.isEmpty(label)) {
			LOG.warn("Label empty defaulting to name");
			label = name;
		}
		if (Utilities.isEmpty(type)) {
			LOG.debug("No type specified, default to " + EdenConstants.RULE_ATTRIBUTE_TYPE);
			type = EdenConstants.RULE_ATTRIBUTE_TYPE;
			//throw new InvalidXmlException("RuleAttribute must have an attribute type");
		}
		RuleAttribute ruleAttribute = new RuleAttribute();
		ruleAttribute.setName(name);
		ruleAttribute.setClassName(className);
		ruleAttribute.setType(type);
		ruleAttribute.setLabel(label);
//		 default description to label
        if (StringUtils.isEmpty(description)) {
            description = label;
        }
		ruleAttribute.setDescription(description);
		ruleAttribute.setMessageEntity(messageEntity);
		
		if(xmlConfig != null){
			try {
				ruleAttribute.setXmlConfigData(XmlHelper.writeNode(xmlConfig));
			} catch (TransformerException e) {
				throw new InvalidXmlException("XML config is invalid", e);
			}	
		} else {
			if(EdenConstants.RULE_XML_ATTRIBUTE_TYPE.equals(type)){
				throw new InvalidXmlException("A routing config must be present to be of type: "+type);
			} else if(EdenConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(type)){
				throw new InvalidXmlException("A searching config must be present to be of type: "+type);
			} else if(EdenConstants.SEARCH_RESULT_XML_PROCESSOR_ATTRIBUTE_TYPE.equals(type)){
				throw new InvalidXmlException("A searching config must be present to be of type: "+type);
			}
		}
		return ruleAttribute;
	}	
}