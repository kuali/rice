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

import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

/**
 * SimpleDocumentActionsWebService is a simplified view into KEW that exposes those methods
 * that would be required by a basic client as a web service that is meant
 * to be as interoperable as possible (using simple types, etc.)
 *
 * The standard return is a simple structure containing a standard set of return values:
 * <ul>
 * <li>docStatus String - current status of document in KEW</li>
 * <li>createDate String - date document was created in KEW</li>
 * <li>initiatorId String - netid of document initiator</li>
 * <li>appDocId String - application specific document id</li>
 * <li>initiatorName String - display name of the document initiator</li>
 * <li>routedByUserId String - id of the user that routed the document (can be different from initiator)</li>
 * <li>routedByUserName String - display name of the user that routed the document (can be different from initiator)</li>
 * <li>errorMessage String - error message from KEW if any</li>
 * </ul>
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface SimpleDocumentActionsWebService {

    /**
     * Response object used for isUserInRouteLog method
     */
    public static class UserInRouteLogResponse extends ErrorResponse {
        protected String isUserInRouteLog;
        public String getIsUserInRouteLog() {
            return isUserInRouteLog;
        }
        public void setIsUserInRouteLog(String isUserInRouteLog) {
            this.isUserInRouteLog = isUserInRouteLog;
        }
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * Response object used for deleteNote method
     */
    public static class ErrorResponse {
        protected String errorMessage;
        public String getErrorMessage() {
            return errorMessage;
        }
        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * Response object for note-related methods
     */
    public static class NoteResponse extends ErrorResponse {
        protected String author;
        protected String noteId;
        protected String timestamp;
        protected String noteText;
        public String getAuthor() {
            return author;
        }
        public void setAuthor(String author) {
            this.author = author;
        }
        public String getNoteId() {
            return noteId;
        }
        public void setNoteId(String noteId) {
            this.noteId = noteId;
        }
        public String getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
        public String getNoteText() {
            return noteText;
        }
        public void setNoteText(String noteText) {
            this.noteText = noteText;
        }
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }
    
    /**
     * "Standard" response object
     */
    public static class StandardResponse extends ErrorResponse {
        protected String docStatus;
        protected String createDate;
        protected String initiatorId;
        protected String routedByUserId;
        protected String routedByUserName;
        protected String appDocId;
        protected String initiatorName;
        public String getDocStatus() {
            return docStatus;
        }
        public void setDocStatus(String docStatus) {
            this.docStatus = docStatus;
        }
        public String getCreateDate() {
            return createDate;
        }
        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }
        public String getInitiatorId() {
            return initiatorId;
        }
        public void setInitiatorId(String initiatorId) {
            this.initiatorId = initiatorId;
        }
        public String getAppDocId() {
            return appDocId;
        }
        public void setAppDocId(String appDocId) {
            this.appDocId = appDocId;
        }
        public String getInitiatorName() {
            return initiatorName;
        }
        public void setInitiatorName(String initiatorName) {
            this.initiatorName = initiatorName;
        }
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
        public String getRoutedByUserId() {
            return routedByUserId;
        }
        public void setRoutedByUserId(String routedByUserId) {
            this.routedByUserId = routedByUserId;
        }
        public String getRoutedByUserName() {
            return routedByUserName;
        }
        public void setRoutedByUserName(String routedByUserName) {
            this.routedByUserName = routedByUserName;
        }
    }

    /**
     * Response object used when creating or obtaining documents
     */
    public static class DocumentResponse extends StandardResponse {
        protected String docId;
        protected String docContent;
        protected String title;
        protected List<NoteDetail> notes;
        protected String actionRequested;
        public DocumentResponse() {}
        public DocumentResponse(StandardResponse standardResponse) {
            this.appDocId = standardResponse.getAppDocId();
            this.createDate = standardResponse.getCreateDate();
            this.docStatus = standardResponse.getDocStatus();
            this.errorMessage = standardResponse.getErrorMessage();
            this.initiatorId = standardResponse.getInitiatorId();
            this.initiatorName = standardResponse.getInitiatorName();
            this.routedByUserId = standardResponse.getRoutedByUserId();
            this.routedByUserName = standardResponse.getRoutedByUserName();
        }
        public String getDocContent() {
            return docContent;
        }
        public void setDocContent(String docContent) {
            this.docContent = docContent;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public List<NoteDetail> getNotes() {
            return notes;
        }
        public void setNotes(List<NoteDetail> notes) {
            this.notes = notes;
        }
        public String getActionRequested() {
            return actionRequested;
        }
        public void setActionRequested(String actionRequested) {
            this.actionRequested = actionRequested;
        }
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
        public String getDocId() {
            return docId;
        }
        public void setDocId(String docId) {
            this.docId = docId;
        }
    }
    
    /**
     * Response object encapsulating a note on a document
     */
    public static class NoteDetail {
        public String author;
        public String id;
        public String timestamp;
        public String noteText;
        
        public String getAuthor() {
            return author;
        }
        public void setAuthor(String author) {
            this.author = author;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
        public String getNoteText() {
            return noteText;
        }
        public void setNoteText(String noteText) {
            this.noteText = noteText;
        }
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    
	/**
	 * Create a KEW document.
	 *
	 * @param initiatorId netid of the document initiator
	 * @param appDocId application specific document id
	 * @param docType KEW document type for the document to be created
	 * @param docTitle title for this document
	 * @return DocumentResponse including the standard set of return values and the docId of the newly created document
	 */
	public DocumentResponse create(String initiatorId, String appDocId, String docType, String docTitle);

	/**
	 * Route a KEW document.
	 *
	 * @param docId KEW document id of the document to route
	 * @param userId netid of the user who is routing the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse route(String	docId, String userId, String docTitle, String docContent, String annotation);

	/**
	 * Approve the KEW document, in response to an approval action request.
	 *
	 * @param docId KEW document id of the document to approve
	 * @param userId netid of the user who is approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse approve(String docId, String userId, String docTitle, String docContent, String annotation);

	/**
	 * Blanket Approve the KEW document (all future approval requests will be satisfied),
	 * in response to an approval action request.  Can only be performed by a super user.
	 *
	 * @param docId KEW document id of the document to blanket approve
	 * @param userId netid of the user who is blanket approving the document
	 * @param docTitle title for this document
	 * @param docContent xml content for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse blanketApprove(String docId, String userId, String docTitle, String docContent, String annotation);

	/**
	 * Cancel the KEW document.
	 *
	 * @param docId KEW document id of the document to cancel
	 * @param userId netid of the user who is canceling the document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse cancel(String docId, String userId, String annotation);

	/**
	 * Disapprove the KEW document, in response to an approval action request.
	 *
	 * @param docId KEW document id of the document to disapprove
	 * @param userId netid of the user who is disapproving the document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse disapprove(String docId, String userId, String annotation);

	/**
	 * Acknowledge the KEW document, in response to an acknowledge action request.
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param userId netid of the user who is acknowledging the document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse acknowledge(String docId, String userId, String annotation);

	/**
	 * Clear an FYI request for this KEW document from the user's action list,
	 * in response to an FYI action request.
	 *
	 * @param docId KEW document id of the document to acknowledge
	 * @param userId netid of the user who is acknowledging the document
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse fyi(String docId, String userId);

	/**
	 * Save the KEW document, keeps it in the user's action list for completion later.
	 *
	 * @param docId KEW document id of the document to save
	 * @param userId netid of the user who is saving the document
	 * @param docTitle title for this document
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse save(String docId, String userId, String docTitle, String annotation);

	/**
	 * Create an Adhoc FYI request for another user for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientUserId netid of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocFyiToUser(String docId, String userId, String recipientUserId, String annotation);

	/**
	 * Create an Adhoc FYI request for another group for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocFyiToGroup(String docId, String userId, String recipientGroupId, String annotation);

	/**
	 * Create an Adhoc Acknowledge request for another user for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientUserId netid of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocAckToUser(String docId, String userId, String recipientUserId, String annotation);

	/**
	 * Create an Adhoc Acknowledge request for another group for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocAckToGroup(String docId, String userId, String recipientGroupId, String annotation);

	/**
	 * Create an Adhoc Approval request for another user for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientUserId netid of the user for whom the request is being created
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocApproveToUser(String docId, String userId, String recipientUserId, String annotation);

	/**
	 * Create an Adhoc Approval request for another group for this KEW document.
	 * NOTE: Must make a subsequent call to route in order for the action
	 * request to be created.  This allows the user to create multiple adhoc
	 * requests at the same time prior to routing.
	 *
	 * @param docId KEW document id of the document to create the adhoc request for
	 * @param userId netid of the user who is making this request
	 * @param recipientGroupId workgroupId of the group to create this request for
	 * @param annotation a comment associated with this request
	 * @return StandardResponse including the standard set of return values
	 */
	public StandardResponse requestAdHocApproveToGroup(String docId, String userId, String recipientGroupId, String annotation);

	/**
	 * Check to see if the user is associated with this KEW document.
	 * Useful for security purposes (if return is False, user shouldn't
	 * be able to see the document unless it's public.)
	 *
	 * @param docId KEW document id of the document to check
	 * @param userId netid of the user to check
	 * @return UserInRouteLogResponse containing True/False for isUserInRouteLog and an error message if
	 * a problem occured
	 */
	public UserInRouteLogResponse isUserInRouteLog(String docId, String userId);

	/**
	 * Retrieve a KEW document based on the docId and userId passed in, and return the
	 * information about the document.
	 *
	 * @param docId KEW document id of the document to retrieve information about
	 * @param userId netid of the user to retrieve the document for
	 * @return DocumentResponse including the standard set of return values, the xml document content, 
	 * the title, the action requested ( Approve, Aknowledge, Fyi, Complete ) and an 
	 * array of Maps containing the following for each Note (author, noteId, timestamp, 
	 * noteText).
	 */
	public DocumentResponse getDocument(String docId, String userId);

    /**
     * Add a note (possibly including a binary attachment) to this KEW document.
     *
	 * @param docId KEW document id of the document to add the note to
	 * @param userId netid of the user who is adding the note
     * @param noteText text of the note
     * @return NoteResponse containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
     */
    public NoteResponse addNote(String docId, String userId, String noteText);

    /**
     * Update an existing note (possibly including a binary attachment) to this KEW document.
     *
	 * @param docId KEW document id of the document to update the note for
	 * @param noteId the id of the note to update
	 * @param userId netid of the user who is updating the note
     * @param noteText text of the note if changed
     * @return NoteResponse containing relevant note information (author, noteId, timestamp, noteText)
     * along with an error message (if any)
     */
    public NoteResponse updateNote(String docId, String noteId, String userId, String noteText);

    /**
     * Delete an existing note.
     *
	 * @param docId KEW document id of the document to delete the note from
     * @param noteId the id of the note to delete
	 * @param userId netid of the user who is deleting the note
     * @return ErrorResponse containing an error message if any
     */
    public ErrorResponse deleteNote(String docId, String noteId, String userId);

	/**
	 * Return a KEW document to a previous route node.  This method should
	 * be used with caution.
	 *
	 * @param docId KEW document id of the document to return to a previous node
	 * @param userId netid of the user who is requesting this action
	 * @param annotation a comment associated with this request
     * @param nodeName name of the route node to return to
	 * @return StandardResponse including the standard set of return values
	 */
    public StandardResponse returnToPreviousNode(String docId, String userId, String annotation, String nodeName);

}
