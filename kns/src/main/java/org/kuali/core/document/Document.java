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
package org.kuali.core.document;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.exceptions.IllegalObjectStateException;
import org.kuali.core.rule.event.KualiDocumentEvent;

import edu.iu.uis.eden.exception.WorkflowException;

import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;

/**
 * This is the Document interface. All entities that are regarded as "eDocs" in the system, including Maintenance documents and
 * Transaction Processing documents should implement this interface as it defines methods that are necessary to interact with the
 * underlying frameworks and components (i.e. attachments, workflow, etc).
 */
public interface Document extends PersistableBusinessObject{
    /**
     * This retrieves the standard <code>DocumentHeader</code> object, which contains standard meta-data about a document.
     * 
     * @return document header since all docs will have a document header
     */
    public DocumentHeader getDocumentHeader();

    /**
     * Sets the associated <code>DocumentHeader</code> for this document.
     * 
     * @param documentHeader
     */
    public void setDocumentHeader(DocumentHeader documentHeader);

    /**
     * All documents have a document header id. This is the quick accessor to that unique identifier and should return the same
     * value as documentHeader.getDocumentHeaderId().
     * 
     * @return doc header id
     */
    public String getDocumentNumber();

    /**
     * setter for document header id
     * 
     * @param documentHeaderId
     */
    public void setDocumentNumber(String documentHeaderId);

    /**
     * This is the method to integrate with workflow, where we will actually populate the workflow defined data structure(s) so that
     * workflow can routed based on this data. This method is responsible for passing over the proper Kuali (client system) data
     * that will be used by workflow to determine how the document is actually routed.
     */
    public void populateDocumentForRouting();

    /**
     * This is a method where we can get the xml of a document that the workflow system will use to base it's routing and search
     * attributes on.
     * 
     * @return the document serialized to an xml string
     */
    public String serializeDocumentToXml();
    
    /**
     * method to integrate with workflow, where we will actually handle the transitions of status for documents
     * 
     */
    public void handleRouteStatusChange();

    /**
     * method to integrate with workflow, where we will actually handle the transitions of levels for documents
     */
    public void handleRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent);
    
    /**
     * method to integrate with workflow where we will be able to perform logic for an action taken being performed on a document
     */
    public void doActionTaken(ActionTakenEventVO event);

    /**
     * Getter method to get the document title as it will appear in and be searchable in workflow.
     */
    public String getDocumentTitle();

    /**
     * getter method to get the list of ad hoc route persons associated with a document at a point in time, this list is only valid
     * for a given users version of a document as this state is only persisted in workflow itself when someone takes an action on a
     * document
     */
    public List getAdHocRoutePersons();

    /**
     * getter method to get the list of ad hoc route workgroups associated with a document at a point in time, this list is only
     * valid for a given users version of a document as this state is only persisted in workflow itself when someone takes an action
     * on a document
     */
    public List getAdHocRouteWorkgroups();

    /**
     * setter method to set the list of ad hoc route persons associated with a document at a point in time, this list is only valid
     * for a given users version of a document as this state is only persisted in workflow itself when someone takes an action on a
     * document
     * 
     * @param adHocRoutePersons
     */
    public void setAdHocRoutePersons(List adHocRoutePersons);

    /**
     * setter method to set the list of ad hoc route workgroups associated with a document at a point in time, this list is only
     * valid for a given users version of a document as this state is only persisted in workflow itself when someone takes an action
     * on a document
     * 
     * @param adHocRouteWorkgroups
     */
    public void setAdHocRouteWorkgroups(List adHocRouteWorkgroups);

    /**
     * This method provides a hook that will be called before the document is saved. This method is useful for applying document
     * level data to children. For example, if someone changes data at the document level, and that data needs to be propagated to
     * child objects or child lists of objects, you can use this method to update the child object or iterate through the list of
     * child objects and apply the document level data to them. Any document that follows this paradigm will need to make use of
     * this method to apply all of those changes.
     */
    public void prepareForSave();

    /**
     * Sends document off to the rules engine to verify business rules.
     * 
     * @param document - document to validate
     * @param event - indicates which document event was requested
     * @throws ValidationException - containing the MessageMap from the validation session.
     */
    public void validateBusinessRules(KualiDocumentEvent event);
    
    /**
     * Do any work on the document that requires the KualiDocumentEvent before the save.
     * 
     * @param event - indicates which document event was requested
     */
    public void prepareForSave(KualiDocumentEvent event);
    
    /**
     * Do any work on the document after the save.
     * 
     * @param event - indicates which document event was requested
     */
    public void postProcessSave(KualiDocumentEvent event);
    
    /**
     * This method provides a hook that will be called after a document is retrieved, but before it is returned from the
     * DocumentService.
     */
    public void processAfterRetrieve();

    /**
     * This method returns whether or not this document can be copied.
     * 
     * @return True if it can be copied, false if not.
     */
    public boolean getAllowsCopy();
    
    /**
     * Generate any necessary events required during the save event generation
     * 
     */
    public List generateSaveEvents();
    
   /**
     * Handle the doRouteStatusChange event from the post processor
     * 
     */
    public void doRouteStatusChange(DocumentRouteStatusChangeVO statusChangeEvent) throws Exception ;
    
    
    /**
     * This is a helper to return BO for use by notes, it allows both maintenance and transactional to have consistent 
     * notes paths without the tag or action knowing what kind of document they are
     * 
     * @return "this" unless overriden
     */
    public PersistableBusinessObject getDocumentBusinessObject();
    
}