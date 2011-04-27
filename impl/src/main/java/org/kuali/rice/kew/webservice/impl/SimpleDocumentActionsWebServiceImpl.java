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
package org.kuali.rice.kew.webservice.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.kew.dto.AdHocRevokeDTO;
import org.kuali.rice.kew.dto.NoteDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWWebServiceConstants;
import org.kuali.rice.kew.webservice.DocumentResponse;
import org.kuali.rice.kew.webservice.ErrorResponse;
import org.kuali.rice.kew.webservice.NoteDetail;
import org.kuali.rice.kew.webservice.NoteResponse;
import org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService;
import org.kuali.rice.kew.webservice.StandardResponse;
import org.kuali.rice.kew.webservice.UserInRouteLogResponse;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.Person;


/**
 * Implementation of the SimpleDocumentActionsWebService
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(endpointInterface = KEWWebServiceConstants.SimpleDocumentActionsWebService.INTERFACE_CLASS,
        serviceName = KEWWebServiceConstants.SimpleDocumentActionsWebService.WEB_SERVICE_NAME,
        portName = KEWWebServiceConstants.SimpleDocumentActionsWebService.WEB_SERVICE_PORT,
        targetNamespace = KEWWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class SimpleDocumentActionsWebServiceImpl implements SimpleDocumentActionsWebService {

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Acknowledge the document with the passed in annotation</li>
	 * <li>Return the standard set of return values.</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param principalId principal id of the user who is acknowledging the document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#acknowledge(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse acknowledge(String docId, String principalId, String annotation) {
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);

			workflowDocument.acknowledge(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Set the docTitle and docContent if they are passed in</li>
	 * <li>Approve the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to approve
	 * @param principalId principal id of the user who is approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#approve(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse approve(String docId, String principalId, String docTitle,
			String docContent, String annotation) {
	  //Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);

			workflowDocument.approve(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Set the docTitle and docContent if they are passed in</li>
	 * <li>Blanket Approve the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * Blanket Approval means all future approval requests will be satisfied
	 * Can only be performed by a super user.
	 *
	 * @param docId KEW document id of the document to blanket approve
	 * @param principalId principal id of the user who is blanket approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#blanketApprove(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse blanketApprove(String docId, String principalId, String docTitle,
			String docContent, String annotation) {
	  //Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);

			workflowDocument.blanketApprove(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Cancel the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to cancel
	 * @param principalId principal id of the user who is canceling the document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 * 
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#cancel(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse cancel(String docId, String principalId, String annotation) {
		//Map<String, Object> results;
	    StandardResponse results;
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);

			workflowDocument.cancel(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument with the docType and principalId passed in</li>
	 * <li>Set the document title to be the docTitle that was passed in</li
	 * <li>Save the Routing data (Route Header info)</li>
	 * <li>Return the standard set of return values and the docId of the newly created document</li>
	 * </ol>
	 *
	 * @param initiatorPrincipalId principal id of the document initiator
	 * @param appDocId application specific document id
	 * @param docType KEW document type for the document to be created
	 * @param docTitle title for this document
	 * @return Map including the standard set of return values and the docId of the newly created document
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public DocumentResponse create(String initiatorPrincipalId, String appDocId, String docType,
			String docTitle) {

	    // Map<String, Object> results;
        StandardResponse results;

		String docId = "";

		try {
			WorkflowDocument workflowDocument = new WorkflowDocument(initiatorPrincipalId, docType);
			workflowDocument.setTitle(docTitle);
			workflowDocument.setAppDocId(appDocId);
			workflowDocument.saveRoutingData();

			results = createResults(workflowDocument);
			if (workflowDocument.getRouteHeaderId() != null) {
				docId = workflowDocument.getRouteHeaderId().toString();
			}
	        DocumentResponse docResponse = new DocumentResponse(results);
	        docResponse.setDocId(docId);
	        docResponse.setDocContent(workflowDocument.getApplicationContent());
	        docResponse.setTitle(workflowDocument.getTitle());
	        docResponse.setNotes(new ArrayList<NoteDetail>());
            if (workflowDocument.isApprovalRequested()) {
                docResponse.setActionRequested(KEWConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
            } else if (workflowDocument.isAcknowledgeRequested()) {
                docResponse.setActionRequested(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
            } else if (workflowDocument.isFYIRequested()) {
                docResponse.setActionRequested(KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
            } else if (workflowDocument.isCompletionRequested()) {
                // TODO: how do we want to handle a "Complete" request?
                docResponse.setActionRequested(KEWConstants.ACTION_REQUEST_COMPLETE_REQ_LABEL);
            }
	        return docResponse;
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
	        DocumentResponse docResponse = new DocumentResponse(results);
	        docResponse.setDocId(docId);
	        return docResponse;
		}
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Disapprove the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to disapprove
	 * @param principalId principal id of the user who is disapproving the document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#disapprove(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse disapprove(String docId, String principalId, String annotation) {
//      Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);

			workflowDocument.disapprove(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Clear the FYI request on the document</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param principalId principal id of the user who is acknowledging the document
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#fyi(java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse fyi(String docId, String principalId) {
//		Map<String, Object> results;
	    StandardResponse results;
	    
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);

			workflowDocument.fyi();
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Get the document content and the action requested (Approve, Acknowledge, etc) of the user</li>
	 * <li>Return the standard set of return values, the docContent, the title,
	 * and the actionRequested</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to retrieve information about
	 * @param principalId principal id of the user to retrieve the document for
	 * @return Map including the standard set of return values, the xml document content,
	 * the action requested ( Approve, Acknowledge, Fyi, Complete ) and an array of Maps
	 * containing the following for each Note (author, noteId, timestamp, noteText).
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#getDocument(java.lang.String, java.lang.String)
	 */
	@Override
	public DocumentResponse getDocument(String docId, String principalId) {
//		Map<String, Object> results;
		StandardResponse results;
	    List<NoteDetail> noteDetails = new ArrayList<NoteDetail>(0);
		String actionRequested = "";
		String docContent = "";
		String title = "";

		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);
			RouteHeaderDTO routeHeader = workflowDocument.getRouteHeader();

			if (routeHeader == null) {
				results = createErrorResults("Error: NULL Route Header");
			} else {
				results = createStandardResults(routeHeader);
				docContent = workflowDocument.getApplicationContent();
				title = workflowDocument.getTitle();
				List notes = workflowDocument.getNoteList();
				noteDetails = buildNoteDetails(notes);

				if (routeHeader.isApproveRequested()) {
					actionRequested = KEWConstants.ACTION_REQUEST_APPROVE_REQ_LABEL;
				} else if (routeHeader.isAckRequested()) {
					actionRequested = KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL;
				} else if (routeHeader.isFyiRequested()) {
					actionRequested = KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL;
				} else if (routeHeader.isCompleteRequested()) {
					// TODO: how do we want to handle a "Complete" request?
					actionRequested = KEWConstants.ACTION_REQUEST_COMPLETE_REQ_LABEL;
				}
			}
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		DocumentResponse docResponse = new DocumentResponse(results);
		
