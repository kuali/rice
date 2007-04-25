/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.MaintainableFieldDefinition;
import org.kuali.core.datadictionary.MaintainableItemDefinition;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.KNSServiceLocator;

public class MaintenanceDocumentAuthorizerBase extends DocumentAuthorizerBase implements MaintenanceDocumentAuthorizer {

    /**
     * @see org.kuali.core.authorization.MaintenanceDocumentAuthorizer#getFieldAuthorizations(org.kuali.core.document.MaintenanceDocument,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        // by default, there are no restrictions, only if this method is
        // overridden by a subclass that adds restrictions
        return new MaintenanceDocumentAuthorizations();
    }

    /**
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {

        // run the super, let the common flags be set
        MaintenanceDocumentActionFlags docActionFlags = new MaintenanceDocumentActionFlags(super.getDocumentActionFlags(document, user));

        // run the fieldAuthorizations
        MaintenanceDocument maintDoc = (MaintenanceDocument) document;
        MaintenanceDocumentAuthorizations docAuths = getFieldAuthorizations(maintDoc, user);

        // if there are any field restrictions for this user, then we need to turn off the
        // ability to BlanketApprove, as this person doesnt have access to all the fields, so
        // they certainly cant blanket approve it.
        if (docAuths.hasAnyFieldRestrictions()) {
            docActionFlags.setCanBlanketApprove(false);
        }

        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        // if a user can't initiate a document of this type then they can't copy one, either
        if (!canCopy(workflowDocument.getDocumentType(), user)) {
            docActionFlags.setCanCopy(false);
        }
        else {
            docActionFlags.setCanCopy(document.getAllowsCopy() && (!workflowDocument.stateIsInitiated() && !workflowDocument.stateIsEnroute() && !workflowDocument.stateIsException() && !workflowDocument.stateIsSaved()));
        }

        return docActionFlags;
    }

    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document document, UniversalUser user) {

        // if this is not a MaintenanceDocument, then fail loudly, something is badly wrong
        if (!MaintenanceDocument.class.isAssignableFrom(document.getClass())) {
            throw new IllegalArgumentException("A document was passed into MaintenanceDocumentAuthorizerBase.getEditMode() " + "that is not a MaintenanceDocument descendent.  Processing cannot continue.");
        }

        Map editMode = new HashMap();
        
        // cast the document as a MaintenanceDocument, and get a handle on the workflowDocument
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        KualiWorkflowDocument workflowDocument = maintenanceDocument.getDocumentHeader().getWorkflowDocument();

        // default to view-only, as a safety precaution
        String editModeKey = AuthorizationConstants.MaintenanceEditMode.VIEW_ONLY;

        // if the document is cancelled, then its view only
        if (workflowDocument.stateIsCanceled()) {
            editModeKey = AuthorizationConstants.MaintenanceEditMode.VIEW_ONLY;
        }

        // if the document is being edited, then its full entry, or if the current user is
        // the system supervisor
        else if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            if (workflowDocument.userIsInitiator(user)) {
                editModeKey = AuthorizationConstants.MaintenanceEditMode.FULL_ENTRY;
                
                // initiators of documents for new records can view these fields for the documents while they're sitll under the control
                // of the initiators.  If they are always allowed access to the document, then they would be able to view the changes that
                // were made during routing, which would be a bad idea, as the router may have edited sensitive information enroute
                if (isDocumentForCreatingNewEntry(maintenanceDocument)) {
                    addAllMaintDocDefinedEditModesToMap(editMode, maintenanceDocument);
                }
            }
        }

        // if the document is in routing, then we have some special rules
        else if (workflowDocument.stateIsEnroute()) {

            // the person who has the approval request in their Actiong List
            // should be able to modify the document
            if (workflowDocument.isApprovalRequested()) {
                editModeKey = AuthorizationConstants.MaintenanceEditMode.APPROVER_EDIT_ENTRY;
            }
        }

        // save the editmode
        editMode.put(editModeKey, "TRUE");
        return editMode;
    }

    protected void addAllMaintDocDefinedEditModesToMap(Map editModes, MaintenanceDocument maintDoc) {
        String docTypeName = maintDoc.getDocumentHeader().getWorkflowDocument().getDocumentType();
        List<MaintainableSectionDefinition> sectionDefinitions = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);
        
        for ( MaintainableSectionDefinition sectionDefinition : sectionDefinitions ) {
            for ( MaintainableItemDefinition itemDefinition : sectionDefinition.getMaintainableItems() ) {
                if (itemDefinition instanceof MaintainableFieldDefinition) {
                    String displayEditMode = ((MaintainableFieldDefinition) itemDefinition).getDisplayEditMode();
                    if (StringUtils.isNotBlank(displayEditMode)) {
                        editModes.put(displayEditMode, "TRUE");
                    }
                }
                // TODO: what about MaintainableCollectionDefinition?
            }
        }
    }
    
    /**
     * This method returns whether this document is creating a new entry in the maintenible/underlying table
     * 
     * This method is useful to determine whether all the field-based edit modes should be enabled, which is 
     * useful in determining which fields are encrypted
     * 
     * This method considers that Constants.MAINTENANCE_NEWWITHEXISTING_ACTION is not a new document because 
     * there is uncertainity how documents with this action will actually be implemented
     * 
     * @param maintDoc
     * @param user
     * @return
     */
    protected boolean isDocumentForCreatingNewEntry(MaintenanceDocument maintDoc) {
        // the rule is as follows: if the maint doc represents a new record AND the user is the same user who initiated the maintenance doc
        // if the user check is not added, then it would be pointless to do any encryption since I can just pull up a document to view the encrypted values
        
        // A maint doc is new when the new maintainable maintenance flag is set to either Constants.MAINTENANCE_NEW_ACTION or Constants.MAINTENANCE_COPY_ACTION
        String maintAction = maintDoc.getNewMaintainableObject().getMaintenanceAction();
        return (Constants.MAINTENANCE_NEW_ACTION.equals(maintAction) || Constants.MAINTENANCE_COPY_ACTION.equals(maintAction));
    }
}
