/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch.xml;

import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kns.web.ui.Column;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchXMLResultProcessorImpl extends StandardDocumentSearchResultProcessor implements DocumentSearchXMLResultProcessor {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentSearchXMLResultProcessorImpl.class);

	private RuleAttribute ruleAttribute;
	private List<Column> customDisplayColumns = new ArrayList<Column>();

	public void setRuleAttribute(RuleAttribute ruleAttribute) {
		this.ruleAttribute = ruleAttribute;
	}

	@Override
	public List<Column> getCustomDisplayColumns() {
		List<Column> displayColumns = new ArrayList<Column>();
		if (customDisplayColumns.isEmpty()) {
			XPath xpath = XPathHelper.newXPath();
			String xPathExpression = "//searchResultConfig/column";
			try {
				NodeList nodes = (NodeList) xpath.evaluate(xPathExpression, getConfigXML(), XPathConstants.NODESET);
	            if (nodes == null) {
	                LOG.error("Could not find searching configuration columns (<searchResultConfig><column>) for this DocumentSearchXMLResultProcessor");
	            } else {
	    			for (int i = 0; i < nodes.getLength(); i++) {
	    				Node field = nodes.item(i);
	    				NamedNodeMap fieldAttributes = field.getAttributes();
	    				String key = (fieldAttributes.getNamedItem("name") != null) ? fieldAttributes.getNamedItem("name").getNodeValue().trim() : null;
	    				String title = (fieldAttributes.getNamedItem("title") != null) ? fieldAttributes.getNamedItem("title").getNodeValue().trim() : null;
	    				String sortable = (fieldAttributes.getNamedItem("sortable") != null) ? fieldAttributes.getNamedItem("sortable").getNodeValue().trim() : null;
	    				Column currentColumn = new Column(title,sortable,key);
	    				displayColumns.add(currentColumn);
	    			}
	    			customDisplayColumns = displayColumns;
	            }
			} catch (XPathExpressionException e) {
				LOG.error("error in getCustomDisplayColumns ", e);
				throw new RuntimeException("Error trying to find xml content with xpath expression: " + xPathExpression, e);
			} catch (Exception e) {
				LOG.error("error in getCustomDisplayColumns attempting to find xml custon columns", e);
				throw new RuntimeException("Error trying to get xml custom columns.", e);
			}
		}
		return customDisplayColumns;
	}

	@Override
	public boolean getShowAllStandardFields() {
		boolean returnValue = DEFAULT_SHOW_ALL_STANDARD_FIELDS_VALUE;
		XPath xpath = XPathHelper.newXPath();
		String findXpathExpressionPrefix = "//searchResultConfig";
		Node searchResultConfig;
		try {
			searchResultConfig = (Node) xpath.evaluate(findXpathExpressionPrefix, getConfigXML(), XPathConstants.NODE);
			if (searchResultConfig != null) {
				NamedNodeMap fieldAttributes = searchResultConfig.getAttributes();
				if (fieldAttributes.getNamedItem("showStandardSearchFields") != null) {
					returnValue = Boolean.valueOf(fieldAttributes.getNamedItem("showStandardSearchFields").getNodeValue());
				}
			}
		} catch (XPathExpressionException e) {
			LOG.error("error in getSearchContent ", e);
			throw new RuntimeException("Error trying to find xml content with xpath expression", e);
		}
		return returnValue;
	}

	@Override
	public boolean getOverrideSearchableAttributes() {
		boolean returnValue = DEFAULT_OVERRIDE_SEARCHABLE_ATTRIBUTES_VALUE;
		XPath xpath = XPathHelper.newXPath();
		String findXpathExpressionPrefix = "//searchResultConfig";
		Node searchResultConfig;
		try {
			searchResultConfig = (Node) xpath.evaluate(findXpathExpressionPrefix, getConfigXML(), XPathConstants.NODE);
			if (searchResultConfig != null) {
				NamedNodeMap fieldAttributes = searchResultConfig.getAttributes();
				if (fieldAttributes.getNamedItem("overrideSearchableAttributes") != null) {
					returnValue = Boolean.valueOf(fieldAttributes.getNamedItem("overrideSearchableAttributes").getNodeValue());
				}
			}
		} catch (XPathExpressionException e) {
			LOG.error("error in getSearchContent ", e);
			throw new RuntimeException("Error trying to find xml content with xpath expression", e);
		}
		return returnValue;
	}

	public Element getConfigXML() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new BufferedReader(new StringReader(ruleAttribute.getXmlConfigData())))).getDocumentElement();
		} catch (Exception e) {
			String ruleAttrStr = (ruleAttribute == null ? null : ruleAttribute.getName());
			LOG.error("error parsing xml data from search processor attribute: " + ruleAttrStr, e);
			throw new RuntimeException("error parsing xml data from search processor attribute: " + ruleAttrStr, e);
		}
	}
}
