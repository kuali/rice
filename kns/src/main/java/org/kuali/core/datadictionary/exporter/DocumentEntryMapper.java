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
package org.kuali.core.datadictionary.exporter;

import java.util.Iterator;
import java.util.Map;

import org.kuali.core.datadictionary.AuthorizationDefinition;
import org.kuali.core.datadictionary.DocumentEntry;

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
        entryMap.set("documentTypeCode", entry.getDocumentTypeCode());

        entryMap.set("label", entry.getLabel());
        entryMap.set("shortLabel", entry.getShortLabel());

        entryMap.set("summary", entry.getSummary());
        entryMap.set("description", entry.getDescription());

        entryMap.set("documentAuthorizerClass", entry.getDocumentAuthorizerClass().getName());

        entryMap.set("allowsNoteDelete", Boolean.toString(entry.getAllowsNoteDelete()));

        if (entry.getAttachmentTypeValuesFinderClass() != null) {
            entryMap.set("attachmentTypesValuesFinderClass", entry.getAttachmentTypeValuesFinderClass().getName());
        }

        entryMap.set("displayTopicFieldInNotes", Boolean.toString(entry.getDisplayTopicFieldInNotes()));
        
        entryMap.set(new AttributesMapBuilder().buildAttributesMap(entry));
        entryMap.set(new CollectionsMapBuilder().buildCollectionsMap(entry));

        // complex properties
        entryMap.setOptional(buildAuthorizationsMap(entry));

        return entryMap;
    }


    private ExportMap buildAuthorizationsMap(DocumentEntry entry) {
        ExportMap authorizationsMap = null;

        Map authorizationDefinitions = entry.getAuthorizationDefinitions();
        if ((authorizationDefinitions != null) && !authorizationDefinitions.isEmpty()) {
            authorizationsMap = new ExportMap("authorizations");

            for (Iterator i = authorizationDefinitions.entrySet().iterator(); i.hasNext();) {
                Map.Entry e = (Map.Entry) i.next();

                String authAction = (String) e.getKey();
                AuthorizationDefinition authorizationDefinition = (AuthorizationDefinition) e.getValue();

                authorizationsMap.set(buildAuthorizationMap(authorizationDefinition));
            }
        }

        return authorizationsMap;
    }

    private ExportMap buildAuthorizationMap(AuthorizationDefinition authorizationDefinition) {
        ExportMap authorizationMap = new ExportMap(authorizationDefinition.getAction());

        int index = 0;
        for (Iterator i = authorizationDefinition.getGroupNames().iterator(); i.hasNext();) {
            String groupName = (String) i.next();

            ExportMap workgroupsMap = new ExportMap("workgroups");
            workgroupsMap.set(Integer.toString(index++), groupName);

            authorizationMap.set(workgroupsMap);
        }

        return authorizationMap;
    }


}