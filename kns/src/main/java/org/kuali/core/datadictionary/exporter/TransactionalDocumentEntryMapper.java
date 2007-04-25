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
package org.kuali.core.datadictionary.exporter;

import org.kuali.core.datadictionary.TransactionalDocumentEntry;

/**
 * TransactionalDocumentEntryMapper
 */
public class TransactionalDocumentEntryMapper extends DocumentEntryMapper {

    /**
     * @param entry
     * @return Map containing a String- and Map-based representation of the given entry
     */
    public ExportMap mapEntry(TransactionalDocumentEntry entry) {
        ExportMap entryMap = super.mapEntry(entry);

        // simple properties
        entryMap.set("transactionalDocument", "true");
        entryMap.set("documentClass", entry.getDocumentClass().getName());
        entryMap.set("allowsCopy", Boolean.toString(entry.getAllowsCopy()));
        entryMap.set("allowsErrorCorrection", Boolean.toString(entry.getAllowsErrorCorrection()));

        return entryMap;
    }

}