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
package org.kuali.rice.kew.webservice;



/**
 * SimpleDocumentActionsWebService is a simplified view into KEW that exposes those methods
 * that would be required by a basic client as a web service that is meant
 * to be as interoperable as possible (using simple types, etc.)
 *
 * The standard return is a simple structure containing a standard set of return values:
 * <ul>
 * <li>docStatus String - current status of document in KEW</li>
 * <li>createDate String - date document was created in KEW</li>
 * <li>initiatorPrincipalId String - principal id of document initiator</li>
 * <li>appDocId String - application specific document id</li>
 * <li>initiatorName String - display name of the document initiator</li>
 * <li>routedByprincipalId String - id of the user that routed the document (can be different from initiator)</li>
 * <li>routedByUserName String - display name of the user that routed the document (can be different from initiator)</li>
 * <li>errorMessage String - error message from KEW if any</li>
 * </ul>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface SimpleDocumentActionsWebService {

    /**
	 * Create a KEW document.
	 *
	 * @param initiatorPrincipalId principal id of the document initiator
	 * @param appDocId application specific document id
	 * @param docType KEW document type for the document to be created
	 * @param docTitle title for this document
	 * @return DocumentResponse including the standard set of return values and the docId of the newly created document
	 */
	public DocumentResponse create(String initiatorPrincipalId, String appDocId, String docType, String docTitle);

	/**
	 * Route a KEW document.
	 *
	 * @param docId KEW document id of the document to route
	 * @param principalId principal id of the user who is routing the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse route(String docId, String principalId, String docTitle, String docContent, String annotation);

	/**
	 * Approve the KEW document, in response to an approval action request.
	 *
	 * @param docId KEW document id of the document to approve
	 * @param principalId principal id of the user who is approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse approve(String docId, String principalId, String docTitle, String docContent, String annotation);

	/**
	 * Blanket Approve the KEW document (all future approval requests will be satisfied),
	 * in response to an approval action request.  Can only be performed by a super user.
	 *
	 * @param docId KEW document id of the document to blanket approve
	 * @param principalId principal id of the user who is blanket approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse blanketApprove(String docId, String principalId, String docTitle, String docContent, String annotation);

	/**
	 * Cancel the KEW document.
	 *
	 * @param docId KEW document id of the document to cancel
	 * @param principalId principal id of the user who is canceling the document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse cancel(String docId, String principalId, String annotation);

	/**
	 * Disapprove the KEW document, in response to an approval action request.
	 *
	 * @param docId KEW document id of the document to disapprove
	 * @param principalId principal id of the user who is disapproving the document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse disapprove(String docId, String principalId, String annotation);

	/**
	 * Acknowledge the KEW document, in response to an acknowledge action request.
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param principalId principal id of the user who is acknowledging the document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse acknowledge(String docId, String principalId, String annotation);

	/**
	 * Clear an FYI request for this KEW document from the user's action list,
	 * in response to an FYI action request.
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param principalId principal id of the user who is acknowledging the document
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse fyi(String docId, String principalId);

	/**
	 * Save the KEW document, keeps it in the user's action list for completion later.
	 *
	 * @param docId KEW document id of the document to save
	 * @param principalId principal id of the user who is saving the document
	 * @param docTitle title for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse save(String docId, String principalId, String docTitle, String annotation);

	/**
	 * Create an Adhoc FYI request for another user for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientPrincipalId principal id of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocFyiToPrincipal(String docId, String principalId, String recipientPrincipalId, String annotation);

	/**
	 * Create an Adhoc FYI request for another group for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientGroupId group id of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocFyiToGroup(String docId, String principalId, String recipientGroupId, String annotation);

	/**
	 * Create an Adhoc Acknowledge request for another user for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientPrincipalId principal id of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocAckToPrincipal(String docId, String principalId, String recipientPrincipalId, String annotation);

	/**
	 * Create an Adhoc Acknowledge request for another group for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientGroupId group id of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocAckToGroup(String docId, String principalId, String recipientGroupId, String annotation);

	/**
	 * Create an Adhoc Approval request for another user for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientPrincipalId principal id of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocApproveToPrincipal(String docId, String principalId, String recipientPrincipalId, String annotation);

	/**
	 * Create an Adhoc Approval request for another group for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param principalId principal id of the user who is making this request
	 * @param recipientGroupId group id of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocApproveToGroup(String docId, String principalId, String recipientGroupId, String annotation);

	/**
	 * Check to see if the user is associated with this KEW document.
	 * Useful for security purposes (if return is False, user shouldn't
	 * be able to see the document unless it's public.)
	 *
	 * @param docId KEW document id of the document to check
	 * @param principalId principal id of the user to check
	 * @return UserInRouteLogResponse containing True/False for isUserInRouteLog and an error message if
	 * a problem occured
	 */
	public UserInRouteLogResponse isUserInRouteLog(String docId, String principalId);

	/**
	 * Retrieve a KEW document based on the docId and principalId passed in, and return the
	 * information about the document.
	 *
	 * @param docId KEW document id of the document to retrieve information about
	 * @param principalId principal id of the user to retrieve the document for
	 * @return DocumentResponse including the standard set of return values, the xml document content, 
	 * the title, the action requested ( Approve, Aknowledge, Fyi, Complete ) and an 
	 * array of Maps containing the following for each Note (author, noteId, timestamp, 
	 * noteText).
	 */
	public DocumentResponse getDocument(String docId, String principalId);

    /**
     * Add a note (possibly including a binary attachment) to this KEW document.
     *
	 * @param docId KEW document id of the document to add the note to
	 * @param principalId principal id of the user who is adding the note
     * @param noteText text of the note
     * @return NoteResponse containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
     */
    public NoteResponse addNote(String docId, String principalId, String noteText);

    /**
     * Update an existing note (possibly including a binary attachment) to this KEW document.
     *
	 * @param docId KEW document id of the document to update the note for
	 * @param noteId the id of the note to update
	 * @param principalId principal id of the user who is updating the note
     * @param noteText text of the note if changed
     * @return NoteResponse containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
     */
    public NoteResponse updateNote(String docId, String noteId, String principalId, String noteText);

    /**
     * Delete an existing note.
     *
	 * @param docId KEW document id of the document to delete the note from
     * @param noteId the id of the note to delete
	 * @param principalId principal id of the user who is deleting the note
     * @return ErrorResponse containing an error message if any
     */
    public ErrorResponse deleteNote(String docId, String noteId, String principalId);

	/**
	 * Return a KEW document to a previous route node.  This method should
	 * be used with caution.
	 *
	 * @param docId KEW document id of the document to return to a previous node
	 * @param principalId principal id of the user who is requesting this action
	 * @param annotation a comment associated with this request
     * @param nodeName name of the route node to return to
	 * @return StandardResponse including the standard set of return values
	 */
    public StandardResponse returnToPreviousNode(String docId, String principalId, String annotation, String nodeName);

}