//		results.put(DOC_CONTENT_LABEL, docContent);
//		results.put(TITLE_LABEL, title);
//		results.put(NOTES_LABEL, noteDetails);
//		results.put(ACTION_REQUESTED_LABEL, actionRequested);

		docResponse.setDocId(docId);
		docResponse.setDocContent(docContent);
		docResponse.setTitle(title);
		docResponse.setNotes(noteDetails);
		docResponse.setActionRequested(actionRequested);
		
		return docResponse;
	}

	/**
  	 * <ol>
	 * <li>Create a new WorkflowInfo object</li>
	 * <li>Call isUserAuthenticatedByRouteLog on the WorkflowInfo object to see if the user is in the route log</li>
	 * <li>Return True/False and an error message if any</li>
	 * </ol>
	 * Useful for security purposes (if return is False, user shouldn't
	 * be able to see the document unless it's public.)
	 *
	 * Call isUserAuthenticatedByRouteLog with true for the lookFuture parameter so that
	 * we will check future workflow requests as well as currently outstanding requests.
	 *
	 * @param docId KEW document id of the document to check
	 * @param principalId principal id of the user to check
	 * @return Map containing True/False for isUserInRouteLog and an error message if
	 * a problem occured
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#isUserInRouteLog(java.lang.String, java.lang.String)
	 */
	@Override
	public UserInRouteLogResponse isUserInRouteLog(String docId, String principalId) {
		//Map<String, Object> results = new HashMap<String, Object>(6);
		
	    UserInRouteLogResponse results = new UserInRouteLogResponse();
	    String errorMessage = "";
		boolean isUserInRouteLog = false;
		WorkflowInfo info = new WorkflowInfo();
		try {
			Long id = Long.parseLong(docId);
			isUserInRouteLog = info.isUserAuthenticatedByRouteLog(id, principalId, true);
		} catch (NumberFormatException e) {
			errorMessage = "Invalid (non-numeric) docId";
		} catch (WorkflowException e) {
			errorMessage = "Workflow Error: " + e.getLocalizedMessage();
		}
		
		results.setIsUserInRouteLog(String.valueOf(isUserInRouteLog));
		results.setErrorMessage(errorMessage);
		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Add the adhoc acknowlege request (app specific route) to the passed in group with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#requestAdHocAckToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse requestAdHocAckToGroup(String docId, String principalId,
			String recipientGroupId, String annotation) {
		return requestAdHocToGroup(docId, principalId, recipientGroupId, annotation, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Add the adhoc acknowlege request (app specific route) to the passed in user with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientPrincipalId principal id of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 */
	@Override
	public StandardResponse requestAdHocAckToPrincipal(String docId, String principalId,
			String recipientPrincipalId, String annotation) {
		return requestAdHocToPrincipal(docId, principalId, recipientPrincipalId, annotation, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Add the adhoc approve request (app specific route) to the passed in group with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#requestAdHocApproveToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse requestAdHocApproveToGroup(String docId, String principalId,
			String recipientGroupId, String annotation) {
		return requestAdHocToGroup(docId, principalId, recipientGroupId, annotation, KEWConstants.ACTION_REQUEST_APPROVE_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Add the adhoc approve request (app specific route) to the passed in user with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientPrincipalId principal id of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 */
	@Override
	public StandardResponse requestAdHocApproveToPrincipal(String docId, String principalId,
			String recipientPrincipalId, String annotation) {

		return requestAdHocToPrincipal(docId, principalId, recipientPrincipalId, annotation, KEWConstants.ACTION_REQUEST_APPROVE_REQ, KEWConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Add the adhoc fyi request (app specific route) to the passed in group with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#requestAdHocFyiToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse requestAdHocFyiToGroup(String docId, String principalId, String recipientGroupId, String annotation) {
		return requestAdHocToGroup(docId, principalId, recipientGroupId, annotation, KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Add the adhoc fyi request (app specific route) to the passed in user with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientPrincipalId principal id of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 */
	@Override
	public StandardResponse requestAdHocFyiToPrincipal(String docId, String principalId,
			String recipientPrincipalId, String annotation) {
		return requestAdHocToPrincipal(docId, principalId, recipientPrincipalId, annotation, KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
	}

	/**
	 * Create the adhoc request for the specified group.
	 *
 	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientGroupId workflowid of the group for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @param actionRequested the action for this adhoc request ( A)pprove, aK)nowledge, F)yi )
	 * @param responsibilityDesc description of the type of responsibility for this request
	 * @return Map including the standard set of return values
	 */
	private StandardResponse requestAdHocToGroup(String docId, String principalId,
			String groupId, String annotation, String actionRequested, String responsibilityDesc) {
//      Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);
			workflowDocument.adHocRouteDocumentToGroup(actionRequested, annotation, groupId, responsibilityDesc, true);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * Create the adhoc request for the specified user.
	 *
 	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientPrincipalId principal id of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @param actionRequested the action for this adhoc request ( A)pprove, aK)nowledge, F)yi )
	 * @param responsibilityDesc description of the type of responsibility for this request
	 * @return Map including the standard set of return values
	 */
	private StandardResponse requestAdHocToPrincipal(String docId, String principalId,
			String recipientPrincipalId, String annotation, String actionRequested, String responsibilityDesc) {
	    StandardResponse results;	    
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);
			workflowDocument.adHocRouteDocumentToPrincipal(actionRequested, annotation, recipientPrincipalId, responsibilityDesc, true);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}
		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Set the docTitle and docContent if they are passed in</li>
	 * <li>Route the document with the passed in annotation</li>
	 * <li>Return the standard set of return values.</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to route
	 * @param principalId principal id of the user who is routing the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#route(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse route(String docId, String principalId, String docTitle,
			String docContent, String annotation) {

		//Map<String, Object> results;
	    StandardResponse results;
	    
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);

			workflowDocument.routeDocument(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and principalId passed in</li>
	 * <li>Set the docTitle if it was passed in</li>
	 * <li>Save the document with the passed in annotation (keep in user's action list)</li>
	 * <li>Return the standard set of return values.</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to save
	 * @param principalId principal id of the user who is saving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#save(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse save(String docId, String principalId, String docTitle, String docContent, String annotation) {
	    StandardResponse results;
	    
		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);
			workflowDocument.saveDocument(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#saveDocumentContent(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public StandardResponse saveDocumentContent(String docId, String principalId, String docTitle, String docContent) {
	    StandardResponse results;
	    
		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);
			workflowDocument.saveRoutingData();
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
     * Add a note to this KEW document.
     *
	 * @param docId KEW document id of the document to add the note to
	 * @param principalId principal id of the user who is adding the note
     * @param noteText text of the note
     * @return Map containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#addNote(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public NoteResponse addNote(String docId, String principalId, String noteText) {
//		Map<String, Object> results = new HashMap<String, Object>(5);
		NoteResponse results = new NoteResponse();
		
		String author = "";
		String noteId = "";
		String timestamp = "";
		String resultsNoteText = "";
		String errorMessage = "";

//		results.put(ERROR_MESSAGE_LABEL, "");

		results.setErrorMessage("");
		
		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);

			// setup note
			NoteDTO noteVO = new NoteDTO();
			noteVO.setNoteAuthorWorkflowId(principalId);
			noteVO.setNoteCreateDate(new GregorianCalendar());
			noteVO.setNoteText(noteText);
			noteVO.setRouteHeaderId(workflowDocument.getRouteHeaderId());
			workflowDocument.updateNote(noteVO);

			//TODO: is this necessary?
			workflowDocument.saveRoutingData();
			RouteHeaderDTO routeHeader = workflowDocument.getRouteHeader();

			//TODO: do we need to return the standard result set?
			//results = createResults(workflowDocument);

			noteVO = routeHeader.getNotes()[routeHeader.getNotes().length-1];

			// return note info
			Person person = KIMServiceLocator.getPersonService().getPerson(noteVO.getNoteAuthorWorkflowId());
			author = person.getName();
			noteId = noteVO.getNoteId().toString();
			timestamp = formatCalendar(noteVO.getNoteCreateDate());
			resultsNoteText = noteVO.getNoteText();
		} catch (WorkflowException e) {
			errorMessage = "Workflow Error: " + e.getLocalizedMessage();
		}
		results.setAuthor(author);
		results.setNoteId(noteId);
		results.setTimestamp(timestamp);
		results.setNoteText(resultsNoteText);
		results.setErrorMessage(errorMessage);
		
		return results;
    }

    /**
     * Update an existing note to this KEW document.
     *
	 * @param docId KEW document id of the document to update the note for
	 * @param noteId the id of the note to update
	 * @param principalId principal id of the user who is updating the note
     * @param noteText text of the note if changed
     * @return Map containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#updateNote(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public NoteResponse updateNote(String docId, String noteId, String principalId, String noteText) {
//		Map<String, Object> results = new HashMap<String, Object>(5);
		String author = "";
		String resultsNoteId = "";
		String timestamp = "";
		String resultsNoteText = "";
		String errorMessage = "";

		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);
			RouteHeaderDTO routeHeader = workflowDocument.getRouteHeader();

			// setup note
			NoteDTO noteVO = getNote(routeHeader.getNotes(), noteId);
			noteVO.setNoteText(noteText);
			workflowDocument.updateNote(noteVO);

			//TODO: is this necessary?
			workflowDocument.saveRoutingData();
			routeHeader = workflowDocument.getRouteHeader();

			//TODO: do we need to return the standard result set?
			//results = createResults(workflowDocument);

			noteVO = getNote(routeHeader.getNotes(), noteId);

			if (noteVO == null) {
				errorMessage = "Error retrieving note for id [" + noteId + "].";
			} else {
				// return note info
				Person person = KIMServiceLocator.getPersonService().getPerson(noteVO.getNoteAuthorWorkflowId());
				author = person.getName();
				resultsNoteId = noteVO.getNoteId().toString();
				timestamp = formatCalendar(noteVO.getNoteCreateDate());
				resultsNoteText = noteVO.getNoteText();
			}
		} catch (WorkflowException e) {
			errorMessage = "Workflow Error: " + e.getLocalizedMessage();
		}

		NoteResponse results = new NoteResponse();

		results.setAuthor(author);
		results.setNoteId(resultsNoteId);
		results.setTimestamp(timestamp);
		results.setNoteText(resultsNoteText);
		results.setErrorMessage(errorMessage);
		
		return results;
    }

    /**
     * Delete an existing note.
     *
	 * @param docId KEW document id of the document to delete the note from
     * @param noteId the id of the note to delete
	 * @param principalId principal id of the user who is deleting the note
     * @return Map containing an error message if any
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#deleteNote(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ErrorResponse deleteNote(String docId, String noteId, String principalId) {
		//Map<String, Object> results = new HashMap<String, Object>(1);
		
        ErrorResponse results = new ErrorResponse();
        
        String errorMessage = "";

		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId);
			RouteHeaderDTO routeHeader = workflowDocument.getRouteHeader();

			// setup note
			NoteDTO noteVO = getNote(routeHeader.getNotes(), noteId);
			workflowDocument.deleteNote(noteVO);

			//TODO: is this necessary?
			workflowDocument.saveRoutingData();

////	      update notes database based on notes and notesToDelete arrays in routeHeaderVO
//	        DTOConverter.updateNotes(routeHeader, routeHeader.getRouteHeaderId());
		} catch (WorkflowException e) {
			errorMessage = "Workflow Error: " + e.getLocalizedMessage();
		}
		//results.put(ERROR_MESSAGE_LABEL, errorMessage);

		results.setErrorMessage(errorMessage);
		
		return results;
    }

	/**
	 * Return a KEW document to a previous route node.  This method should
	 * be used with caution.
	 *
	 * @param docId KEW document id of the document that is being returned
	 * @param principalId principal id of the user who is returning the doc
	 * @param annotation a comment associated with this request
     * @param nodeName name of the route node to return to
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#returnToPreviousNode(java.lang.String, java.lang.String)
	 */
    @Override
	public StandardResponse returnToPreviousNode(String docId, String principalId, String annotation, String nodeName) {
    	return returnToPreviousNodeWithUpdates(docId, principalId, annotation, nodeName, null, null);
    }

	/**
	 * Return a KEW document to a previous route node.  This method should
	 * be used with caution.
	 *
	 * @param docId KEW document id of the document that is being returned
	 * @param principalId principal id of the user who is returning the doc
	 * @param annotation a comment associated with this request
     * @param nodeName name of the route node to return to
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @return Map including the standard set of return values
	 *
	 * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#returnToPreviousNode(java.lang.String, java.lang.String)
	 */
    @Override
	public StandardResponse returnToPreviousNodeWithUpdates(String docId, String principalId, String annotation, String nodeName, String docTitle, String docContent) {
        StandardResponse results;
        
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);

			workflowDocument.returnToPreviousNode(annotation, nodeName);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
    }

    /**
     * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#revokeAdHocRequestsByNodeName(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public StandardResponse revokeAdHocRequestsByNodeName(String docId, String principalId, String docTitle, String docContent, String nodeName, String annotation) {
        StandardResponse results;

        try {
            WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);
            AdHocRevokeDTO revokeDTO = new AdHocRevokeDTO();
            revokeDTO.setNodeName(nodeName);
            workflowDocument.revokeAdHocRequests(revokeDTO, annotation);
            results = createResults(workflowDocument);
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#revokeAdHocRequestsByPrincipalId(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public StandardResponse revokeAdHocRequestsByPrincipalId(String docId, String principalId, String docTitle, String docContent, String adhocPrincipalId, String annotation) {
        StandardResponse results;

        try {
            WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);
            AdHocRevokeDTO revokeDTO = new AdHocRevokeDTO();
            revokeDTO.setPrincipalId(adhocPrincipalId);
            workflowDocument.revokeAdHocRequests(revokeDTO, annotation);
            results = createResults(workflowDocument);
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#revokeAdHocRequestsByGroupId(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public StandardResponse revokeAdHocRequestsByGroupId(String docId, String principalId, String docTitle, String docContent, String groupId, String annotation) {
        StandardResponse results;

        try {
            WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);
            AdHocRevokeDTO revokeDTO = new AdHocRevokeDTO();
            revokeDTO.setGroupId(groupId);
            workflowDocument.revokeAdHocRequests(revokeDTO, annotation);
            results = createResults(workflowDocument);
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#revokeAdHocRequestsByActionRequestId(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public StandardResponse revokeAdHocRequestsByActionRequestId(String docId, String principalId, String docTitle, String docContent, String actionRequestId, String annotation) {
        StandardResponse results;

        try {
            WorkflowDocument workflowDocument = setupWorkflowDocument(docId, principalId, docTitle, docContent);
            AdHocRevokeDTO revokeDTO = new AdHocRevokeDTO();
            revokeDTO.setActionRequestId(Long.valueOf(actionRequestId));
            workflowDocument.revokeAdHocRequests(revokeDTO, annotation);
            results = createResults(workflowDocument);
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#superUserApprove(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public StandardResponse superUserApprove(String docId, String superUserPrincipalId, String docTitle, String docContent, String annotation) {
        StandardResponse results;

        try {
            WorkflowDocument workflowDocument = setupWorkflowDocument(docId, superUserPrincipalId, docTitle, docContent);

            workflowDocument.superUserApprove(annotation);
            results = createResults(workflowDocument);
        } catch (WorkflowServiceErrorException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#superUserDisapprove(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public StandardResponse superUserDisapprove(String docId, String superUserPrincipalId, String docTitle, String docContent, String annotation) {
        StandardResponse results;

        try {
            WorkflowDocument workflowDocument = setupWorkflowDocument(docId, superUserPrincipalId, docTitle, docContent);

            workflowDocument.superUserDisapprove(annotation);
            results = createResults(workflowDocument);
        } catch (WorkflowServiceErrorException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    /**
     * @see org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService#superUserCancel(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public StandardResponse superUserCancel(String docId, String superUserPrincipalId, String docTitle, String docContent, String annotation) {
        StandardResponse results;

        try {
            WorkflowDocument workflowDocument = setupWorkflowDocument(docId, superUserPrincipalId, docTitle, docContent);

            workflowDocument.superUserCancel(annotation);
            results = createResults(workflowDocument);
        } catch (WorkflowServiceErrorException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    public StandardResponse superUserReturnToPrevious(String docId, String superUserPrincipalId, String docTitle, String docContent, String nodeName, String annotation) {
        StandardResponse results;

        try {
            // verify that the doc id has been sent in correctly
            if (StringUtils.isBlank(docId)) {
                throw new WorkflowException("Invalid Parameter: docId is required but was blank");
            }
            // if the docTitle or docContent has been updated then update it with a saveRoutingData call
            if (StringUtils.isNotEmpty(docTitle) || StringUtils.isNotEmpty(docContent)) {
                WorkflowDocument workflowDocument = setupWorkflowDocument(docId, superUserPrincipalId, docTitle, docContent);
                workflowDocument.saveRoutingData();
            }
            // perform the return to previous
            KEWServiceLocator.getWorkflowDocumentActionsService().superUserReturnToPreviousNode(superUserPrincipalId, Long.valueOf(docId), nodeName, annotation);
            // refetch the WorkflowDocument after the return to previous is completed
            results = createResults(new WorkflowDocument(superUserPrincipalId, Long.decode(docId)));
        } catch (WorkflowServiceErrorException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        } catch (WorkflowException e) {
            results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
        }

        return results;
    }

    private NoteDTO getNote(NoteDTO[] notes, String noteId) {
    	NoteDTO note = null;

    	if (notes != null){
	    	for (NoteDTO note2 : notes) {
	    		if (note2.getNoteId().toString().equals(noteId)) {
	    			note = note2;
	    			break;
	    		}
	    	}
    	}

    	return note;
    }

    /**
	 * Convenience method to setup workflow document without title or content.
	 *
	 * @param docId KEW document id for the document to setup
	 * @param principalId KEW principal id for the user associated with this document
	 * @return populated WorkflowDocument object
	 * @throws WorkflowException if something goes wrong
	 */
	private WorkflowDocument setupWorkflowDocument(String docId, String principalId) throws WorkflowException {
		return setupWorkflowDocument(docId, principalId, null, null);
	}

	/**
	 * Instantiate and setup the WorkflowDocument object.
	 *
	 * @param docId KEW document id for the document to setup
	 * @param principalId KEW principal id for the user associated with this document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @return populated WorkflowDocument object
	 * @throws WorkflowException if something goes wrong
	 */
	private WorkflowDocument setupWorkflowDocument(String docId, String principalId, String docTitle, String docContent) throws WorkflowException {
		WorkflowDocument workflowDocument = new WorkflowDocument(principalId, Long.decode(docId));
		if (StringUtils.isNotEmpty(docTitle)) {
			workflowDocument.setTitle(docTitle);
		}
		if (StringUtils.isNotEmpty(docContent)) {
			workflowDocument.setApplicationContent(docContent);
		}
		return workflowDocument;
	}

	/**
	 * Create the note details result set.
	 *
	 * @param notes List of notes to build details Map array for
	 * @return Map[] containing note details
	 * @throws WorkflowException if an error occurs retrieving user display name
	 */
	private List<NoteDetail> buildNoteDetails(List notes) throws WorkflowException {
		List<NoteDetail> noteDetails;

		if (notes == null) {
			noteDetails = new ArrayList<NoteDetail>(0);
		} else {
			noteDetails = new ArrayList<NoteDetail>(notes.size());
			for (int i=0;i<notes.size();i++) {
				//Map<String, String> noteDetail = new HashMap<String, String>(4);

			    NoteDetail noteDetail = new NoteDetail();
				NoteDTO note = (NoteDTO)notes.get(i);
				//author, noteId, timestamp, noteText
				Person person = KIMServiceLocator.getPersonService().getPerson(note.getNoteAuthorWorkflowId());
				noteDetail.setAuthor(person.getName());
				noteDetail.setId(note.getNoteId().toString());
				noteDetail.setTimestamp(formatCalendar(note.getNoteCreateDate()));
				noteDetail.setNoteText(note.getNoteText());
				
				noteDetails.add(noteDetail);
			}
		}

		return noteDetails;
	}


	/**
	 * Create the result set, either the standard results or error results
	 * if the routeHeader is null.
	 *
	 * @param workflowDocument WorkflowDocument used to get route header info
	 * @return Map containing results of the call (either standard or error version)
	 */
	private StandardResponse createResults(WorkflowDocument workflowDocument) {
		//Map<String, Object> results;

	    StandardResponse response;
	    
		RouteHeaderDTO routeHeader = workflowDocument.getRouteHeader();

		if (routeHeader == null) {
			response = createErrorResults("Error: NULL Route Header");
		} else {
			response = createStandardResults(routeHeader);
		}

		return response;
	}

	/**
	 * Create the standard result set with only the error message populated.
	 *
	 * @param errorMessage the message describing what error occured in the KEW engine
	 * @return Map containing the standard result set with only the error message populated
	 */
	private StandardResponse createErrorResults(String errorMessage) {
	    StandardResponse response = new StandardResponse();
	    response.setDocStatus("");
	    response.setCreateDate("");
	    response.setInitiatorPrincipalId("");
	    response.setRoutedByPrincipalId("");
	    response.setAppDocId("");
	    response.setInitiatorName("");
	    response.setRoutedByUserName("");
	    response.setErrorMessage(errorMessage);
		
	    return response;
	}

	/**
	 * Create the standard result set populated with values contained in the
	 * documents route header.
	 *
	 * @param routeHeader RouteHeaderVO for this document
	 * @return Map containing the standard result set populated with values contained in the
	 * documents route header.
	 */
	private StandardResponse createStandardResults(RouteHeaderDTO routeHeader) {
		String docStatus = "";
		String createDate = "";
		String initiatorPrincipalId = "";
        String routedByPrincipalId = "";
		String appDocId = "";
		String initiatorName = "";
        String routedByUserName = "";
		String errorMessage = "";

		if (routeHeader == null) {
			errorMessage = "Error: NULL Route Header";
		} else {
			if (routeHeader.getDocRouteStatus() == null) {
				errorMessage = "Error: NULL Route Status; ";
			} else {
				docStatus = routeHeader.getDocRouteStatus();
			}

			if (routeHeader.getDateCreated() == null) {
				errorMessage += "Error: NULL Date Created; ";
			} else {
				createDate = formatCalendar(routeHeader.getDateCreated());
			}

			if (routeHeader.getInitiatorPrincipalId() == null) {
				errorMessage += "Error: NULL Initiator; ";
			} else {
				initiatorPrincipalId = routeHeader.getInitiatorPrincipalId();
				Person initiator = KIMServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
				if (initiator != null) {
				    initiatorName = initiator.getName();
				}
			}

            if (routeHeader.getRoutedByPrincipalId() == null) {
                // of the document has been routed, but there is no routed-by user, that is an error
                if (KEWConstants.ROUTE_HEADER_ENROUTE_CD.equals(routeHeader.getDocRouteStatus())) {
                        errorMessage += "Error: NULL routedBy user; ";
                }
            } else {
                routedByPrincipalId = routeHeader.getRoutedByPrincipalId();
                Person routedByUser = KIMServiceLocator.getPersonService().getPerson(initiatorPrincipalId);
                if (routedByUser != null) {
                    routedByUserName = routedByUser.getName();
                }
            }

			if (routeHeader.getAppDocId() != null) {
				appDocId = routeHeader.getAppDocId();
			}
		}

		//Map<String, Object> results = new HashMap<String, Object>(6);
		StandardResponse response = new StandardResponse();

//		results.put(DOC_STATUS_LABEL, docStatus);
//		results.put(CREATE_DATE_LABEL, createDate);
//		results.put(INITIATOR_ID_LABEL, initiatorPrincipalId);
//        results.put(ROUTED_BY_USER_ID_LABEL, routedByprincipalId);
//		results.put(APP_DOC_ID_LABEL, appDocId);
//		results.put(INITIATOR_NAME_LABEL, initiatorName);
//        results.put(ROUTED_BY_USER_NAME_LABEL, routedByUserName);
//		results.put(ERROR_MESSAGE_LABEL, errorMessage);

		response.setDocStatus(docStatus);
		response.setCreateDate(createDate);
		response.setInitiatorPrincipalId(initiatorPrincipalId);
		response.setRoutedByPrincipalId(routedByPrincipalId);
		response.setAppDocId(appDocId);
		response.setInitiatorName(initiatorName);
		response.setRoutedByUserName(routedByUserName);
		response.setErrorMessage(errorMessage);
		
		return response;
	}


	/**
	 * Format a String date based on a given Calendar object.
	 *
	 * @param calendar Calendar date to format
	 * @return String formatted date
	 */
	private String formatCalendar(Calendar calendar) {
		String formattedDate = "";

		DateFormat dateFormat = new SimpleDateFormat();
		Timestamp dateCreated = SQLUtils.convertCalendar(calendar);
		formattedDate = dateFormat.format(dateCreated);

		return formattedDate;
	}

}
