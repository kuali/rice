/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.workflow.tools.xml;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Attribute {

	private XmlGenHelper helper;

	public Attribute(XmlGenHelper helper) {
		this.helper = helper;
	}

	private String name;
	private String className;
	private String xmlConfigData;
	private List<String> fieldNames;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getXmlConfigData() {
		return xmlConfigData;
	}

	public void setXmlConfigData(String xmlConfigData) {
		this.xmlConfigData = xmlConfigData;
	}

	public List<String> getFieldNames() {
		if (fieldNames == null) {
			generateFieldNames();
		}
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	private void generateFieldNames() {
		fieldNames = new ArrayList<String>();
		try {
			if (!isBlank(xmlConfigData)) {
				XPath xpath = XPathFactory.newInstance().newXPath();
				NodeList fieldDefs = (NodeList)xpath.evaluate("/routingConfig/fieldDef", new InputSource(new ByteArrayInputStream(xmlConfigData.getBytes())), XPathConstants.NODESET);
				for (int index = 0; index <fieldDefs.getLength(); index++) {
					Element fieldDefElem = (Element)fieldDefs.item(index);
					String type = fieldDefElem.getAttribute("workflowType");
					if ("ALL".equals(type) || "RULE".equals(type)) {
						fieldNames.add(fieldDefElem.getAttribute("name"));
					}
				}
				return;
			} else if (helper != null) {
				List<String> resolvedFieldNames = helper.resolveFieldNames(this);
				if (resolvedFieldNames != null) {
					fieldNames.addAll(resolvedFieldNames);
				}
			}

//			Class<?> attributeClass = Class.forName(className);
//			if (!WorkflowAttribute.class.isAssignableFrom(attributeClass)) {
//				throw new RuntimeException("The attribute class '" + className + "' is not a valid instance of " + WorkflowAttribute.class.getName());
//			}
//			WorkflowAttribute workflowAttribute = (WorkflowAttribute)attributeClass.newInstance();
//			if (workflowAttribute instanceof GenericXMLRuleAttribute) {
//				if (isBlank(xmlConfigData)) {
//					throw new RuntimeException("No XML configuration was available on attribute '" + name + "'");
//				}
//				RuleAttribute ruleAttribute = new RuleAttribute();
//				ruleAttribute.setClassName(className);
//				ruleAttribute.setName(name);
//				ruleAttribute.setXmlConfigData(xmlConfigData);
//				((GenericXMLRuleAttribute)workflowAttribute).setRuleAttribute(ruleAttribute);
//			}
//			List<Row> rows = workflowAttribute.getRuleRows();
//			for (Row row : rows) {
//				for (Field field : row.getFields()) {
//					fieldNames.add(field.getPropertyName());
//				}
//			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected boolean isBlank(String string) {
		return string == null || string.trim().equals("");
	}

}
