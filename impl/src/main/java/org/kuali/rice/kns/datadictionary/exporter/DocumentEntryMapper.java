/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kns.datadictionary.exporter;

import java.util.List;

import org.kuali.rice.kns.datadictionary.AuthorizationDefinition;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * DocumentEntryMapper
 * 
 * 
 */
public abstract class DocumentEntryMapper {
    /**
     * @param entry
     * @return Map containing entries for properties common to all DocumentEntry subclasses
     */
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
        if (ObjectUtils.isNotNull(entry.getDocumentTypeCode())) {
        	entryMap.set("documentTypeCode", entry.getDocumentTypeCode());
        }

        entryMap.set("label", entry.getLabel());
        entryMap.set("shortLabel", entry.getShortLabel());

        if (entry.getSummary() != null) {
            entryMap.set("summary", entry.getSummary());
        }
        if (entry.getDescription() != null) {
            entryMap.set("description", entry.getDescription());
        }

        entryMap.set("documentAuthorizerClass", entry.getDocumentAuthorizerClass().getName());
        if(entry.getDocumentPresentationControllerClass() != null){
        	entryMap.set("documentPresentationControllerClass", entry.getDocumentPresentationControllerClass().getName());
        }
        entryMap.set("allowsNoteDelete", Boolean.toString(entry.getAllowsNoteDelete()));

        entryMap.set("allowsNoteAttachments", Boolean.toString(entry.getAllowsNoteAttachments()));

        if (entry.getAttachmentTypesValuesFinderClass() != null) {
            entryMap.set("attachmentTypesValuesFinderClass", entry.getAttachmentTypesValuesFinderClass().getName());
        }

        entryMap.set("displayTopicFieldInNotes", Boolean.toString(entry.getDisplayTopicFieldInNotes()));
        
        entryMap.set("usePessimisticLocking", Boolean.toString(entry.getUsePessimisticLocking()));
        entryMap.set("useWorkflowPessimisticLocking", Boolean.toString(entry.getUseWorkflowPessimisticLocking()));
        entryMap.set("sessionDocument", Boolean.toString(entry.getSessionDocument()));
        
        entryMap.set(new AttributesMapBuilder().buildAttributesMap(entry));
        entryMap.set(new CollectionsMapBuilder().buildCollectionsMap(entry));

        // complex properties
        entryMap.setOptional(buildAuthorizationsMap(entry));

        return entryMap;
    }


    private ExportMap buildAuthorizationsMap(DocumentEntry entry) {
        ExportMap authorizationsMap = null;

        List<AuthorizationDefinition> authorizationDefinitions = entry.getAuthorizationDefinitions();
        if ((authorizationDefinitions != null) && !authorizationDefinitions.isEmpty()) {
            authorizationsMap = new ExportMap("authorizations");

            for ( AuthorizationDefinition authorizationDefinition : authorizationDefinitions ) {
                authorizationsMap.set(buildAuthorizationMap(authorizationDefinition));
            }
        }

        return authorizationsMap;
    }

    private ExportMap buildAuthorizationMap(AuthorizationDefinition authorizationDefinition) {
        ExportMap authorizationMap = new ExportMap(authorizationDefinition.getAction());

        int index = 0;
        for (String groupName : authorizationDefinition.getAuthorizedGroups() ) {
            ExportMap workgroupsMap = new ExportMap("workgroups");
            workgroupsMap.set(Integer.toString(index++), groupName);

            authorizationMap.set(workgroupsMap);
        }

        return authorizationMap;
    }


}