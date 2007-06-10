/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;


/**
 * This is the DocumentService interface which must have an implementation that accompanies it. This interfaces defines all of the
 * generally required methods for all document instances
 *
 *
 */

// TODO put exceptions that are kuali based into here instead of implementation based
public interface DocumentService {

    /**
     * @param documentHeaderId
     * @return true if a document with the given documentHeaderId exists
     */
    public boolean documentExists(String documentHeaderId);


    /**
     * get a new blank document instance based on the document type name
     *
     * @param documentTypeName
     * @return
     * @throws EdenUserNotFoundException
     * @throws EdenException
     */
    public Document getNewDocument(String documentTypeName) throws WorkflowException;

    /**
     * get a new blank document instance having the given Document class
     *
     * @param documentClass
     * @return
     * @throws EdenUserNotFoundException
     * @throws EdenException
     */
    public Document getNewDocument(Class documentClass) throws WorkflowException;

    /**
     * get a document based on the document header id which is the primary key for all document types
     *
     * @param documentHeaderId
     * @return
     * @throws EdenUserNotFoundException
     * @throws EdenException
     */
    public Document getByDocumentHeaderId(String documentHeaderId) throws WorkflowException;


    /**
     * Retrieves a collection of documents with type of given Class, and with the passed status code.
     *
     * @param clazz
     * @param statusCode
     * @return
     */
    public Collection findByDocumentHeaderStatusCode(Class clazz, String statusCode) throws WorkflowException;

    /**
     * This method retrieves a list of fully-populated documents given a list of document header id values.
     *
     * @param clazz
     * @param documentHeaderIds
     * @return List of fully-populated documents
     * @throws WorkflowException
     */
    public List getDocumentsByListOfDocumentHeaderIds(Class clazz, List documentHeaderIds) throws WorkflowException;

    /**
     * Retrieves a collection of DocumentHeaders by the date that they were finalized.
     *
     * @param documentFinalDate
     * @return Collection of DocumentHeaders
     * @throws WorkflowException
     */
    public Collection getFinalDocumentHeadersByDate(Date documentFinalDate) throws WorkflowException;

    /**
     *
     * This method is to allow for documents to be updated which is currently used to update the document status as well as to allow
     * for locked docs to be unlocked
     *
     * @param document
     */
    public void updateDocument(Document document);

    /**
     * Calls validation methods to validate against the dictionary and run through the rules engine. If validation succeeds, the
     * document is persisted and control returns back to the route action.
     *
     * @param document
     * @param event
     * @throws WorkflowException
     * @throws ValidationException
     */
    public void validateAndPersistDocument(Document document, KualiDocumentEvent event) throws WorkflowException, ValidationException;

    /**
     * Saves the passed-in document. This will persist it both to the Kuali database, and also initiate it (if necessary) within
     * workflow, so its available in the initiator's action list.
     *
     * Note that the system does not support passing in Workflow Annotations or AdHoc Route Recipients on a SaveDocument call. These
     * are sent to workflow on a routeDocument action, or any of the others which actually causes a routing action to happen in
     * workflow.
     *
     * @param document The document to be saved.
     * @return Returns the document that was passed in.
     * @throws EdenException
     * @throws ValidationErrorList
     */
    public Document saveDocument(Document document) throws WorkflowException;


    /**
     * start the route the document for approval, optionally providing a list of ad hoc recipients, and additionally provideing a
     * annotation to show up in the route log for the document
     *
     * @param document
     * @param annotation
     * @param adHocRoutingRecipients
     * @return
     * @throws EdenException
     * @throws ValidationErrorList
     */
    public Document routeDocument(Document document, String annotation, List adHocRoutingRecipients) throws WorkflowException;

    /**
     * approve this document, optionally providing an annotation which will show up in the route log for this document for this
     * action taken, and optionally providing a list of ad hoc recipients for the document
     *
     * @param document
     * @param annotation
     * @param adHocRoutingRecipients
     * @return
     * @throws EdenException
     * @throws ValidationErrorList
     */
    public Document approveDocument(Document document, String annotation, List adHocRoutingRecipients) throws WorkflowException;

    /**
     * approve this document as super user, optionally providing an annotation which will show up in the route log for this document
     * for this action taken
     *
     * @param document
     * @param annotation
     * @return
     * @throws EdenException
     * @throws ValidationErrorList
     */
    public Document superUserApproveDocument(Document document, String annotation) throws WorkflowException;

    /**
     * disapprove this document, optionally providing an annotation for the disapproval which will show up in the route log for the
     * document for this action taken
     *
     * @param document
     * @param annotation
     * @return Document
     * @throws Exception
     */
    public Document disapproveDocument(Document document, String annotation) throws Exception;

    /**
     * cancel this document, optionally providing an annotation for the disapproval which will show up in the route log for the
     * document for this action taken
     *
     * @param document
     * @param annotation
     * @return
     * @throws EdenException
     */
    public Document cancelDocument(Document document, String annotation) throws WorkflowException;

    /**
     * acknowledge this document, optionally providing an annotation for the acknowledgement which will show up in the route log for
     * the document for this acknowledgement, additionally optionally provide a list of ad hoc recipients that should recieve this
     * document. The list of ad hoc recipients for this document should have an action requested of acknowledge or fyi as all other
     * actions requested will be discarded as invalid based on the action being taken being an acknowledgement.
     *
     * @param document
     * @param annotation
     * @param adHocRecipients
     * @return
     * @throws EdenException
     */
    public Document acknowledgeDocument(Document document, String annotation, List adHocRecipients) throws WorkflowException;

    /**
     * blanket approve this document which will approve the document and stand in for an approve for all typically generated
     * approval actions requested for this document. The user must have blanket approval authority for this document by being
     * registered as a user in the blanket approval workgroup that is associated with this document type. Optionally an annotation
     * can be provided which will show up for this action taken on the document in the route log. Additionally optionally provide a
     * list of ad hoc recipients for this document, which should be restricted to actions requested of acknowledge and fyi as all
     * other actions requested will be discarded
     *
     * @param document
     * @param annotation
     * @param adHocRecipients
     * @return
     * @throws EdenException
     * @throws ValidationErrorList
     */
    public Document blanketApproveDocument(Document document, String annotation, List adHocRecipients) throws WorkflowException;

    /**
     * clear the fyi request for this document, optionally providing a list of ad hoc recipients for this document, which should be
     * restricted to action requested of fyi as all other actions requested will be discarded
     *
     * @param document
     * @param adHocRecipients
     * @return
     * @throws EdenException
     */
    public Document clearDocumentFyi(Document document, List adHocRecipients) throws WorkflowException;

    /**
     * Sets the title and app document id in the flex document
     *
     * @param document
     * @throws WorkflowException
     */
    public void prepareWorkflowDocument(Document document) throws WorkflowException;
    
    
    /**
     * 
     * This method creates a note from a given document and note text
     * @param document
     * @param text
     * @return
     * @throws Exception
     */
    public Note createNoteFromDocument(Document document, String text) throws Exception;
    
    /**
     * 
     * This method adds a note to a document
     * @param document
     * @param note
     * @return the added Note
     */
    public boolean addNoteToDocument(Document document, Note note);
    
    /**
     * 
     * This method gets the parent for a note from a document
     * @param document
     * @param newNote
     * @return Business Object that the note is attached to.
     */
    public PersistableBusinessObject getNoteParent(Document document, Note newNote);
}