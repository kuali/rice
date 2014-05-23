/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.DocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveEvent;

import java.util.List;

/**
 * Defines various operations that support the Document framework.
 *
 * The calling code should always use any returned Document object for future operations since a new object will be
 * created if a passed-in document is saved.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DocumentService {

    /**
     * @param documentHeaderId
     * @return true if a document with the given documentHeaderId exists
     */
    boolean documentExists(String documentHeaderId);

    /**
     * get a new blank document instance based on the document type name
     *
     * @param documentTypeName
     * @return new document instance
     */
    Document getNewDocument(String documentTypeName) throws WorkflowException;

    /**
     * get a new blank document instance having the given Document class
     *
     * @param documentClass
     * @return new document instance
     */
    Document getNewDocument(Class<? extends Document> documentClass) throws WorkflowException;

    /**
     * get a new blank document instance based on the document type name. The principal name
     * passed in will be used as the document initiator.
     *
     * @param documentTypeName
     * @param initiatorPrincipalNm
     * @return new document instance
     */
    Document getNewDocument(String documentTypeName, String initiatorPrincipalNm) throws WorkflowException;

    /**
     * get a document based on the document header id which is the primary key for all document types
     *
     * @param documentHeaderId
     * @return document, by id
     */
    Document getByDocumentHeaderId(String documentHeaderId) throws WorkflowException;

    /**
     * get a document based on the document header id which is the primary key for all document types.  Using this
     * method does not require that GlobalVariables.getUserSession() be populated.  Therefore, this method can be used
     * when a HTTP request is not being processed (e.g. during workflow indexing/post-processing).
     *
     * @param documentHeaderId
     * @return document, by id
     */
    Document getByDocumentHeaderIdSessionless(String documentHeaderId) throws WorkflowException;

    /**
     * This method retrieves a list of fully-populated documents given a list of document header id values.
     *
     * @param documentClass
     * @param documentHeaderIds
     * @return list of fully-populated documents
     * @throws WorkflowException
     */
    List<Document> getDocumentsByListOfDocumentHeaderIds(Class<? extends Document> documentClass,
            List<String> documentHeaderIds) throws WorkflowException;

    /**
     * This method is to allow for documents to be updated.  It is currently used to update the document status as
     * well as to allow for locked docs to be unlocked
     *
     * @param document the document to be updated
     * @return the updated document
     */
    Document updateDocument(Document document);

    /**
     * This is a helper method that performs the same as the {@link #saveDocument(Document, Class)} method.  The
     * convenience of this method is that the event being used is the standard SaveDocumentEvent.
     *
     * @see org.kuali.rice.krad.service.DocumentService#saveDocument(Document, Class)
     */
    Document saveDocument(Document document) throws WorkflowException;

    /**
     * This method saves the given document using the document event passed in.
     *
     * @see org.kuali.rice.krad.service.DocumentService#saveDocument(Document, Class)
     */
    Document saveDocument(Document document, DocumentEvent docEvent) throws WorkflowException;

    /**
     * Saves the passed-in document. This will persist it both to the Kuali database, and also initiate it
     * (if necessary) within workflow, so its available in the initiator's action list.  This method uses the
     * passed in DocumentEvent class when saving the document.  The DocumentEvent class must implement
     * the {@link SaveEvent} interface.
     *
     * Note that the system does not support passing in Workflow Annotations or AdHoc Route Recipients on a SaveDocument
     * call. These are sent to workflow on a routeDocument action, or any of the others which actually causes a
     * routing action to happen in workflow.
     *
     * Also note that this method will not check the document action flags to check if a save is valid
     *
     * The calling code should always use the object returned from this method for future operations since a new
     * object is created when the passed-in document is saved.
     *
     * @param document the document to be saved
     * @param kualiDocumentEventClass the event class to use when saving (class must implement the SaveEvent interface)
     * @return the saved document
     * @throws WorkflowException
     */
    Document saveDocument(Document document,
            Class<? extends DocumentEvent> kualiDocumentEventClass) throws WorkflowException;

    /**
     * Save and then route the document, optionally providing an annotation which will show up in the route log
     * of the document for the action taken, and optionally providing a list of ad hoc recipients for the document.
     *
     * @param document the document to be routed
     * @param annotation the annotation to appear in the route log of the document
     * @param adHocRoutingRecipients list of ad hoc recipients to which the document will be routed
     * @return the saved and routed document
     * @throws WorkflowException
     */
    Document routeDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRoutingRecipients) throws WorkflowException;

    /**
     * Save and then approve the document, optionally providing an annotation which will show up in the route log
     * of the document for the action taken, and optionally providing a list of ad hoc recipients for the document.
     *
     * @param document the document to be approved
     * @param annotation the annotation to appear in the route log of the document
     * @param adHocRoutingRecipients list of ad hoc recipients to which the document will be routed
     * @return the saved and approved document
     * @throws WorkflowException
     */
    Document approveDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRoutingRecipients) throws WorkflowException;

    /**
     * Save and then approve the document as a super user, optionally providing an annotation which will show up in the
     * route log of the document for the action taken.
     *
     * @param document the document to be super user approved
     * @param annotation the annotation to appear in the route log of the document
     * @return the saved and super user approved document
     * @throws WorkflowException
     */
    Document superUserApproveDocument(Document document, String annotation) throws WorkflowException;

     /**
     * Save and then cancel the document as a super user, optionally providing an annotation which will show up in the
     * route log of the document for the action taken.
     *
     * @param document the document to be super user canceled
     * @param annotation the annotation to appear in the route log of the document
     * @return the saved and super user canceled document
     * @throws WorkflowException
     */
    Document superUserCancelDocument(Document document, String annotation) throws WorkflowException;

    /**
     * Save and then disapprove the document as a super user, optionally providing an annotation which will show up
     * in the route log of the document for the action taken.
     *
     * @param document the document to be super user disapproved
     * @param annotation the annotation to appear in the route log of the document
     * @return the saved and super user disapproved document
     * @throws WorkflowException
     */
    Document superUserDisapproveDocument(Document document, String annotation) throws WorkflowException;

    /**
     * Disapprove the document as super user, without saving, optionally providing an annotation which will show
     * up in the route log of the document for the action taken.
     *
     * @param document the document to be super user disapproved
     * @param annotation the annotation to appear in the route log of the document
     * @return the super user disapproved document
     * @throws WorkflowException
     */
    Document superUserDisapproveDocumentWithoutSaving(Document document,
            String annotation) throws WorkflowException;

    /**
     * Disapprove the document, without saving, optionally providing an annotation which will show up in the route log
     * of the document for the action taken.
     *
     * @param document the document to be disapproved
     * @param annotation the annotation to appear in the route log of the document
     * @return the disapproved document
     * @throws Exception
     */
    Document disapproveDocument(Document document, String annotation) throws Exception;

    /**
     * Cancel the document, without saving, optionally providing an annotation for the disapproval which will show
     * up in the route log of the document for the action taken.
     *
     * @param document the document to be canceled
     * @param annotation the annotation to appear in the route log of the document
     * @return the canceled document
     * @throws WorkflowException
     */
    Document cancelDocument(Document document, String annotation) throws WorkflowException;

    /**
     * Acknowledge the document, optionally providing an annotation for the acknowledgement which will show up in the
     * route log of the document, and optionally providing a list of ad hoc recipients for the document.  The list of
     * ad hoc recipients for this document should have an action requested of acknowledge or fyi as all other actions
     * requested will be discarded as invalid due to the fact that this action being taken is an acknowledgement.
     *
     * @param document the document to be acknowledged
     * @param annotation the annotation to appear in the route log of the document
     * @param adHocRecipients list of ad hoc recipients to which the document will be routed
     * @return the acknowledged document
     * @throws WorkflowException
     */
    Document acknowledgeDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException;

    /**
     * Blanket approve the document which will save the document, approve the document, and stand in for an
     * approve for all typically generated approval actions requested for this document. The user must have blanket
     * approval authority for this document by being registered as a user in the blanket approval workgroup that is
     * associated with this document type.  Optionally an annotation can be provided which will show up for the
     * action taken on the document in the route log. Also optionally a list of ad hoc recipients can be provided
     * for the document, which should be restricted to actions requested of acknowledge and fyi as all other actions
     * requested will be discarded.
     *
     * @param document the document to be blanket approved
     * @param annotation the annotation to appear in the route log of the document
     * @param adHocRecipients list of ad hoc recipients to which the document will be routed
     * @return the saved and blanket approved document
     * @throws WorkflowException
     */
    Document blanketApproveDocument(Document document, String annotation,
            List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException;

    /**
     * Clear the fyi requests for the document, optionally providing a list of ad hoc recipients for the document,
     * which should be restricted to action requested of fyi as all other actions requested will be discarded.
     *
     * @param document the document to clear of fyi requests
     * @param adHocRecipients list of ad hoc recipients to which the document will be routed
     * @return the document
     * @throws WorkflowException
     */
    Document clearDocumentFyi(Document document,
            List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException;

    /**
     * Sets the title and app document id in the workflow document
     *
     * @param document the document to prepare
     * @throws WorkflowException
     */
    void prepareWorkflowDocument(Document document) throws WorkflowException;

    /**
     * This method creates a note from the given document and note text.  The resulting Note will
     * have it's note type set to the value of {@link Document#getNoteType()}.  Additionally, it's
     * remoteObjectId will be set to the object id of the document's note target.
     *
     * @param document the document from which to use the note type and note target when creating the note
     * @param text the text value to include in the resulting note
     * @return the note that was created
     */
    Note createNoteFromDocument(Document document, String text);

    /**
     * Saves the notes associated with the given document if they are in a state where they can be
     * saved.  In certain cases they may not be ready to be saved.  For example, in maintenance documents
     * where the notes are associated with the business object instead of the document header, the notes
     * cannot be saved until the business object itself has been persisted.
     *
     * @param document the document for which to save notes
     * @return true if the notes were saved, false if they were not
     */
    boolean saveDocumentNotes(Document document);

    /**
     * Send ad hoc requests for the given document, optionally providing an annotation which will show up in the route
     * log of the document.  Also optionally providing a list of ad hoc recipients for the document. However if
     * no ad hoc recipients are provided, no ad hoc requests will be sent.
     *
     * @param document the document for which the ad hoc requests are sent
     * @param annotation the annotation to appear in the route log of the document
     * @param adHocRecipients list of ad hoc recipients to which the document will be routed
     * @return the document
     * @throws WorkflowException
     */
    public Document sendAdHocRequests(Document document, String annotation, List<AdHocRouteRecipient> adHocRecipients) throws WorkflowException;

    /**
     * Builds an workflow notification request for the note and sends it to note recipient.
     *
     * @param document - document that contains the note
     * @param note - note to notify
     * @param sender - user who is sending the notification
     * @return the document
     * @throws WorkflowException
     */
    public Document sendNoteRouteNotification(Document document, Note note, Person sender) throws WorkflowException;

    /**
     * Recall the document, optionally providing an annotation for the recall which will show up in the route
     * log of the document for the action taken.
     *
     * @since 2.1
     * @param document the document to recall
     * @param annotation the annotation to appear in the route log of the document
     * @param cancel indicates if the document should be canceled as part of the recall
     * @return the recalled document
     * @throws WorkflowException
     * @since 2.1
     */
    Document recallDocument(Document document, String annotation, boolean cancel) throws WorkflowException;

    /**
     * Save and then complete the document, optionally providing an annotation which will show up in the route log
     * of the document for the action taken, and optionally providing a list of ad hoc recipients for the document
     *
     * @param document the document to complete
     * @param annotation the annotation to appear in the route log of the document
     * @param adHocRecipients list of ad hoc recipients to which the document will be routed
     * @return the saved and completed document
     */
    Document completeDocument(Document document, String annotation,
            List adHocRecipients) throws WorkflowException;

    /**
     * Helper method used to save and validate a document
     *
     * @param document document to be validated and persisted
     * @param event indicates which kualiDocumentEvent was requested
     * @return the saved document
     */
    Document validateAndPersistDocument(Document document, DocumentEvent event) throws ValidationException;
}
