/*
 * Copyright 2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
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
package org.kuali.rice.kew.api.doctype;

import javax.jws.WebParam;


public interface DocumentTypeService {

	String getDocumentTypeIdByName(@WebParam(name = "documentTypeName") String documentTypeName);
	
	//DocumentType getDocumentType(@WebParam(name = "documentTypeId"), String documentTypeId);
	
	boolean isSuperUser(String principalId, String documentTypeId);
	
	// TODO add the following methods to this service
	
//	public DocumentTypeDTO getDocumentType(
//			@WebParam(name = "documentTypeId") Long documentTypeId)
//			throws WorkflowException;
//
//	public DocumentTypeDTO getDocumentTypeByName(
//			@WebParam(name = "documentTypeName") String documentTypeName)
//			throws WorkflowException;
//	
//	public boolean hasRouteNode(
//			@WebParam(name = "documentTypeName") String documentTypeName,
//			@WebParam(name = "routeNodeName") String routeNodeName)
//			throws WorkflowException;
//
//	public boolean isCurrentActiveDocumentType(
//			@WebParam(name = "documentTypeName") String documentTypeName)
//			throws WorkflowException;
	
}
