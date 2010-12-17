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
package org.kuali.rice.kew.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.kuali.rice.kew.util.KEWWebServiceConstants;

/**
 * SimpleDocumentActionsWebService is a simplified view into KEW that exposes those methods that would be required by a basic
 * client as a web service that is meant to be as interoperable as possible (using simple types, etc.) The standard return is
 * a simple structure containing a standard set of return values:
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
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@WebService(name = KEWWebServiceConstants.SimpleDocumentActionsWebService.WEB_SERVICE_NAME, targetNamespace = KEWWebServiceConstants.MODULE_TARGET_NAMESPACE)
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface SimpleDocumentActionsWebService {

    /**
     * Create a KEW document.
     * 
     * @param initiatorPrincipalId
     *            principal id of the document initiator
     * @param appDocId
     *            application specific document id
     * @param docType
     *            KEW document type for the document to be created
     * @param docTitle
     *            title for this document
     * @return DocumentResponse including the standard set of return values and the docId of the newly created document
     */
    public DocumentResponse create( @WebParam(name = "initiatorPrincipalId") String initiatorPrincipalId,
                                    @WebParam(name = "appDocId") String appDocId,
                                    @WebParam(name = "docType") String docType,
                                    @WebParam(name = "docTitle") String docTitle);

    /**
     * Route a KEW document.
     * 
     * @param docId
     *            KEW document id of the document to route
     * @param principalId
     *            principal id of the user who is routing the document
     * @param docTitle
     *            title for this document
     * @param docContent
     *            xml content for this document
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse route(  @WebParam(name = "docId") String docId,
                                    @WebParam(name = "principalId")String principalId,
                                    @WebParam(name = "docTitle")String docTitle,
                                    @WebParam(name = "docContent")String docContent,
                                    @WebParam(name = "annotation")String annotation);

    /**
     * Approve the KEW document, in response to an approval action request.
     * 
     * @param docId
     *            KEW document id of the document to approve
     * @param principalId
     *            principal id of the user who is approving the document
     * @param docTitle
     *            title for this document
     * @param docContent
     *            xml content for this document
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse approve(    @WebParam(name = "docId") String docId,
                                        @WebParam(name = "principalId") String principalId,
                                        @WebParam(name = "docTitle") String docTitle,
                                        @WebParam(name = "docContent") String docContent,
                                        @WebParam(name = "annotation") String annotation);

    /**
     * Blanket Approve the KEW document (all future approval requests will be satisfied), in response to an approval action
     * request. Can only be performed by a super user.
     * 
     * @param docId
     *            KEW document id of the document to blanket approve
     * @param principalId
     *            principal id of the user who is blanket approving the document
     * @param docTitle
     *            title for this document
     * @param docContent
     *            xml content for this document
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse blanketApprove( @WebParam(name = "docId") String docId,
                                            @WebParam(name = "principalId") String principalId,
                                            @WebParam(name = "docTitle") String docTitle,
                                            @WebParam(name = "docContent") String docContent,
                                            @WebParam(name = "annotation") String annotation);

    /**
     * Cancel the KEW document.
     * 
     * @param docId
     *            KEW document id of the document to cancel
     * @param principalId
     *            principal id of the user who is canceling the document
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse cancel( @WebParam(name = "docId") String docId, 
                                    @WebParam(name = "principalId") String principalId,
                                    @WebParam(name = "annotation") String annotation);

    /**
     * Disapprove the KEW document, in response to an approval action request.
     * 
     * @param docId
     *            KEW document id of the document to disapprove
     * @param principalId
     *            principal id of the user who is disapproving the document
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse disapprove( @WebParam(name = "docId") String docId,
                                        @WebParam(name = "principalId") String principalId,
                                        @WebParam(name = "annotation") String annotation);

    /**
     * Acknowledge the KEW document, in response to an acknowledge action request.
     * 
     * @param docId
     *            KEW document id of the document to acknowledge
     * @param principalId
     *            principal id of the user who is acknowledging the document
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse acknowledge(    @WebParam(name = "docId") String docId,
                                            @WebParam(name = "principalId") String principalId,
                                            @WebParam(name = "annotation") String annotation);

    /**
     * Clear an FYI request for this KEW document from the user's action list, in response to an FYI action request.
     * 
     * @param docId
     *            KEW document id of the document to acknowledge
     * @param principalId
     *            principal id of the user who is acknowledging the document
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse fyi(    @WebParam(name = "docId") String docId,
                                    @WebParam(name = "principalId") String principalId);

    /**
     * Save the KEW document, keeps it in the user's action list for completion later.
     * 
     * @param docId
     *            KEW document id of the document to save
     * @param principalId
     *            principal id of the user who is saving the document
     * @param docTitle
     *            title for this document
     * @param docContent
     *            xml content for this document
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse save(   @WebParam(name = "docId") String docId,
                                    @WebParam(name = "principalId") String principalId,
                                    @WebParam(name = "docTitle") String docTitle,
                                    @WebParam(name = "docContent") String docContent,
                                    @WebParam(name = "annotation") String annotation);

    /**
     * Save the KEW document content and update the docTitle if fields are non-null.
     * 
     * @param docId
     *            KEW document id of the document to save
     * @param principalId
     *            principal id of the user who is saving the document
     * @param docTitle
     *            title for this document
     * @param docContent
     *            xml content for this document
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse saveDocumentContent(    @WebParam(name = "docId") String docId,
                                                    @WebParam(name = "principalId") String principalId,
                                                    @WebParam(name = "docTitle") String docTitle,
                                                    @WebParam(name = "docContent") String docContent);

    /**
     * Create an Adhoc FYI request for another user for this KEW document. NOTE: Must make a subsequent call to route in
     * order for the action request to be created. This allows the user to create multiple adhoc requests at the same time
     * prior to routing.
     * 
     * @param docId
     *            KEW document id of the document to create the adhoc request for
     * @param principalId
     *            principal id of the user who is making this request
     * @param recipientPrincipalId
     *            principal id of the user for whom the request is being created
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse requestAdHocFyiToPrincipal( @WebParam(name = "docId") String docId,
                                                        @WebParam(name = "principalId") String principalId,
                                                        @WebParam(name = "recipientPrincipalId") String recipientPrincipalId,
                                                        @WebParam(name = "annotation") String annotation);

    /**
     * Create an Adhoc FYI request for another group for this KEW document. NOTE: Must make a subsequent call to route in
     * order for the action request to be created. This allows the user to create multiple adhoc requests at the same time
     * prior to routing.
     * 
     * @param docId
     *            KEW document id of the document to create the adhoc request for
     * @param principalId
     *            principal id of the user who is making this request
     * @param recipientGroupId
     *            group id of the group to create this request for
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse requestAdHocFyiToGroup( @WebParam(name = "docId") String docId,
                                                    @WebParam(name = "principalId") String principalId,
                                                    @WebParam(name = "recipientGroupId") String recipientGroupId,
                                                    @WebParam(name = "annotation") String annotation);

    /**
     * Create an Adhoc Acknowledge request for another user for this KEW document. NOTE: Must make a subsequent call to route
     * in order for the action request to be created. This allows the user to create multiple adhoc requests at the same time
     * prior to routing.
     * 
     * @param docId
     *            KEW document id of the document to create the adhoc request for
     * @param principalId
     *            principal id of the user who is making this request
     * @param recipientPrincipalId
     *            principal id of the user for whom the request is being created
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse requestAdHocAckToPrincipal( @WebParam(name = "docId") String docId,
                                                        @WebParam(name = "principalId") String principalId,
                                                        @WebParam(name = "recipientPrincipalId") String recipientPrincipalId,
                                                        @WebParam(name = "annotation") String annotation);

    /**
     * Create an Adhoc Acknowledge request for another group for this KEW document. NOTE: Must make a subsequent call to
     * route in order for the action request to be created. This allows the user to create multiple adhoc requests at the
     * same time prior to routing.
     * 
     * @param docId
     *            KEW document id of the document to create the adhoc request for
     * @param principalId
     *            principal id of the user who is making this request
     * @param recipientGroupId
     *            group id of the group to create this request for
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse requestAdHocAckToGroup( @WebParam(name = "docId") String docId,
                                                    @WebParam(name = "principalId") String principalId,
                                                    @WebParam(name = "recipientGroupId") String recipientGroupId,
                                                    @WebParam(name = "annotation") String annotation);

    /**
     * Create an Adhoc Approval request for another user for this KEW document. NOTE: Must make a subsequent call to route in
     * order for the action request to be created. This allows the user to create multiple adhoc requests at the same time
     * prior to routing.
     * 
     * @param docId
     *            KEW document id of the document to create the adhoc request for
     * @param principalId
     *            principal id of the user who is making this request
     * @param recipientPrincipalId
     *            principal id of the user for whom the request is being created
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse requestAdHocApproveToPrincipal( @WebParam(name = "docId") String docId,
                                                            @WebParam(name = "principalId") String principalId,
                                                            @WebParam(name = "recipientPrincipalId") String recipientPrincipalId,
                                                            @WebParam(name = "annotation") String annotation);

    /**
     * Create an Adhoc Approval request for another group for this KEW document. NOTE: Must make a subsequent call to route
     * in order for the action request to be created. This allows the user to create multiple adhoc requests at the same time
     * prior to routing.
     * 
     * @param docId
     *            KEW document id of the document to create the adhoc request for
     * @param principalId
     *            principal id of the user who is making this request
     * @param recipientGroupId
     *            group id of the group to create this request for
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse requestAdHocApproveToGroup( @WebParam(name = "docId") String docId,
                                                        @WebParam(name = "principalId") String principalId,
                                                        @WebParam(name = "recipientGroupId") String recipientGroupId,
                                                        @WebParam(name = "annotation") String annotation);

    /**
     * Check to see if the user is associated with this KEW document. Useful for security purposes (if return is False, user
     * shouldn't be able to see the document unless it's public.)
     * 
     * @param docId
     *            KEW document id of the document to check
     * @param principalId
     *            principal id of the user to check
     * @return UserInRouteLogResponse containing True/False for isUserInRouteLog and an error message if a problem occured
     */
    public UserInRouteLogResponse isUserInRouteLog( @WebParam(name = "docId") String docId,
                                                    @WebParam(name = "principalId") String principalId);

    /**
     * Retrieve a KEW document based on the docId and principalId passed in, and return the information about the document.
     * 
     * @param docId
     *            KEW document id of the document to retrieve information about
     * @param principalId
     *            principal id of the user to retrieve the document for
     * @return DocumentResponse including the standard set of return values, the xml document content, the title, the action
     *         requested ( Approve, Aknowledge, Fyi, Complete ) and an array of Maps containing the following for each Note
     *         (author, noteId, timestamp, noteText).
     */
    public DocumentResponse getDocument(    @WebParam(name = "docId") String docId,
                                            @WebParam(name = "principalId") String principalId);

    /**
     * Add a note (possibly including a binary attachment) to this KEW document.
     * 
     * @param docId
     *            KEW document id of the document to add the note to
     * @param principalId
     *            principal id of the user who is adding the note
     * @param noteText
     *            text of the note
     * @return NoteResponse containing relevant note information (author, noteId, timestamp, noteText) along with an error
     *         message (if any)
     */
    public NoteResponse addNote(    @WebParam(name = "docId") String docId,
                                    @WebParam(name = "principalId") String principalId,
                                    @WebParam(name = "noteText") String noteText);

    /**
     * Update an existing note (possibly including a binary attachment) to this KEW document.
     * 
     * @param docId
     *            KEW document id of the document to update the note for
     * @param noteId
     *            the id of the note to update
     * @param principalId
     *            principal id of the user who is updating the note
     * @param noteText
     *            text of the note if changed
     * @return NoteResponse containing relevant note information (author, noteId, timestamp, noteText) along with an error
     *         message (if any)
     */
    public NoteResponse updateNote( @WebParam(name = "docId") String docId,
                                    @WebParam(name = "noteId") String noteId,
                                    @WebParam(name = "principalId") String principalId,
                                    @WebParam(name = "noteText") String noteText);

    /**
     * Delete an existing note.
     * 
     * @param docId
     *            KEW document id of the document to delete the note from
     * @param noteId
     *            the id of the note to delete
     * @param principalId
     *            principal id of the user who is deleting the note
     * @return ErrorResponse containing an error message if any
     */
    public ErrorResponse deleteNote(    @WebParam(name = "docId") String docId,
                                        @WebParam(name = "noteId") String noteId,
                                        @WebParam(name = "principalId") String principalId);

    /**
     * Return a KEW document to a previous route node. This method should be used with caution.
     * 
     * @param docId
     *            KEW document id of the document to return to a previous node
     * @param principalId
     *            principal id of the user who is requesting this action
     * @param annotation
     *            a comment associated with this request
     * @param nodeName
     *            name of the route node to return to
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse returnToPreviousNode(   @WebParam(name = "docId") String docId,
                                                    @WebParam(name = "principalId") String principalId,
                                                    @WebParam(name = "annotation") String annotation,
                                                    @WebParam(name = "nodeName") String nodeName);

    /**
     * Return a KEW document to a previous route node. This method should be used with caution.
     * 
     * @param docId
     *            KEW document id of the document to return to a previous node
     * @param principalId
     *            principal id of the user who is requesting this action
     * @param annotation
     *            a comment associated with this request
     * @param nodeName
     *            name of the route node to return to
     * @param docTitle
     *            title for this document
     * @param docContent
     *            xml content for this document
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse returnToPreviousNodeWithUpdates(    @WebParam(name = "docId") String docId,
                                                                @WebParam(name = "principalId") String principalId,
                                                                @WebParam(name = "annotation") String annotation,
                                                                @WebParam(name = "nodeName") String nodeName,
                                                                @WebParam(name = "docTitle") String docTitle,
                                                                @WebParam(name = "docContent") String docContent);

    /**
     * This method revokes all AdHoc requests set on the document at the specified node.
     * 
     * @param docId
     *            KEW document id of the document
     * @param principalId
     *            principal id of the user who is revoking the requests
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param nodeName
     *            name of the route node that adhoc requests should be revoked from
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse revokeAdHocRequestsByNodeName(String docId, String principalId, String docTitle, String docContent, String nodeName, String annotation);

    /**
     * This method revokes all AdHoc requests set on the document for the specified principal.
     * 
     * @param docId
     *            KEW document id of the document
     * @param principalId
     *            principal id of the user who is revoking the requests
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param adhocPrincipalId
     *            principal ID of the principal that should have all their adhoc requests revoked
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse revokeAdHocRequestsByPrincipalId(String docId, String principalId, String docTitle, String docContent, String adhocPrincipalId, String annotation);

    /**
     * This method revokes all AdHoc requests set on the document for a specified group.
     * 
     * @param docId
     *            KEW document id of the document that is being returned
     * @param principalId
     *            principal id of the user who is revoking the requests
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param groupId
     *            groupId of the group that should have adhoc requests revoked
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse revokeAdHocRequestsByGroupId(String docId, String principalId, String docTitle, String docContent, String groupId, String annotation);

    /**
     * This method revokes the AdHoc request set on the document for the specified action request.
     * 
     * @param docId
     *            KEW document id of the document
     * @param principalId
     *            principal id of the user who is revoking the requests
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param actionRequestId
     *            the id of the adhoc request to be revoked
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse revokeAdHocRequestsByActionRequestId(String docId, String principalId, String docTitle, String docContent, String actionRequestId, String annotation);

    /**
     * This method will super user approve the document.
     * 
     * @param docId
     *            KEW document id of the document
     * @param superUserPrincipalId
     *            principal id of the super user who is taking the action
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse superUserApprove(String docId, String superUserPrincipalId, String docTitle, String docContent, String annotation);

    /**
     * This method will super user disapprove the document.
     * 
     * @param docId
     *            KEW document id of the document
     * @param superUserPrincipalId
     *            principal id of the super user who is taking the action
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse superUserDisapprove(String docId, String superUserPrincipalId, String docTitle, String docContent, String annotation);

    /**
     * This method will super user cancel the document.
     * 
     * @param docId
     *            KEW document id of the document
     * @param superUserPrincipalId
     *            principal id of the super user who is taking the action
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse superUserCancel(String docId, String superUserPrincipalId, String docTitle, String docContent, String annotation);

    /**
     * Return a KEW document to a previous route node. This method should be used with caution.
     * 
     * @param docId
     *            KEW document id of the document to return to a previous node
     * @param superUserPrincipalId
     *            principal id of the super user who is requesting this action
     * @param docTitle
     *            title for this document (updated if non-null)
     * @param docContent
     *            xml content for this document (updated if non-null)
     * @param nodeName
     *            name of the route node to return to
     * @param annotation
     *            a comment associated with this request
     * @return StandardResponse including the standard set of return values
     */
    public StandardResponse superUserReturnToPrevious(String docId, String superUserPrincipalId, String docTitle, String docContent, String nodeName, String annotation);

}
