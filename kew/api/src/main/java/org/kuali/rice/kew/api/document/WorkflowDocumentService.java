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
package org.kuali.rice.kew.api.document;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionTaken;

import javax.jws.WebParam;
import java.util.List;

/**
 * TODO ... annotate for JAX-WS! 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface WorkflowDocumentService {
		
	Document getDocument(String documentId);
	
	boolean doesDocumentExist(String documentId);
	
	DocumentContent getDocumentContent(String documentId);
	
	List<ActionRequest> getRootActionRequests(@WebParam(name = "documentId") String documentId);

	List<ActionRequest> getActionRequests(String documentId, String nodeName, String principalId);
	
	List<ActionTaken> getActionsTaken(@WebParam(name = "documentId") String documentId);
	
    DocumentDetail getDocumentDetailByAppId(String documentTypeName, String appId);

/*	public RouteHeaderDTO getRouteHeaderWithPrincipal(
			@WebParam(name = "principalId") String principalId,
			@WebParam(name = "documentId") String documentId)
			throws WorkflowException;*/
//
//	public RouteHeaderDTO getRouteHeader(
//			@WebParam(name = "documentId") String documentId)
//			throws WorkflowException;
//

	DocumentDetail getDocumentDetail(@WebParam(name = "documentId") String documentId);

	RouteNodeInstance getNodeInstance(@WebParam(name = "nodeInstanceId") String nodeInstanceId);

	String getNewResponsibilityId();


	String getAppDocId(@WebParam(name = "documentId") String documentId);
//
//	
//	public DocumentSearchResultDTO performDocumentSearch(
//			@WebParam(name = "criteriaVO") DocumentSearchCriteriaDTO criteriaVO)
//			throws WorkflowException;
//
//	public DocumentSearchResultDTO performDocumentSearchWithPrincipal(
//			@WebParam(name = "principalId") String principalId,
//			@WebParam(name = "criteriaVO") DocumentSearchCriteriaDTO criteriaVO)
//			throws WorkflowException;
//

	List<RouteNodeInstance> getRouteNodeInstances(String documentId);
	
	List<RouteNodeInstance> getActiveRouteNodeInstances(
			@WebParam(name = "documentId") String documentId);

	List<RouteNodeInstance> getTerminalNodeInstances(
			@WebParam(name = "documentId") String documentId);

	List<String> getPreviousRouteNodeNames(@WebParam(name = "documentId") String documentId);

	
	String getDocumentStatus(@WebParam(name = "documentId") String documentId);

	List<RouteNodeInstance> getCurrentNodeInstances(@WebParam(name = "documentId") String documentId);

	List<String> getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(
		@WebParam(name = "actionRequestedCd") String actionRequestedCd,
		@WebParam(name = "documentId") String documentId);


	String getDocumentInitiatorPrincipalId(@WebParam(name = "documentId") String documentId);

	/**
	 * Returns the principal ID of the user who routed the given document.
	 * <b>null</b> if the document can not be found.
	 */
	public String getDocumentRoutedByPrincipalId(
			@WebParam(name = "documentId") String documentId);
//
//	@XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
//	public Map<String, String> getActionsRequested(
//			@WebParam(name = "principalId") String principalId,
//			@WebParam(name = "documentId") String documentId);
//
//	/**
//	 * 
//	 * This method does a direct search for the searchableAttribute without
//	 * going through the doc search.
//	 * 
//	 * @param documentId
//	 * @param key
//	 * @return
//	 */
//	public String[] getSearchableAttributeStringValuesByKey(
//			@WebParam(name = "documentId") String documentId,
//			@WebParam(name = "key") String key);
//
//	/**
//	 * 
//	 * This method does a direct search for the searchableAttribute without
//	 * going through the doc search.
//	 * 
//	 * @param documentId
//	 * @param key
//	 * @return
//	 */
//	@XmlJavaTypeAdapter(value = SqlTimestampAdapter.class)
//	public Timestamp[] getSearchableAttributeDateTimeValuesByKey(
//			@WebParam(name = "documentId") String documentId,
//			@WebParam(name = "key") String key);
//
//	/**
//	 * 
//	 * This method does a direct search for the searchableAttribute without
//	 * going through the doc search.
//	 * 
//	 * @param documentId
//	 * @param key
//	 * @return
//	 */
//	public BigDecimal[] getSearchableAttributeFloatValuesByKey(
//			@WebParam(name = "documentId") String documentId,
//			@WebParam(name = "key") String key);
//
//	/**
//	 * 
//	 * This method does a direct search for the searchableAttribute without
//	 * going through the doc search.
//	 * 
//	 * @param documentId
//	 * @param key
//	 * @return
//	 */
//	public Long[] getSearchableAttributeLongValuesByKey(
//			@WebParam(name = "documentId") String documentId,
//			@WebParam(name = "key") String key);
//
//	public String getFutureRequestsKey(
//			@WebParam(name = "principalId") String principalId);
//
//	public String getReceiveFutureRequestsValue();
//
//	public String getDoNotReceiveFutureRequestsValue();
//
//	public String getClearFutureRequestsValue();
//	
//	 public DocumentStatusTransitionDTO[] getDocumentStatusTransitionHistory(
//	    		@WebParam(name = "documentId") String documentId)
//	    		throws WorkflowException;
//
	
	/**
	 * TODO - document that this "ignores" the request to create the link if it already exists,
	 * returning the existing link if there is one
	 * 
	 * TODO - also document that this method actually creates two links in the db, one from the document being
	 * linked to the target and vice-versa
	 */
	DocumentLink addDocumentLink(DocumentLink documentLink) throws RiceIllegalArgumentException;

	DocumentLink deleteDocumentLink(String documentLinkId) throws RiceIllegalArgumentException;
	
    List<DocumentLink> deleteDocumentLinksByDocumentId(String originatingDocumentId) throws RiceIllegalArgumentException;
	    
    List<DocumentLink> getOutgoingDocumentLinks(String originatingDocumentId) throws RiceIllegalArgumentException;
    
    List<DocumentLink> getIncomingDocumentLinks(String originatingDocumentId) throws RiceIllegalArgumentException;
	    
    DocumentLink getDocumentLink(String documentLinkId) throws RiceIllegalArgumentException;

}
