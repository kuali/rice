/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.kew.document;

import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionitem.OutboxItem;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.api.KEWPropertyConstants;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.xml.DocumentTypeXmlParser;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.util.KRADUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is the maintainable implementation for the Workflow {@link DocumentType} 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentTypeMaintainable extends KualiMaintainableImpl {
    
    private static final long serialVersionUID = -5920808902137192662L;

    /**
     * Override the getSections method on this maintainable so that the document type name field
     * can be set to read-only for 
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        // if the document isn't new then we need to make the document type name field read-only
        if (!document.isNew()) {
            sectionLoop: for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                        if (KEWPropertyConstants.NAME.equals(field.getPropertyName())) {
                            field.setReadOnly(true);
                            break sectionLoop;
                        }
                    }
                }
            }
        }
        return sections;
    }

    /**
     * This overridden method resets the name
     * 
     *
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        DocumentType docType = ((DocumentType)getBusinessObject());
        docType.setDocumentTypeId(null);
        docType.setName("");
        docType.setPreviousVersionId(null);
        docType.setObjectId(null);
        docType.setVersionNumber(null);
    }

    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
    }
    
    private Set<String> constructUserInterfaceEditablePropertyNamesList() {
        Set<String> propertyNames = new HashSet<String>();
        List<MaintainableSectionDefinition> sectionDefinitions = getMaintenanceDocumentDictionaryService().getMaintainableSections(getDocumentTypeName());
        for (MaintainableSectionDefinition maintainableSectionDefinition : sectionDefinitions) {
            for (MaintainableItemDefinition maintainableItemDefinition : maintainableSectionDefinition.getMaintainableItems()) {
                propertyNames.add(maintainableItemDefinition.getName());
            }
        }
        return propertyNames;
    }

    /**
     * This is a complete override which does not call into
     * {@link KualiMaintainableImpl}. This method calls
     * {@link DocumentTypeService#versionAndSave(DocumentType)}.
     *
     */
    @Override
    public void saveBusinessObject() {
        DocumentTypeService docTypeService = KEWServiceLocator.getDocumentTypeService();
        DocumentType newDocumentType = (DocumentType) getBusinessObject();
        String documentTypeName = newDocumentType.getName();
        DocumentType docTypeFromDatabase = docTypeService.findByName(documentTypeName);
        if (docTypeFromDatabase == null) {
            docTypeService.versionAndSave(newDocumentType);
        }
        else {
            Boolean applyRetroactively = newDocumentType.getApplyRetroactively();
        	DocumentType newDocumentTypeFromDatabase;
            DocumentTypeXmlParser parser = new DocumentTypeXmlParser();
            try {
                newDocumentTypeFromDatabase = parser.generateNewDocumentTypeFromExisting(documentTypeName);
            } catch (Exception e) {
                throw new WorkflowRuntimeException("Error while attempting to generate new document type from existing " +
                		"database document type with name '" + documentTypeName + "'", e);
            }
            newDocumentTypeFromDatabase.populateDataDictionaryEditableFields(constructUserInterfaceEditablePropertyNamesList(), newDocumentType);
            docTypeService.versionAndSave(newDocumentTypeFromDatabase);
            if (KRADUtils.isNotNull(applyRetroactively) && applyRetroactively.booleanValue()) {
                // save all previous instances of document type with the same name
                // fields: label, description, unresolvedHelpDefinitionUrl
                List<DocumentType> previousDocTypeInstances = docTypeService.findPreviousInstances(documentTypeName);
                for (DocumentType prevDocType : previousDocTypeInstances) {
                    // set up fields
                    prevDocType.setLabel(newDocumentType.getLabel());
                    prevDocType.setDescription(newDocumentType.getDescription());
                    prevDocType.setUnresolvedHelpDefinitionUrl(newDocumentType.getUnresolvedHelpDefinitionUrl());
                    prevDocType.setUnresolvedDocSearchHelpUrl(newDocumentType.getUnresolvedDocSearchHelpUrl());
                    docTypeService.save(prevDocType);
                }
                // save all former/current action items matching document type name
                // fields: docLabel
                ActionListService actionListService = KEWServiceLocator.getActionListService(); 
                Collection<ActionItem> items = actionListService.findByDocumentTypeName(documentTypeName);
                for (ActionItem actionItem : items) {
                    actionItem.setDocLabel(newDocumentType.getLabel());
                    actionListService.saveActionItem(actionItem);
                }
                // save all former/current outbox action items matching document type name
                // fields: docLabel
                Collection<OutboxItem> outboxItems = actionListService.getOutboxItemsByDocumentType(documentTypeName);
                for (OutboxItem outboxItem : outboxItems) {
                    outboxItem.setDocLabel(newDocumentType.getLabel());
                    actionListService.saveOutboxItem(outboxItem);
                }
            }
        }
    }

}
