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

package org.kuali.core.datadictionary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.core.exceptions.DuplicateKeyException;
import org.kuali.rice.KNSServiceLocator;

/**
 * Collection of named BusinessObjectEntry objects, each of which contains
 * information relating to the display, validation, and general maintenance of a
 * BusinessObject.
 * 
 * 
 */
public class DataDictionary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4707349557978305232L;

	// logger
	private static final Log LOG = LogFactory.getLog(DataDictionary.class);

	private DataDictionaryBuilder dataDictionaryBuilder;

	// keyed by BusinessObject class
	private Map<String, BusinessObjectEntry> businessObjectEntries;

	// keyed by documentTypeName
	private Map<String, DocumentEntry> documentEntries;

	// keyed by other things
	private Map<Class, DocumentEntry> documentEntriesByDocumentClass;

	private Map<Class, DocumentEntry> documentEntriesByBusinessObjectClass;

	private Map<Class, DocumentEntry> documentEntriesByMaintainableClass;

	private Map<String, DataDictionaryEntry> entriesByJstlKey;

	private Set jstlKeys;

	private ValidationCompletionUtils validationCompletionUtils;

	private boolean allowOverrides = true;

	public DataDictionary(ValidationCompletionUtils validationCompletionUtils, DataDictionaryBuilder dataDictionaryBuilder) {
		LOG.debug("creating new DataDictionary");

		this.validationCompletionUtils = validationCompletionUtils;

		// primary indices
		businessObjectEntries = new HashMap<String, BusinessObjectEntry>();
		documentEntries = new HashMap<String, DocumentEntry>();

		// alternate indices
		documentEntriesByDocumentClass = new HashMap<Class, DocumentEntry>();
		documentEntriesByBusinessObjectClass = new HashMap<Class, DocumentEntry>();
		documentEntriesByMaintainableClass = new HashMap<Class, DocumentEntry>();
		entriesByJstlKey = new HashMap<String, DataDictionaryEntry>();

		this.dataDictionaryBuilder = dataDictionaryBuilder;
		jstlKeys = new HashSet();
	}
	
	protected Map<String,String> getFileLocationMap() {
		return dataDictionaryBuilder.getFileLocationMap();
	}

	// called by digester
	public void addDocumentEntry(DocumentEntry documentEntry) {
		if (documentEntry == null) {
			throw new IllegalArgumentException("invalid (null) documentEntry");
		}
		LOG.debug("calling addDocumentEntry '" + documentEntry.getDocumentTypeName() + "'");

		String entryName = documentEntry.getDocumentTypeName();
		if (!allowOverrides) {
			if (documentEntries.containsKey(entryName)) {
				throw new DuplicateEntryException("duplicate DocumentEntry for document type '" + entryName + "'");
			}
		}
		if ((documentEntry instanceof TransactionalDocumentEntry) && (documentEntries.get(documentEntry.getFullClassName()) != null) && !((DocumentEntry)documentEntries.get(documentEntry.getFullClassName())).getDocumentTypeName().equals(documentEntry.getDocumentTypeName())) {
			throw new DataDictionaryException(new StringBuffer("Two transactional document types may not share the same document class: this=").append(documentEntry.getDocumentTypeName()).append(" / existing=").append(((DocumentEntry)documentEntries.get(documentEntry.getDocumentClass().getName())).getDocumentTypeName()).toString());
		}
		if ((entriesByJstlKey.get(documentEntry.getJstlKey()) != null) && !((DocumentEntry)documentEntries.get(documentEntry.getJstlKey())).getDocumentTypeName().equals(documentEntry.getDocumentTypeName())) {
			throw new DataDictionaryException(new StringBuffer("Two document types may not share the same jstl key: this=").append(documentEntry.getDocumentTypeName()).append(" / existing=").append(((DocumentEntry)documentEntries.get(documentEntry.getJstlKey())).getDocumentTypeName()).toString());
		}

		documentEntry.completeValidation(validationCompletionUtils);

		documentEntries.put(entryName, documentEntry);
		documentEntries.put(documentEntry.getFullClassName(), documentEntry);
		entriesByJstlKey.put(documentEntry.getJstlKey(), documentEntry);

		if (documentEntry instanceof TransactionalDocumentEntry) {
			TransactionalDocumentEntry tde = (TransactionalDocumentEntry) documentEntry;

			documentEntriesByDocumentClass.put(tde.getDocumentClass(), documentEntry);
			documentEntries.put(tde.getDocumentClass().getSimpleName(), documentEntry);
		}
		if (documentEntry instanceof MaintenanceDocumentEntry) {
			MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry) documentEntry;

			documentEntriesByBusinessObjectClass.put(mde.getBusinessObjectClass(), documentEntry);
			documentEntriesByMaintainableClass.put(mde.getMaintainableClass(), documentEntry);
			documentEntries.put(mde.getBusinessObjectClass().getSimpleName() + "MaintenanceDocument", documentEntry);
		}

		String jstlKey = documentEntry.getJstlKey();
		if (!allowOverrides) {
			if (jstlKeys.contains(jstlKey)) {
				StringBuffer msg = new StringBuffer("unable to add jstlKey for documentType");
				msg.append(StringUtils.substringAfterLast(documentEntry.getDocumentTypeName(), "."));
				msg.append(": key '");
				msg.append(jstlKey);
				msg.append("' is already in use");

				throw new DuplicateKeyException(msg.toString());
			}
		}
		jstlKeys.add(jstlKey);
		documentEntry.validateAuthorizations(KNSServiceLocator.getKualiGroupService());
		documentEntry.validateAuthorizer(KNSServiceLocator.getKualiConfigurationService(), validationCompletionUtils);
		documentEntry.expandAttributeReferences(this, validationCompletionUtils);
		KNSServiceLocator.getAuthorizationService().setupAuthorizations(documentEntry);
	}

	public void addBusinessObjectEntry(BusinessObjectEntry businessObjectEntry) {
		if (businessObjectEntry == null) {
			throw new IllegalArgumentException("invalid (null) businessObjectEntry");
		}
		LOG.debug("calling addBusinessObjectEntry '" + businessObjectEntry.getBusinessObjectClass().getName() + "'");

		String entryName = businessObjectEntry.getBusinessObjectClass().getName();
		if (!allowOverrides) {
			if (businessObjectEntries.containsKey(entryName)) {
				throw new DuplicateEntryException("duplicate BusinessObjectEntry for class '" + entryName + "'");
			}
		}
		if ((businessObjectEntries.get(businessObjectEntry.getJstlKey()) != null) && !((BusinessObjectEntry)businessObjectEntries.get(businessObjectEntry.getJstlKey())).getBusinessObjectClass().equals(businessObjectEntry.getBusinessObjectClass())) {
			throw new DataDictionaryException(new StringBuffer("Two business object classes may not share the same jstl key: this=").append(businessObjectEntry.getBusinessObjectClass()).append(" / existing=").append(((BusinessObjectEntry)businessObjectEntries.get(businessObjectEntry.getJstlKey())).getBusinessObjectClass()).toString());
		}

		businessObjectEntry.completeValidation(validationCompletionUtils);

		businessObjectEntries.put(entryName, businessObjectEntry);
		businessObjectEntries.put(businessObjectEntry.getBusinessObjectClass().getSimpleName(), businessObjectEntry);
		entriesByJstlKey.put(businessObjectEntry.getJstlKey(), businessObjectEntry);

		String jstlKey = businessObjectEntry.getJstlKey();
		if (!allowOverrides) {
			if (jstlKeys.contains(jstlKey)) {
				StringBuffer msg = new StringBuffer("unable to add jstlKey for businessObject");
				msg.append(StringUtils.substringAfterLast(businessObjectEntry.getClass().getName(), "."));
				msg.append(": key '");
				msg.append(jstlKey);
				msg.append("' is already in use");

				throw new DuplicateKeyException(msg.toString());
			}
		}
		jstlKeys.add(jstlKey);
		businessObjectEntry.expandAttributeReferences(this, validationCompletionUtils);
	}
	
    /**
     * This method provides the Map of all "components" (i.e. lookup, inquiry, and document titles), keyed by business object or document class names
     * 
     * @return map of component names, keyed by class name
     */
    protected Map<String,Set<String>> getComponentNamesByClassName() {
        Map<String,Set<String>> componentNamesByClassName = new HashMap<String,Set<String>>();
        for (String businessObjectEntryKey : businessObjectEntries.keySet()) {
            BusinessObjectEntry businessObjectEntry = businessObjectEntries.get(businessObjectEntryKey);
            Set<String> componentNames = new HashSet<String>();
            if (businessObjectEntry.getLookupDefinition() != null) {
                componentNames.add(businessObjectEntry.getLookupDefinition().getTitle());
            }
            if (businessObjectEntry.getInquiryDefinition() != null) {
                componentNames.add(businessObjectEntry.getInquiryDefinition().getTitle());
            }
            componentNamesByClassName.put(businessObjectEntry.getFullClassName(), componentNames);
        }
        for (Class businessObjectClass : documentEntriesByBusinessObjectClass.keySet()) {
            DocumentEntry documentEntry = documentEntriesByBusinessObjectClass.get(businessObjectClass);
            if (componentNamesByClassName.containsKey(businessObjectClass.getName())) {
                componentNamesByClassName.get(businessObjectClass.getName()).add(documentEntry.getLabel());
            }
            else {
                Set<String> componentNames = new HashSet<String>();
                componentNames.add(documentEntry.getLabel());
                componentNamesByClassName.put(businessObjectClass.getName(), componentNames);                
            }
        }
        for (Class documentClass : documentEntriesByDocumentClass.keySet()) {
            DocumentEntry documentEntry = documentEntriesByDocumentClass.get(documentClass);
            Set<String> componentNames = new HashSet<String>();
            componentNames.add(documentEntry.getLabel());
            componentNamesByClassName.put(documentClass.getName(), componentNames);
        }
        return componentNamesByClassName;
    }

	/**
	 * @param className
	 * @return BusinessObjectEntry for the named class, or null if none exists
	 */
	public BusinessObjectEntry getBusinessObjectEntry(String className) {
		return getBusinessObjectEntry( className, true );
	}
	/**
	 * @param className
	 * @return BusinessObjectEntry for the named class, or null if none exists
	 */
	public BusinessObjectEntry getBusinessObjectEntry(String className, boolean parseOnFail ) {
		if (StringUtils.isBlank(className)) {
			throw new IllegalArgumentException("invalid (blank) className");
		}
		LOG.debug("calling getBusinessObjectEntry '" + className + "'");
		int index = className.indexOf("$$");
		if (index >= 0) {
			className = className.substring(0, index);
		}
		// LOG.info("calling getBusinessObjectEntry truncated '" + className + "'");

		BusinessObjectEntry boe = businessObjectEntries.get(className);
		if ( boe == null && parseOnFail ) {
			LOG.debug("Unable to find BusinessObjectEntry '" + className + "' -- calling parseBO()");
			this.dataDictionaryBuilder.parseBO(className, isAllowOverrides());
		}
		return businessObjectEntries.get(className);
	}

	/**
	 * @return List of businessObject classnames
	 */
	public List getBusinessObjectClassNames() {
		List classNames = new ArrayList();
		classNames.addAll(this.businessObjectEntries.keySet());

		return Collections.unmodifiableList(classNames);
	}

	/**
	 * @return Map of (classname, BusinessObjectEntry) pairs
	 */
	public Map<String, BusinessObjectEntry> getBusinessObjectEntries() {
		return Collections.unmodifiableMap(this.businessObjectEntries);
	}

	/**
	 * @param className
	 * @return DataDictionaryEntryBase for the named class, or null if none
	 *         exists
	 */
	public DataDictionaryEntry getDictionaryObjectEntry(String className) {
		if (StringUtils.isBlank(className)) {
			throw new IllegalArgumentException("invalid (blank) className");
		}
		LOG.debug("calling getDictionaryObjectEntry '" + className + "'");
		int index = className.indexOf("$$");
		if (index >= 0) {
			className = className.substring(0, index);
		}

		// look in the JSTL key cache
		DataDictionaryEntry entry = entriesByJstlKey.get(className);
		if ( entry == null ) {
			// look in the BO cache
			entry = getBusinessObjectEntry(className, false );
			if (entry == null) {	
				//look in the document cache
				entry = getDocumentEntry(className, false);
				
				// the object does not exist in the DD currently, attempt to parse the file
				if ( entry == null ) {
					this.dataDictionaryBuilder.parseDocument(className, isAllowOverrides());
					// re-try the BO and document caches after the parse
					entry = getDocumentEntry(className, false);					
					if ( entry == null ) {
						entry = getBusinessObjectEntry(className, true );
					}
				}
			}
		}
		
		return entry;
	}

	public DocumentEntry getDocumentEntry(String documentTypeDDKey) {
		return getDocumentEntry( documentTypeDDKey, true );
	}	
	
	public DocumentEntry getDocumentEntry(String documentTypeDDKey, boolean parseOnFail ) {
		if (StringUtils.isBlank(documentTypeDDKey)) {
			throw new IllegalArgumentException("invalid (blank) documentTypeName");
		}
		LOG.debug("calling getDocumentEntry by documentTypeName '" + documentTypeDDKey + "'");

		DocumentEntry de = documentEntries.get(documentTypeDDKey);		
        if (de == null) {
        	// need to attempt to convert the key to a class since the documentEntries...
        	// maps are keyed by class objects rather than class names
        	try {
        		Class clazz = Class.forName( documentTypeDDKey );
                de = documentEntriesByBusinessObjectClass.get( clazz );
                if (de == null) {
                    de = documentEntriesByDocumentClass.get( clazz );
                }
        	} catch ( ClassNotFoundException ex ) {
        		// do nothing, just skip if not a valid class name
        	}
        }
		if ( de == null && parseOnFail ) {
			this.dataDictionaryBuilder.parseDocument(documentTypeDDKey, isAllowOverrides());
			de = documentEntries.get(documentTypeDDKey);
		}
        return de;
	}

	/**
	 * Note: only MaintenanceDocuments are indexed by businessObject Class
	 * 
	 * This is a special case that is referenced in one location. Do we need
	 * another map for this stuff??
	 * 
	 * @param businessObjectClass
	 * @return DocumentEntry associated with the given Class, or null if there
	 *         is none
	 */
	public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(Class businessObjectClass) {
		if (businessObjectClass == null) {
			throw new IllegalArgumentException("invalid (null) businessObjectClass");
		}
		LOG.debug("calling getDocumentEntry by businessObjectClass '" + businessObjectClass + "'");

		MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry) documentEntriesByBusinessObjectClass.get(businessObjectClass);
		if (mde == null) {
			//Before attempting to parse the DD again, try to look in the documentEntries, if we found
			//it there, then we'll return null for this method because it means that the businessObjectClass
			//is not in maintenance document, it's transactional.
		    if (documentEntries.get(businessObjectClass.getName()) != null) {	
		    	return null;
		    }
			this.dataDictionaryBuilder.parseMaintenanceDocument(businessObjectClass.getName(), true);
		}
		return (MaintenanceDocumentEntry) documentEntriesByBusinessObjectClass.get(businessObjectClass);
	}

	public Map<String, DocumentEntry> getDocumentEntries() {
		return Collections.unmodifiableMap(this.documentEntries);
	}

	public void setAllowOverrides(boolean allowOverrides) {
		LOG.debug("calling setAllowOverrides " + allowOverrides);

		this.allowOverrides = allowOverrides;
	}

	public boolean isAllowOverrides() {
		return allowOverrides;
	}
}