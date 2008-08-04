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
package edu.iu.uis.eden.server;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.NoteVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.util.Utilities;

/**
 * Implementation of the SimpleDocumentActionsWebService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SimpleDocumentActionsWebServiceImpl implements SimpleDocumentActionsWebService {

	/*private static final String IS_USER_IN_ROUTE_LOG_LABEL = "isUserInRouteLog";
	private static final String DOC_ID_LABEL = "docId";
	private static final String DOC_CONTENT_LABEL = "docContent";
	private static final String NOTES_LABEL = "notes";
	private static final String ACTION_REQUESTED_LABEL = "actionRequested";
	private static final String ERROR_MESSAGE_LABEL = "errorMessage";
	private static final String INITIATOR_NAME_LABEL = "initiatorName";
	private static final String ROUTED_BY_USER_NAME_LABEL = "routedByUserName";
	private static final String APP_DOC_ID_LABEL = "appDocId";
	private static final String INITIATOR_ID_LABEL = "initiatorId";
	private static final String ROUTED_BY_USER_ID_LABEL = "routedByUserId";
	private static final String CREATE_DATE_LABEL = "createDate";
	private static final String DOC_STATUS_LABEL = "docStatus";
	private static final String TITLE_LABEL = "title";
	private static final String NOTE_AUTHOR_LABEL = "author";
	private static final String NOTE_ID_LABEL = "noteId";
	private static final String NOTE_TIMESTAMP_LABEL = "timestamp";
	private static final String NOTE_TEXT_LABEL = "noteText";*/

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Acknowledge the document with the passed in annotation</li>
	 * <li>Return the standard set of return values.</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param userId netid of the user who is acknowledging the document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#acknowledge(java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse acknowledge(String docId, String userId, String annotation) {
	  //Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			workflowDocument.acknowledge(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Set the docTitle and docContent if they are passed in</li>
	 * <li>Approve the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to approve
	 * @param userId netid of the user who is approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#approve(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse approve(String docId, String userId, String docTitle,
			String docContent, String annotation) {
	  //Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId, docTitle, docContent);

			workflowDocument.approve(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Set the docTitle and docContent if they are passed in</li>
	 * <li>Blanket Approve the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * Blanket Approval means all future approval requests will be satisfied
	 * Can only be performed by a super user.
	 *
	 * @param docId KEW document id of the document to blanket approve
	 * @param userId netid of the user who is blanket approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#blanketApprove(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse blanketApprove(String docId, String userId, String docTitle,
			String docContent, String annotation) {
	  //Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId, docTitle, docContent);

			workflowDocument.blanketApprove(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Cancel the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to cancel
	 * @param userId netid of the user who is canceling the document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#cancel(java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse cancel(String docId, String userId, String annotation) {
		//Map<String, Object> results;
	    StandardResponse results;
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			workflowDocument.cancel(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument with the docType and userId passed in</li>
	 * <li>Set the document title to be the docTitle that was passed in</li
	 * <li>Save the Routing data (Route Header info)</li>
	 * <li>Return the standard set of return values and the docId of the newly created document</li>
	 * </ol>
	 *
	 * @param initiatorId netid of the document initiator
	 * @param appDocId application specific document id
	 * @param docType KEW document type for the document to be created
	 * @param docTitle title for this document
	 * @return Map including the standard set of return values and the docId of the newly created document
	 *
	 * @see edu.cornell.kew.service.CornellKewService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public DocumentResponse create(String initiatorId, String appDocId, String docType,
			String docTitle) {

	    // Map<String, Object> results;
        StandardResponse results;

		String docId = "";

		try {
			UserIdVO userIdVO = new NetworkIdVO(initiatorId);
			WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, docType);
			workflowDocument.setTitle(docTitle);
			workflowDocument.setAppDocId(appDocId);
			workflowDocument.saveRoutingData();

			results = createResults(workflowDocument);
			if (workflowDocument.getRouteHeaderId() != null) {
				docId = workflowDocument.getRouteHeaderId().toString();
			}
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		DocumentResponse docResponse = new DocumentResponse(results);
		docResponse.setDocId(docId);
		//results.put(DOC_ID_LABEL, docId);

		
		return docResponse;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Disapprove the document with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to disapprove
	 * @param userId netid of the user who is disapproving the document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#disapprove(java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse disapprove(String docId, String userId, String annotation) {
//      Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			workflowDocument.disapprove(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Clear the FYI request on the document</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param userId netid of the user who is acknowledging the document
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#fyi(java.lang.String, java.lang.String)
	 */
	public StandardResponse fyi(String docId, String userId) {
//		Map<String, Object> results;
	    StandardResponse results;
	    
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			workflowDocument.fyi();
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Get the document content and the action requested (Approve, Acknowledge, etc) of the user</li>
	 * <li>Return the standard set of return values, the docContent, the title,
	 * and the actionRequested</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to retrieve information about
	 * @param userId netid of the user to retrieve the document for
	 * @return Map including the standard set of return values, the xml document content,
	 * the action requested ( Approve, Aknowledge, Fyi, Complete ) and an array of Maps
	 * containing the following for each Note (author, noteId, timestamp, noteText).
	 *
	 * @see edu.cornell.kew.service.CornellKewService#getDocument(java.lang.String, java.lang.String)
	 */
	public DocumentResponse getDocument(String docId, String userId) {
//		Map<String, Object> results;
		StandardResponse results;
	    List<NoteDetail> noteDetails = new ArrayList<NoteDetail>(0);
		String actionRequested = "";
		String docContent = "";
		String title = "";

		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);
			RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();

			if (routeHeader == null) {
				results = createErrorResults("Error: NULL Route Header");
			} else {
				results = createStandardResults(routeHeader);
				docContent = workflowDocument.getApplicationContent();
				title = workflowDocument.getTitle();
				List notes = workflowDocument.getNoteList();
				noteDetails = buildNoteDetails(notes);

				if (routeHeader.isApproveRequested()) {
					actionRequested = EdenConstants.ACTION_REQUEST_APPROVE_REQ_LABEL;
				} else if (routeHeader.isAckRequested()) {
					actionRequested = EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL;
				} else if (routeHeader.isFyiRequested()) {
					actionRequested = EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL;
				} else if (routeHeader.isCompleteRequested()) {
					// TODO: how do we want to handle a "Complete" request?
					actionRequested = EdenConstants.ACTION_REQUEST_COMPLETE_REQ_LABEL;
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
	 * @param userId netid of the user to check
	 * @return Map containing True/False for isUserInRouteLog and an error message if
	 * a problem occured
	 *
	 * @see edu.cornell.kew.service.CornellKewService#isUserInRouteLog(java.lang.String, java.lang.String)
	 */
	public UserInRouteLogResponse isUserInRouteLog(String docId, String userId) {
		//Map<String, Object> results = new HashMap<String, Object>(6);
		
	    UserInRouteLogResponse results = new UserInRouteLogResponse();
	    String errorMessage = "";
		boolean isUserInRouteLog = false;
		WorkflowInfo info = new WorkflowInfo();
		try {
			Long id = Long.parseLong(docId);
			UserIdVO userIdVO = new NetworkIdVO(userId);

			isUserInRouteLog = info.isUserAuthenticatedByRouteLog(id, userIdVO, true);
		} catch (NumberFormatException e) {
			errorMessage = "Invalid (non-numeric) docId";
		} catch (WorkflowException e) {
			errorMessage = "Workflow Error: " + e.getLocalizedMessage();
		}

//		results.put(IS_USER_IN_ROUTE_LOG_LABEL, String.valueOf(isUserInRouteLog));
//		results.put(ERROR_MESSAGE_LABEL, errorMessage);
		
		results.setIsUserInRouteLog(String.valueOf(isUserInRouteLog));
		results.setErrorMessage(errorMessage);
		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Add the adhoc acknowlege request (app specific route) to the passed in group with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#requestAdHocAckToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse requestAdHocAckToGroup(String docId, String userId,
			String recipientGroupId, String annotation) {
		return requestAdHocToGroup(docId, userId, recipientGroupId, annotation, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Add the adhoc acknowlege request (app specific route) to the passed in user with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientUserId netid of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#requestAdHocAckToUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse requestAdHocAckToUser(String docId, String userId,
			String recipientUserId, String annotation) {
		return requestAdHocToUser(docId, userId, recipientUserId, annotation, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Add the adhoc approve request (app specific route) to the passed in group with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#requestAdHocApproveToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse requestAdHocApproveToGroup(String docId, String userId,
			String recipientGroupId, String annotation) {
		return requestAdHocToGroup(docId, userId, recipientGroupId, annotation, EdenConstants.ACTION_REQUEST_APPROVE_REQ, EdenConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Add the adhoc approve request (app specific route) to the passed in user with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientUserId netid of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#requestAdHocApproveToUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse requestAdHocApproveToUser(String docId, String userId,
			String recipientUserId, String annotation) {

		return requestAdHocToUser(docId, userId, recipientUserId, annotation, EdenConstants.ACTION_REQUEST_APPROVE_REQ, EdenConstants.ACTION_REQUEST_APPROVE_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Add the adhoc fyi request (app specific route) to the passed in group with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#requestAdHocFyiToGroup(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse requestAdHocFyiToGroup(String docId, String userId,
			String recipientGroupId, String annotation) {
		return requestAdHocToGroup(docId, userId, recipientGroupId, annotation, EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Add the adhoc fyi request (app specific route) to the passed in user with the passed in annotation</li>
	 * <li>Return the standard set of return values</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientUserId netid of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#requestAdHocFyiToUser(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse requestAdHocFyiToUser(String docId, String userId,
			String recipientUserId, String annotation) {
		return requestAdHocToUser(docId, userId, recipientUserId, annotation, EdenConstants.ACTION_REQUEST_FYI_REQ, EdenConstants.ACTION_REQUEST_FYI_REQ_LABEL);
	}

	/**
	 * Create the adhoc request for the specified group.
	 *
 	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientGroupId workflowid of the group for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @param actionRequested the action for this adhoc request ( A)pprove, aK)nowledge, F)yi )
	 * @param responsibilityDesc description of the type of responsibility for this request
	 * @return Map including the standard set of return values
	 */
	private StandardResponse requestAdHocToGroup(String docId, String userId,
			String recipientGroupId, String annotation, String actionRequested, String responsibilityDesc) {
//      Map<String, Object> results;
        StandardResponse results;

       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			WorkgroupIdVO recipientId = new WorkgroupNameIdVO(recipientGroupId);
			// TODO: what should we put in the responsibility description?
			workflowDocument.appSpecificRouteDocumentToWorkgroup(actionRequested, annotation, recipientId, responsibilityDesc, true);
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
	 * @param userId netid of the user who is making this request
	 * @param recipientUserId netid of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @param actionRequested the action for this adhoc request ( A)pprove, aK)nowledge, F)yi )
	 * @param responsibilityDesc description of the type of responsibility for this request
	 * @return Map including the standard set of return values
	 */
	private StandardResponse requestAdHocToUser(String docId, String userId,
			String recipientUserId, String annotation, String actionRequested, String responsibilityDesc) {
//		Map<String, Object> results;
	    StandardResponse results;
	    
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			UserIdVO recipientId = new NetworkIdVO(recipientUserId);
			// TODO: what should we put in the responsibility description?
			workflowDocument.appSpecificRouteDocumentToUser(actionRequested, annotation, recipientId, responsibilityDesc, true);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Set the docTitle and docContent if they are passed in</li>
	 * <li>Route the document with the passed in annotation</li>
	 * <li>Return the standard set of return values.</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to route
	 * @param userId netid of the user who is routing the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#route(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse route(String docId, String userId, String docTitle,
			String docContent, String annotation) {

		//Map<String, Object> results;
	    StandardResponse results;
	    
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId, docTitle, docContent);

			workflowDocument.routeDocument(annotation);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
	}

	/**
 	 * <ol>
	 * <li>Create a WorkflowDocument based on the docId and userId passed in</li>
	 * <li>Set the docTitle if it was passed in</li>
	 * <li>Save the document with the passed in annotation (keep in user's action list)</li>
	 * <li>Return the standard set of return values.</li>
	 * </ol>
	 *
	 * @param docId KEW document id of the document to save
	 * @param userId netid of the user who is saving the document
	 * @param docTitle title for this document
	 * @param annotation a comment associated with this request
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#save(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public StandardResponse save(String docId, String userId, String docTitle, String annotation) {
//		Map<String, Object> results;

	    StandardResponse results;
	    
		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId, docTitle);
			workflowDocument.saveDocument(annotation);
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
	 * @param userId netid of the user who is adding the note
     * @param noteText text of the note
     * @return Map containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
	 *
	 * @see edu.cornell.kew.service.CornellKewService#addNote(java.lang.String, java.lang.String, java.lang.String)
     */
    public NoteResponse addNote(String docId, String userId, String noteText) {
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
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			// setup note
			NoteVO noteVO = new NoteVO();
			noteVO.setNoteAuthorWorkflowId(userId);
			noteVO.setNoteCreateDate(new GregorianCalendar());
			noteVO.setNoteText(noteText);
			noteVO.setRouteHeaderId(workflowDocument.getRouteHeaderId());
			workflowDocument.updateNote(noteVO);

			//TODO: is this necessary?
			workflowDocument.saveRoutingData();
			RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();

			//TODO: do we need to return the standard result set?
			//results = createResults(workflowDocument);

			noteVO = routeHeader.getNotes()[routeHeader.getNotes().length-1];

			// return note info
			UserIdVO userIdVO = new NetworkIdVO(noteVO.getNoteAuthorWorkflowId());
			WorkflowInfo info = new WorkflowInfo();
			UserVO user = info.getWorkflowUser(userIdVO);
			author = user.getDisplayName();
			noteId = noteVO.getNoteId().toString();
			timestamp = formatCalendar(noteVO.getNoteCreateDate());
			resultsNoteText = noteVO.getNoteText();
		} catch (WorkflowException e) {
			errorMessage = "Workflow Error: " + e.getLocalizedMessage();
		}

//		results.put(NOTE_AUTHOR_LABEL, author);
//		results.put(NOTE_ID_LABEL, noteId);
//		results.put(NOTE_TIMESTAMP_LABEL, timestamp);
//		results.put(NOTE_TEXT_LABEL, resultsNoteText);
//		results.put(ERROR_MESSAGE_LABEL, errorMessage);

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
	 * @param userId netid of the user who is updating the note
     * @param noteText text of the note if changed
     * @return Map containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
	 *
	 * @see edu.cornell.kew.service.CornellKewService#updateNote(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public NoteResponse updateNote(String docId, String noteId, String userId, String noteText) {
//		Map<String, Object> results = new HashMap<String, Object>(5);
		String author = "";
		String resultsNoteId = "";
		String timestamp = "";
		String resultsNoteText = "";
		String errorMessage = "";

		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);
			RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();

			// setup note
			NoteVO noteVO = getNote(routeHeader.getNotes(), noteId);
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
				UserIdVO userIdVO = new NetworkIdVO(noteVO.getNoteAuthorWorkflowId());
				WorkflowInfo info = new WorkflowInfo();
				UserVO user = info.getWorkflowUser(userIdVO);
				author = user.getDisplayName();
				resultsNoteId = noteVO.getNoteId().toString();
				timestamp = formatCalendar(noteVO.getNoteCreateDate());
				resultsNoteText = noteVO.getNoteText();
			}
		} catch (WorkflowException e) {
			errorMessage = "Workflow Error: " + e.getLocalizedMessage();
		}

		NoteResponse results = new NoteResponse();
		
//		results.put(NOTE_AUTHOR_LABEL, author);
//		results.put(NOTE_ID_LABEL, resultsNoteId);
//		results.put(NOTE_TIMESTAMP_LABEL, timestamp);
//		results.put(NOTE_TEXT_LABEL, resultsNoteText);
//		results.put(ERROR_MESSAGE_LABEL, errorMessage);

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
	 * @param userId netid of the user who is deleting the note
     * @return Map containing an error message if any
	 *
	 * @see edu.cornell.kew.service.CornellKewService#deleteNote(java.lang.String, java.lang.String, java.lang.String)
     */
    public ErrorResponse deleteNote(String docId, String noteId, String userId) {
		//Map<String, Object> results = new HashMap<String, Object>(1);
		
        ErrorResponse results = new ErrorResponse();
        
        String errorMessage = "";

		try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);
			RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();

			// setup note
			NoteVO noteVO = getNote(routeHeader.getNotes(), noteId);
			workflowDocument.deleteNote(noteVO);

			//TODO: is this necessary?
			workflowDocument.saveRoutingData();

////	      update notes database based on notes and notesToDelete arrays in routeHeaderVO
//	        BeanConverter.updateNotes(routeHeader, routeHeader.getRouteHeaderId());
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
	 * @param annotation a comment associated with this request
     * @param nodeName name of the route node to return to
	 * @return Map including the standard set of return values
	 *
	 * @see edu.cornell.kew.service.CornellKewService#returnToPreviousNode(java.lang.String, java.lang.String)
	 */
    public StandardResponse returnToPreviousNode(String docId, String userId, String annotation, String nodeName) {
		//Map<String, Object> results;

        StandardResponse results;
        
       	try {
			WorkflowDocument workflowDocument = setupWorkflowDocument(docId, userId);

			workflowDocument.returnToPreviousNode(annotation, nodeName);
			results = createResults(workflowDocument);
		} catch (WorkflowException e) {
			results = createErrorResults("Workflow Error: " + e.getLocalizedMessage());
		}

		return results;
    }

    private NoteVO getNote(NoteVO[] notes, String noteId) {
    	NoteVO note = null;

    	if (notes != null){
	    	for (int i=0; i<notes.length; i++){
	    		if (notes[i].getNoteId().toString().equals(noteId)) {
	    			note = notes[i];
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
	 * @param userId KEW netid for the user associated with this document
	 * @return populated WorkflowDocument object
	 * @throws WorkflowException if something goes wrong
	 */
	private WorkflowDocument setupWorkflowDocument(String docId, String userId) throws WorkflowException {
		return setupWorkflowDocument(docId, userId, null, null);
	}

	/**
	 * Convenience method to setup workflow document without content.
	 *
	 * @param docId KEW document id for the document to setup
	 * @param userId KEW netid for the user associated with this document
	 * @param docTitle title for this document
	 * @return populated WorkflowDocument object
	 * @throws WorkflowException if something goes wrong
	 */
	private WorkflowDocument setupWorkflowDocument(String docId, String userId, String docTitle) throws WorkflowException {
		return setupWorkflowDocument(docId, userId, docTitle, null);
	}

	/**
	 * Instantiate and setup the WorkflowDocument object.
	 *
	 * @param docId KEW document id for the document to setup
	 * @param userId KEW netid for the user associated with this document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @return populated WorkflowDocument object
	 * @throws WorkflowException if something goes wrong
	 */
	private WorkflowDocument setupWorkflowDocument(String docId, String userId, String docTitle, String docContent) throws WorkflowException {
		UserIdVO userIdVO = new NetworkIdVO(userId);
		WorkflowDocument workflowDocument = new WorkflowDocument(userIdVO, Long.decode(docId));
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
				NoteVO note = (NoteVO)notes.get(i);
				//author, noteId, timestamp, noteText
				UserIdVO userIdVO = new WorkflowIdVO(note.getNoteAuthorWorkflowId());
				WorkflowInfo info = new WorkflowInfo();
				UserVO user = info.getWorkflowUser(userIdVO);
				//noteDetail.put(NOTE_AUTHOR_LABEL, user.getDisplayName());
				//noteDetail.put(NOTE_ID_LABEL, note.getNoteId().toString());
				//noteDetail.put(NOTE_TIMESTAMP_LABEL, formatCalendar(note.getNoteCreateDate()));
				//noteDetail.put(NOTE_TEXT_LABEL, note.getNoteText());

				noteDetail.setAuthor(user.getDisplayName());
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
	    
		RouteHeaderVO routeHeader = workflowDocument.getRouteHeader();

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
//		Map<String, Object> results = new HashMap<String, Object>(6);
	    StandardResponse response = new StandardResponse();
//		results.put(DOC_STATUS_LABEL, "");
//		results.put(CREATE_DATE_LABEL, "");
//		results.put(INITIATOR_ID_LABEL, "");
//        results.put(ROUTED_BY_USER_ID_LABEL, "");
//		results.put(APP_DOC_ID_LABEL, "");
//		results.put(INITIATOR_NAME_LABEL, "");
//        results.put(ROUTED_BY_USER_NAME_LABEL, "");
//		results.put(ERROR_MESSAGE_LABEL, errorMessage);
	    
	    response.setDocStatus("");
	    response.setCreateDate("");
	    response.setInitiatorId("");
	    response.setRoutedByUserId("");
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
	private StandardResponse createStandardResults(RouteHeaderVO routeHeader) {
		String docStatus = "";
		String createDate = "";
		String initiatorId = "";
        String routedByUserId = "";
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

			if (routeHeader.getInitiator() == null) {
				errorMessage += "Error: NULL Initiator; ";
			} else {
				initiatorId = routeHeader.getInitiator().getNetworkId();
				initiatorName = routeHeader.getInitiator().getDisplayName();
			}

            if (routeHeader.getRoutedByUser() == null) {
                // of the document has been routed, but there is no routed-by user, that is an error
                if (EdenConstants.ROUTE_HEADER_ENROUTE_CD.equals(routeHeader.getDocRouteStatus())) {
                        errorMessage += "Error: NULL routedBy user; ";
                }
            } else {
                routedByUserId = routeHeader.getRoutedByUser().getNetworkId();
                routedByUserName = routeHeader.getRoutedByUser().getDisplayName();
            }

			if (routeHeader.getAppDocId() != null) {
				appDocId = routeHeader.getAppDocId();
			}
		}

		//Map<String, Object> results = new HashMap<String, Object>(6);
		StandardResponse response = new StandardResponse();

//		results.put(DOC_STATUS_LABEL, docStatus);
//		results.put(CREATE_DATE_LABEL, createDate);
//		results.put(INITIATOR_ID_LABEL, initiatorId);
//        results.put(ROUTED_BY_USER_ID_LABEL, routedByUserId);
//		results.put(APP_DOC_ID_LABEL, appDocId);
//		results.put(INITIATOR_NAME_LABEL, initiatorName);
//        results.put(ROUTED_BY_USER_NAME_LABEL, routedByUserName);
//		results.put(ERROR_MESSAGE_LABEL, errorMessage);

		response.setDocStatus(docStatus);
		response.setCreateDate(createDate);
		response.setInitiatorId(initiatorId);
		response.setRoutedByUserId(routedByUserId);
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
		Timestamp dateCreated = Utilities.convertCalendar(calendar);
		formattedDate = dateFormat.format(dateCreated);

		return formattedDate;
	}

}
