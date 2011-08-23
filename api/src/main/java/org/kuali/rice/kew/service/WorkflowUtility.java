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
package org.kuali.rice.kew.service;

import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWWebServiceConstants;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * A remotable service which provides an API for performing various queries and
 * other utilities on KEW.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KEWWebServiceConstants.WorkflowUtility.WEB_SERVICE_NAME, targetNamespace = KEWWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface WorkflowUtility {
	
	public DocumentSearchResultDTO performDocumentSearch(
			@WebParam(name = "criteriaVO") DocumentSearchCriteriaDTO criteriaVO)
			throws WorkflowException;

	public DocumentSearchResultDTO performDocumentSearchWithPrincipal(
			@WebParam(name = "principalId") String principalId,
			@WebParam(name = "criteriaVO") DocumentSearchCriteriaDTO criteriaVO)
			throws WorkflowException;

}
