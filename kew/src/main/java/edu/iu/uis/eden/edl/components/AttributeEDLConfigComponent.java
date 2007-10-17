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
package edu.iu.uis.eden.edl.components;

import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.PropertyDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.edl.EDLContext;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;

/**
 * Populates workflow rule attributes associated with the current configElement.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AttributeEDLConfigComponent extends SimpleWorkflowEDLConfigComponent {


	public List getMatchingParams(Element originalConfigElement, RequestParser requestParser, EDLContext edlContext) {
		List matchingParams = super.getMatchingParams(originalConfigElement, requestParser, edlContext);
		String attributeName = originalConfigElement.getAttribute("attributeName");
		String attributePropertyName = originalConfigElement.getAttribute("name");

		WorkflowDocument document = (WorkflowDocument)requestParser.getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);

//				 clear attribute content so that duplicate attribute values are not added during submission of a new EDL form values version
		        document.clearAttributeContent();

				WorkflowAttributeDefinitionVO attributeDef = getWorkflowAttributeDefinitionVO(attributeName, document);
				for (Iterator iter = matchingParams.iterator(); iter.hasNext();) {
					MatchingParam param = (MatchingParam) iter.next();
					PropertyDefinitionVO property = attributeDef.getProperty(attributePropertyName);
					//if the prop doesn't exist create it and add it to the definition otherwise update the property value
					if (property == null) {
						property = new PropertyDefinitionVO(attributePropertyName, param.getParamValue());
						attributeDef.addProperty(property);
					} else {
						property.setValue(param.getParamValue());
					}
				}

		try {
			// validate if they are taking an action on the document (i.e. it's annotatable)
			String action = requestParser.getParameterValue(WorkflowDocumentActions.USER_ACTION_REQUEST_KEY);
			if (EDLXmlUtils.isValidatableAction(action)) {
				WorkflowAttributeValidationErrorVO[] errors = document.validateAttributeDefinition(attributeDef);
				if (errors.length > 0) {
					getEdlContext().setInError(true);
				}
				for (int index = 0; index < errors.length; index++) {
					WorkflowAttributeValidationErrorVO error = errors[index];
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

		return matchingParams;
	}

	private WorkflowAttributeDefinitionVO getWorkflowAttributeDefinitionVO(String attributeName, WorkflowDocument document) {
		for (int i = 0; i < document.getAttributeDefinitions().length; i++) {
			WorkflowAttributeDefinitionVO workflowAttributeDef = (WorkflowAttributeDefinitionVO)document.getAttributeDefinitions()[i];
			if (workflowAttributeDef.getAttributeName().equals(attributeName)) {
				return workflowAttributeDef;
			}
		}
		WorkflowAttributeDefinitionVO workflowAttributeDef = new WorkflowAttributeDefinitionVO(attributeName);
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