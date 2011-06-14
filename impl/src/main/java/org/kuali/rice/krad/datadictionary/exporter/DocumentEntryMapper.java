/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary.exporter;

import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.service.DocumentHelperService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * DocumentEntryMapper
 * 
 * 
 */
public abstract class DocumentEntryMapper {
    
    protected DocumentTypeDTO getDocumentType(String documentTypeName) {
        try {
            return KRADServiceLocatorWeb.getWorkflowInfoService().getDocType(documentTypeName);
        } catch (WorkflowException e) {
            throw new RuntimeException("Caught Exception trying to get the Workflow Document Type", e);
        }
    }

    /**
     * @param entry
     * @return Map containing entries for properties common to all DocumentEntry subclasses
     */
    @SuppressWarnings("unchecked")
	protected ExportMap mapEntry(DocumentEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("invalid (null) entry");
        }

        // simple properties
        ExportMap entryMap = new ExportMap(entry.getJstlKey());

        Class businessRulesClass = entry.getBusinessRulesClass();
        if (businessRulesClass != null) {
            entryMap.set("businessRulesClass", businessRulesClass.getName());
        }

        entryMap.set("documentTypeName", entry.getDocumentTypeName());

        DocumentTypeDTO docType = getDocumentType(entry.getDocumentTypeName());
        entryMap.set("label", docType.getDocTypeLabel());

        if (docType.getDocTypeDescription() != null) {
            entryMap.set("description", docType.getDocTypeDescription());
        }

        DocumentHelperService documentHelperService = KRADServiceLocatorWeb.getDocumentHelperService();
        entryMap.set("documentAuthorizerClass", documentHelperService.getDocumentAuthorizer(entry.getDocumentTypeName()).getClass().getName());
        entryMap.set("documentPresentationControllerClass", documentHelperService.getDocumentPresentationController(entry.getDocumentTypeName()).getClass().getName());

        entryMap.set("allowsNoteAttachments", Boolean.toString(entry.getAllowsNoteAttachments()));

        entryMap.set("allowsNoteFYI", Boolean.toString(entry.getAllowsNoteFYI()));
        
        if (entry.getAttachmentTypesValuesFinderClass() != null) {
            entryMap.set("attachmentTypesValuesFinderClass", entry.getAttachmentTypesValuesFinderClass().getName());
        }

        entryMap.set("displayTopicFieldInNotes", Boolean.toString(entry.getDisplayTopicFieldInNotes()));
        
        entryMap.set("usePessimisticLocking", Boolean.toString(entry.getUsePessimisticLocking()));
        entryMap.set("useWorkflowPessimisticLocking", Boolean.toString(entry.getUseWorkflowPessimisticLocking()));
        entryMap.set("sessionDocument", Boolean.toString(entry.isSessionDocument()));
        
        entryMap.set(new AttributesMapBuilder().buildAttributesMap(entry));
        entryMap.set(new CollectionsMapBuilder().buildCollectionsMap(entry));

        return entryMap;
    }

}
