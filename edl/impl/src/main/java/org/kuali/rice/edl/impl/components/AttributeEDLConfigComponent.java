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
package org.kuali.rice.edl.impl.components;

import java.util.Iterator;
import java.util.List;

import org.kuali.rice.edl.impl.EDLContext;
import org.kuali.rice.edl.impl.RequestParser;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.dto.PropertyDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.w3c.dom.Element;


/**
 * Populates workflow rule attributes associated with the current configElement.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeEDLConfigComponent extends SimpleWorkflowEDLConfigComponent {


	public List getMatchingParams(Element originalConfigElement, RequestParser requestParser, EDLContext edlContext) {
		List matchingParams = super.getMatchingParams(originalConfigElement, requestParser, edlContext);
		// we don't want to clear the attribute content if they are just opening up the document to view it!
		if (!edlContext.getUserAction().isLoadAction()) {
		    String attributeName = originalConfigElement.getAttribute("attributeName");
		    String attributePropertyName = originalConfigElement.getAttribute("name");

		    WorkflowDocument document = (WorkflowDocument)requestParser.getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);
		    // clear attribute content so that duplicate attribute values are not added during submission of a new EDL form values version
		    document.clearAttributeContent();

		    WorkflowAttributeDefinitionDTO attributeDef = getWorkflowAttributeDefinitionVO(attributeName, document);
		    for (Iterator iter = matchingParams.iterator(); iter.hasNext();) {
			MatchingParam param = (MatchingParam) iter.next();
			PropertyDefinitionDTO property = attributeDef.getProperty(attributePropertyName);
			//if the prop doesn't exist create it and add it to the definition otherwise update the property value
			if (property == null) {
			    property = new PropertyDefinitionDTO(attributePropertyName, param.getParamValue());
			    attributeDef.addProperty(property);
			} else {
			    property.setValue(param.getParamValue());
			}
		    }

		    try {
			// validate if they are taking an action on the document (i.e. it's annotatable)
			if (edlContext.getUserAction().isValidatableAction()) {
			    WorkflowAttributeValidationErrorDTO[] errors = document.validateAttributeDefinition(attributeDef);
			    if (errors.length > 0) {
				getEdlContext().setInError(true);
			    }
			    for (int index = 0; index < errors.length; index++) {
				WorkflowAttributeValidationErrorDTO error = errors[index];
				MatchingParam param = getMatchingParam(matchingParams, error.getKey());
				// if it doesn't match a param, then this is a global error
				if (param == null) {
				    List globalErrors = (List)getEdlContext().getRequestParser().getAttribute(RequestParser.GLOBAL_ERRORS_KEY);
				    globalErrors.add(error.getMessage());
				} else {
				    param.setError(Boolean.TRUE);
				    param.setErrorMessage(error.getMessage());
				}
			    }
			}
		    } catch (WorkflowException e) {
			throw new WorkflowRuntimeException("Encountered an error when validating form.", e);
		    }
		}
		return matchingParams;
	}

	private WorkflowAttributeDefinitionDTO getWorkflowAttributeDefinitionVO(String attributeName, WorkflowDocument document) {
		for (int i = 0; i < document.getAttributeDefinitions().length; i++) {
			WorkflowAttributeDefinitionDTO workflowAttributeDef = (WorkflowAttributeDefinitionDTO)document.getAttributeDefinitions()[i];
			if (workflowAttributeDef.getAttributeName().equals(attributeName)) {
				return workflowAttributeDef;
			}
		}
		WorkflowAttributeDefinitionDTO workflowAttributeDef = new WorkflowAttributeDefinitionDTO(attributeName);
		document.addAttributeDefinition(workflowAttributeDef);
		return workflowAttributeDef;
	}

	private MatchingParam getMatchingParam(List matchingParams, String name) {
		for (Iterator iterator = matchingParams.iterator(); iterator.hasNext();) {
			MatchingParam param = (MatchingParam) iterator.next();
			if (param.getParamName().equals(name)) {
				return param;
			}
		}
		return null;
	}
}
