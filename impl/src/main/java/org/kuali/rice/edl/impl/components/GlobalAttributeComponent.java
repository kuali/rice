/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.edl.impl.components;

import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

import org.kuali.rice.edl.impl.EDLContext;
import org.kuali.rice.edl.impl.EDLModelComponent;
import org.kuali.rice.edl.impl.RequestParser;
import org.kuali.rice.kew.dto.PropertyDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Executes validations and generates XML for workflow attributes that are defined on the EDL Definitions.
 * These attribute definitions exist in a form similiar to the following:
 *
 * <attributes>
 *   <attribute name="AccountAttribute">
 *     <field edlField="finCoaCd" attributeField="finCoaCd"/>
 *     <field edlField="accountNbr" attributeField="accountNbr"/>
 *     <field edlField="totalDollarAmount" attributeField="totalDollarAmount"/>
 *   </attribute>
 * </attributes>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class GlobalAttributeComponent extends SimpleWorkflowEDLConfigComponent implements EDLModelComponent  {

	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
	    //String action = edlContext.getRequestParser().getParameterValueAsString(WorkflowDocumentActions.USER_ACTION_REQUEST_KEY);
	    // we don't want to clear the attribute content if they are just opening up the document to view it!
	    if (!edlContext.getUserAction().isLoadAction()) {
		RequestParser requestParser = edlContext.getRequestParser();
		try {
			WorkflowDocument document = (WorkflowDocument)requestParser.getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);
			//			 clear attribute content so that duplicate attribute values are not added during submission of a new EDL form values version
			document.clearAttributeContent();
			Document edlDef = KEWServiceLocator.getEDocLiteService().getDefinitionXml(edlContext.getEdocLiteAssociation());
			XPath xpath = XPathHelper.newXPath(edlDef);
			NodeList attributeNodes = (NodeList)xpath.evaluate("/edl/attributes/attribute", edlDef, XPathConstants.NODESET);
			for (int index = 0; index < attributeNodes.getLength(); index++) {
				Element attributeElem = (Element)attributeNodes.item(index);
				String attributeName = attributeElem.getAttribute("name");


				WorkflowAttributeDefinitionDTO attributeDef = getWorkflowAttributeDefinitionVO(attributeName, document);

				NodeList fieldNodes = (NodeList)xpath.evaluate("./field", attributeElem, XPathConstants.NODESET);
				for (int fIndex = 0; fIndex < fieldNodes.getLength(); fIndex++) {
					Element fieldElem = (Element)fieldNodes.item(fIndex);
					String edlField = fieldElem.getAttribute("edlField");
					String attributeField = fieldElem.getAttribute("attributeField");
					PropertyDefinitionDTO property = attributeDef.getProperty(attributeField);
					String value = requestParser.getParameterValue(edlField);
					if (property == null) {
						property = new PropertyDefinitionDTO(attributeField, value);
						attributeDef.addProperty(property);
					} else {
						property.setValue(value);
					}
				}
				// validate if they are taking an action on the document (i.e. it's annotatable)
				boolean curAttrValid = true;
				if (edlContext.getUserAction().isValidatableAction()) {
					WorkflowAttributeValidationErrorDTO[] errors = document.validateAttributeDefinition(attributeDef);
					if (errors.length > 0) {
						edlContext.setInError(true);
						curAttrValid = false;
					}
					Map<String, String> fieldErrors = (Map<String, String>)edlContext.getRequestParser().getAttribute(RequestParser.GLOBAL_FIELD_ERRORS_KEY);
					for (int atIndex = 0; atIndex < errors.length; atIndex++) {
						WorkflowAttributeValidationErrorDTO error = errors[atIndex];
						fieldErrors.put(error.getKey(), error.getMessage());
					}
				}
				

				if(curAttrValid){
					document.addAttributeDefinition(attributeDef );
					for (int fIndex = 0; fIndex < fieldNodes.getLength(); fIndex++) {
						Element fieldElem = (Element)fieldNodes.item(fIndex);
						String edlField = fieldElem.getAttribute("edlField");
						String attributeField = fieldElem.getAttribute("attributeField");
						PropertyDefinitionDTO property = attributeDef.getProperty(attributeField);
						String value = requestParser.getParameterValue(edlField);
						if (property == null) {
							property = new PropertyDefinitionDTO(attributeField, value);
							attributeDef.addProperty(property);
						} else {
							property.setValue(value);
						}
					}
				}

			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException)e;
			}
			throw new WorkflowRuntimeException("Failed to process attribute.", e);
		}
	    }
	}

	private WorkflowAttributeDefinitionDTO getWorkflowAttributeDefinitionVO(String attributeName, WorkflowDocument document) {
		for (int i = 0; i < document.getAttributeDefinitions().length; i++) {
			WorkflowAttributeDefinitionDTO workflowAttributeDef = (WorkflowAttributeDefinitionDTO)document.getAttributeDefinitions()[i];
			if (workflowAttributeDef.getAttributeName().equals(attributeName)) {
				return workflowAttributeDef;
			}
		}
		WorkflowAttributeDefinitionDTO workflowAttributeDef = new WorkflowAttributeDefinitionDTO(attributeName);
		return workflowAttributeDef;
	}

}
