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

package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Collection of named BusinessObjectEntry objects, each of which contains
 * information relating to the display, validation, and general maintenance of a
 * BusinessObject.
 * 
 * 
 */
public class DataDictionary {

	// logger
	private static final Log LOG = LogFactory.getLog(DataDictionary.class);

	// keyed by BusinessObject class
	private Map<String, BusinessObjectEntry> businessObjectEntries;

	// keyed by documentTypeName
	private Map<String, DocumentEntry> documentEntries;

	private List<DataDictionaryEntryBase> entries;
	
	public DataDictionary() {
		LOG.debug("creating new DataDictionary - converter");

		// primary indices
		businessObjectEntries = new HashMap<String, BusinessObjectEntry>();
		documentEntries = new HashMap<String, DocumentEntry>();
		entries = new ArrayList<DataDictionaryEntryBase>();
	}
	
	// called by digester
	public void addDocumentEntry(DocumentEntry documentEntry) {
		if (documentEntry == null) {
			throw new IllegalArgumentException("invalid (null) documentEntry");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling addDocumentEntry '" + documentEntry.getDocumentTypeName() + "'");
		}

		String entryName = documentEntry.getDocumentTypeName();

		documentEntry.sourceFile = DataDictionaryConverter.getCurrentFileName();
		
		documentEntries.put(entryName, documentEntry);
		entries.add( documentEntry );
	}

	public void addBusinessObjectEntry(BusinessObjectEntry businessObjectEntry) {
		if (businessObjectEntry == null) {
			throw new IllegalArgumentException("invalid (null) businessObjectEntry");
		}
		if ( LOG.isDebugEnabled() ) {
		    LOG.debug("calling addBusinessObjectEntry '" + businessObjectEntry.getBusinessObjectClass().getName() + "'");
		}

		String entryName = businessObjectEntry.getBusinessObjectClass().getName();

        businessObjectEntry.sourceFile = DataDictionaryConverter.getCurrentFileName();

		businessObjectEntries.put(entryName, businessObjectEntry);
        entries.add( businessObjectEntry );
	}

	/**
	 * @return Map of (classname, BusinessObjectEntry) pairs
	 */
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		return this.businessObjectEntries;
	}

	public Map<String, DocumentEntry> getDocumentEntries() {
		return this.documentEntries;
	}

    public List<DataDictionaryEntryBase> getAllEntries() {
        return entries;
    }
	
	public BusinessObjectEntry getBusinessObjectEntry( String className ) {
	    return businessObjectEntries.get(className);
	}
}