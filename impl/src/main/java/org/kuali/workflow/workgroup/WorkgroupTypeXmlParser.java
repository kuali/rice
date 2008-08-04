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
package org.kuali.workflow.workgroup;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Parses {@link WorkgroupType}s from XML.
 *
 * @see WorkgroupType
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkgroupTypeXmlParser implements XmlConstants {

	private WorkgroupTypeService workgroupTypeService;

	public List<WorkgroupType> parseWorkgroupTypes(InputStream input) throws IOException, InvalidXmlException {

		try {
			Document doc = XmlHelper.trimSAXXml(input);
			Element root = doc.getRootElement();
			return parseWorkgroupTypes(root);
		} catch (JDOMException e) {
			throw new InvalidXmlException("Parse error.", e);
		} catch (SAXException e){
			throw new InvalidXmlException("Parse error.",e);
		} catch(ParserConfigurationException e){
			throw new InvalidXmlException("Parse error.",e);
		}
	}

	public List<WorkgroupType> parseWorkgroupTypes(Element element) throws InvalidXmlException {
		List<WorkgroupType> workgroupTypes = new ArrayList<WorkgroupType>();

		Vector workgroupTypesElements = XmlHelper.findElements(element, WORKGROUP_TYPES);
		for (Iterator iterator = workgroupTypesElements.iterator(); iterator.hasNext();) {
			Element workgroupTypesElement = (Element) iterator.next();
			Vector workgroupTypeElements = XmlHelper.findElements(workgroupTypesElement, WORKGROUP_TYPE);
			for (Iterator typeIt = workgroupTypeElements.iterator(); typeIt.hasNext();) {
				workgroupTypes.add(parseWorkgroupType((Element) typeIt.next()));
			}
		}
		return workgroupTypes;
	}

	private WorkgroupType parseWorkgroupType(Element element) throws InvalidXmlException {
		String name = element.getChildText(NAME, WORKGROUP_TYPE_NAMESPACE);
		String label = element.getChildText(LABEL, WORKGROUP_TYPE_NAMESPACE);
		String description = element.getChildText(DESCRIPTION, WORKGROUP_TYPE_NAMESPACE);
		String documentTypeName = element.getChildText(DOCUMENT_TYPE, WORKGROUP_TYPE_NAMESPACE);
		if (Utilities.isEmpty(name)) {
			throw new InvalidXmlException("WorkgroupType must have a name");
		}
		if (Utilities.isEmpty(label)) {
			label = name;
		}
		WorkgroupType workgroupType = new WorkgroupType();
		WorkgroupType existingType = workgroupTypeService.findByName(name);
		if (existingType != null) {
			workgroupType = existingType;
		}
		workgroupType.setName(name);
		workgroupType.setLabel(label);
		workgroupType.setDescription(description);
		workgroupType.setDocumentTypeName(documentTypeName);

		Element attributesElement = element.getChild(ATTRIBUTES, WORKGROUP_TYPE_NAMESPACE);
		if (attributesElement != null) {
            List<WorkgroupTypeAttribute> attributes = parseAttributes(attributesElement, workgroupType);
            updateAttributes(workgroupType, attributes);
		}

		return workgroupType;
	}

	private List<WorkgroupTypeAttribute> parseAttributes(Element element, WorkgroupType workgroupType) throws InvalidXmlException {
		List<WorkgroupTypeAttribute> attributes = new ArrayList<WorkgroupTypeAttribute>();
		Vector attributeElements = XmlHelper.findElements(element, ATTRIBUTE);
		for (Iterator iterator = attributeElements.iterator(); iterator.hasNext();) {
			attributes.add(parseAttribute((Element) iterator.next(), workgroupType));
		}
		return attributes;
	}

	private WorkgroupTypeAttribute parseAttribute(Element element, WorkgroupType workgroupType) throws InvalidXmlException {
		String attributeName = element.getChildText(NAME, WORKGROUP_TYPE_NAMESPACE);
		if (Utilities.isEmpty(attributeName)) {
			throw new InvalidXmlException("Attribute name must be non-empty on for Workgroup Type '" + workgroupType.getName() + "'");
		}
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(attributeName);
		if (ruleAttribute == null) {
			throw new InvalidXmlException("Could not locate rule attribute for name '" + attributeName + "'");
		}
		WorkgroupTypeAttribute attribute = new WorkgroupTypeAttribute();
		attribute.setAttribute(ruleAttribute);
		attribute.setWorkgroupType(workgroupType);
		return attribute;
	}

	protected void updateAttributes(WorkgroupType workgroupType, List<WorkgroupTypeAttribute> newAttributes) {
		List<WorkgroupTypeAttribute> activeAttributes = new ArrayList<WorkgroupTypeAttribute>();
		List<WorkgroupTypeAttribute> inactiveAttributes = new ArrayList<WorkgroupTypeAttribute>();
		for (WorkgroupTypeAttribute newAttribute : newAttributes) {
			// determine if this is an existing attribute
			boolean isExistingAttribute = false;
			for (WorkgroupTypeAttribute existingAttribute : workgroupType.getAttributes()) {
				if (existingAttribute.getAttribute().getName().equals(newAttribute.getAttribute().getName())) {
					existingAttribute.setActive(true);
					activeAttributes.add(existingAttribute);
					isExistingAttribute = true;
				}
			}
			if (!isExistingAttribute) {
				newAttribute.setActive(true);
				activeAttributes.add(newAttribute);
			}
		}
		// find attributes that are no longer on the workgroup type and mark them as inactive
		outer:for (WorkgroupTypeAttribute existingAttribute : workgroupType.getAttributes()) {
			for (WorkgroupTypeAttribute activeAttribute : activeAttributes) {
				if (existingAttribute.getAttribute().getName().equals(activeAttribute.getAttribute().getName())) {
					continue outer;
				}
			}
			existingAttribute.setActive(false);
			inactiveAttributes.add(existingAttribute);
		}

		// clear out the current attributes and add the new ones
		workgroupType.getAttributes().clear();
		workgroupType.getAttributes().addAll(activeAttributes);
		workgroupType.getAttributes().addAll(inactiveAttributes);
		int orderIndex = 0;
		for (WorkgroupTypeAttribute attribute : workgroupType.getAttributes()) {
			attribute.setOrderIndex(orderIndex++);
		}
	}

	public WorkgroupTypeService getWorkgroupTypeService() {
		return workgroupTypeService;
	}

	public void setWorkgroupTypeService(WorkgroupTypeService workgroupTypeService) {
		this.workgroupTypeService = workgroupTypeService;
	}

}
