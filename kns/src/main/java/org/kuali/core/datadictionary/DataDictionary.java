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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;
import org.kuali.core.exceptions.DuplicateKeyException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;

/**
 * Collection of named BusinessObjectEntry objects, each of which contains information relating to the display, validation, and
 * general maintenance of a BusinessObject.
 * 
 * 
 */
public class DataDictionary implements Serializable {
    // logger
    private static final Log LOG = LogFactory.getLog(DataDictionary.class);

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
    
    private boolean allowOverrides;

    public DataDictionary(ValidationCompletionUtils validationCompletionUtils) {
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

        jstlKeys = new HashSet();
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

        businessObjectEntry.completeValidation(validationCompletionUtils);

        businessObjectEntries.put(entryName, businessObjectEntry);
        entriesByJstlKey.put( businessObjectEntry.getJstlKey(), businessObjectEntry );

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
    }
    
    /**
     * This method provides the Map of all "components" (i.e. lookup, inquiry, and document titles), keyed by business object or document class names
     * 
     * @return map of component names, keyed by class name
     */
    public Map<String,Set<String>> getComponentNamesByClassName() {
        Map<String,Set<String>> componentNamesByClassName = new HashMap();
        for (String businessObjectClassName : businessObjectEntries.keySet()) {
            BusinessObjectEntry businessObjectEntry = businessObjectEntries.get(businessObjectClassName);
            Set componentNames = new HashSet();
            if (businessObjectEntry.getLookupDefinition() != null) {
                componentNames.add(businessObjectEntry.getLookupDefinition().getTitle());
            }
            if (businessObjectEntry.getInquiryDefinition() != null) {
                componentNames.add(businessObjectEntry.getInquiryDefinition().getTitle());
            }
            componentNamesByClassName.put(businessObjectClassName, componentNames);
        }
        for (Class businessObjectClass : documentEntriesByBusinessObjectClass.keySet()) {
            DocumentEntry documentEntry = documentEntriesByBusinessObjectClass.get(businessObjectClass);
            if (componentNamesByClassName.containsKey(businessObjectClass.getName())) {
                componentNamesByClassName.get(businessObjectClass.getName()).add(documentEntry.getLabel());
            }
            else {
                Set componentNames = new HashSet();
                componentNames.add(documentEntry.getLabel());
                componentNamesByClassName.put(businessObjectClass.getName(), componentNames);                
            }
        }
        for (Class documentClass : documentEntriesByDocumentClass.keySet()) {
            DocumentEntry documentEntry = documentEntriesByDocumentClass.get(documentClass);
            Set componentNames = new HashSet();
            componentNames.add(documentEntry.getLabel());
            componentNamesByClassName.put(documentClass.getName(), componentNames);
        }
        return componentNamesByClassName;
    }

    /**
     * @param className
     * @return BusinessObjectEntry for the given Class, or null if none exists
     */
    public BusinessObjectEntry getBusinessObjectEntry(Class clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("invalid (null) class");
        }
        LOG.debug("calling getBusinessObjectEntry '" + clazz + "'");

        return getBusinessObjectEntry(clazz.getName());
    }

    /**
     * @param className
     * @return BusinessObjectEntry for the named class, or null if none exists
     */
    public BusinessObjectEntry getBusinessObjectEntry(String className) {
        if (StringUtils.isBlank(className)) {
            throw new IllegalArgumentException("invalid (blank) className");
        }
        LOG.debug("calling getBusinessObjectEntry '" + className + "'");
        int index = className.indexOf("$$");
        if (index >= 0) {
            className = className.substring(0, index);
        }
        // LOG.info("calling getBusinessObjectEntry truncated '" + className + "'");

        return (BusinessObjectEntry) businessObjectEntries.get(className);
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
    public Map<String,BusinessObjectEntry> getBusinessObjectEntries() {
        return Collections.unmodifiableMap(this.businessObjectEntries);
    }

    /**
     * @return List of documentObject classnames
     */
    public List getDocumentObjectClassNames() {
        List classNames = new ArrayList();
        classNames.addAll(this.documentEntries.keySet());

        return Collections.unmodifiableList(classNames);
    }

    /**
     * Given a BusinessObjectEntryVisitor instance, execute its visitEntry method on every BusinessObjectEntry instance in this
     * DataDictionary.
     * 
     * @param visitor
     */
    public void visitBusinessObjectEntries(BusinessObjectEntryVisitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("invalid (null) visitor");
        }
        LOG.debug("calling visitBusinessObjectEntries '" + visitor.getClass().getName() + "'");

        for (Iterator i = businessObjectEntries.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry) e.getValue();
            visitor.visitEntry(businessObjectEntry);
        }
    }

    /**
     * @param className
     * @return DataDictionaryEntryBase for the named class, or null if none exists
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

        DataDictionaryEntry entry = entriesByJstlKey.get( className );
        if ( entry != null ) {
            return entry;
        }
        
        entry = businessObjectEntries.get(className);
        if (entry != null) {
            return entry;
        } else {
            return documentEntries.get(className);
        }
    }

    /**
     * @param entryClass
     * @return DataDictionaryEntryBase for the given class, or null if none exists
     */
    public DataDictionaryEntryBase getDictionaryObjectEntry(Class entryClass) {
        Object entry = getBusinessObjectEntry(entryClass);
        if (entry == null) {
            entry = documentEntriesByDocumentClass.get(entryClass);
        }
        if (entry == null) {
            entry = documentEntriesByMaintainableClass.get(entryClass);
        }
        if (entry == null) {
            // last ditch, this is a MaintenanceDocument entry for a given BO class
            entry = documentEntriesByBusinessObjectClass.get(entryClass);
        }
        return (DataDictionaryEntryBase) entry;
    }

    public void addDocumentEntry(DocumentEntry documentEntry) {
        if (documentEntry == null) {
            throw new IllegalArgumentException("invalid (null) documentEntry");
        }
        LOG.debug("calling addDocumentEntry '" + documentEntry.getDocumentTypeName() + "'");

        String entryName = documentEntry.getDocumentTypeName();
        if (!allowOverrides) {
            if (documentEntries.containsKey(entryName)) {
                throw new DuplicateEntryException("duplicate DocumentEntry for class '" + entryName + "'");
            }
        }

        documentEntry.completeValidation(validationCompletionUtils);

        documentEntries.put(entryName, documentEntry);
        entriesByJstlKey.put( documentEntry.getJstlKey(), documentEntry );
        if (documentEntry instanceof TransactionalDocumentEntry) {
            TransactionalDocumentEntry tde = (TransactionalDocumentEntry) documentEntry;

            documentEntriesByDocumentClass.put(tde.getDocumentClass(), documentEntry);
        }
        if (documentEntry instanceof MaintenanceDocumentEntry) {
            MaintenanceDocumentEntry mde = (MaintenanceDocumentEntry) documentEntry;

            documentEntriesByBusinessObjectClass.put(mde.getBusinessObjectClass(), documentEntry);
            documentEntriesByMaintainableClass.put(mde.getMaintainableClass(), documentEntry);
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
    }

    public DocumentEntry getDocumentEntry(String documentTypeName) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }
        LOG.debug("calling getDocumentEntry by documentTypeName '" + documentTypeName + "'");

        return (DocumentEntry) documentEntries.get(documentTypeName);
    }

    public DocumentEntry getDocumentEntry(Class documentTypeClass) {
        DocumentEntry documentEntry = null;
        if (StringUtils.isBlank(documentTypeClass.getName())) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }

        for (Iterator iter = documentEntries.keySet().iterator(); iter.hasNext();) {
            DocumentEntry element = (DocumentEntry) documentEntries.get(iter.next());
            if (element.getDocumentClass().equals(documentTypeClass)) {
                documentEntry = element;
            }
        }

        return documentEntry;
    }

    public DocumentEntry getDocumentEntryByCode(String documentTypeCode) {
        DocumentEntry documentEntry = null;
        if (StringUtils.isBlank(documentTypeCode)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeCode");
        }

        for (Iterator iter = documentEntries.keySet().iterator(); iter.hasNext();) {
            DocumentEntry element = (DocumentEntry) documentEntries.get(iter.next());
            if (element.getDocumentTypeCode().equals(documentTypeCode)) {
                documentEntry = element;
            }
        }

        return documentEntry;
    }

    public MaintenanceDocumentEntry getMaintenanceDocumentEntry(String documentTypeName) {
        DocumentEntry documentEntry = getDocumentEntry(documentTypeName);

        MaintenanceDocumentEntry maintenanceDocumentEntry = null;
        if (documentEntry instanceof MaintenanceDocumentEntry) {
            maintenanceDocumentEntry = (MaintenanceDocumentEntry) documentEntry;
        }
        return maintenanceDocumentEntry;
    }

    public TransactionalDocumentEntry getTransactionalDocumentEntry(String documentTypeName) {
        DocumentEntry documentEntry = getDocumentEntry(documentTypeName);

        TransactionalDocumentEntry transactionalDocumentEntry = null;
        if (documentEntry instanceof TransactionalDocumentEntry) {
            transactionalDocumentEntry = (TransactionalDocumentEntry) documentEntry;
        }
        return transactionalDocumentEntry;
    }

    /**
     * FIXME: This documentation appears to be out of date. There appear to be many maintenance documents at the moment. Note: only
     * TransactionalDocuments are indexed by Class, since there is (at the moment) only one MaintenanceDocument class.
     * 
     * @param documentClass
     * @return DocumentEntry associated with the given Class, or null if there is none
     */
    public TransactionalDocumentEntry getTransactionalDocumentEntry(Class documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }
        LOG.debug("calling getDocumentEntry by class '" + documentClass + "'");

        return (TransactionalDocumentEntry) documentEntriesByDocumentClass.get(documentClass);
    }

    /**
     * Note: only MaintenanceDocuments are indexed by businessObject Class
     * 
     * @param businessObjectClass
     * @return DocumentEntry associated with the given Class, or null if there is none
     */
    public MaintenanceDocumentEntry getMaintenanceDocumentEntryForBusinessObjectClass(Class businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) businessObjectClass");
        }
        LOG.debug("calling getDocumentEntry by businessObjectClass '" + businessObjectClass + "'");

        return (MaintenanceDocumentEntry) documentEntriesByBusinessObjectClass.get(businessObjectClass);
    }

    /**
     * Note: only MaintenanceDocuments are indexed by maintainable Class
     * 
     * @param maintainableClass
     * @return DocumentEntry associated with the given Class, or null if there is none
     */
    public MaintenanceDocumentEntry getMaintenanceDocumentEntryForMaintainableClass(Class maintainableClass) {
        if (maintainableClass == null) {
            throw new IllegalArgumentException("invalid (null) maintainableClass");
        }
        LOG.debug("calling getDocumentEntry by maintainableClass '" + maintainableClass + "'");

        return (MaintenanceDocumentEntry) documentEntriesByMaintainableClass.get(maintainableClass);
    }

    /**
     * @return all document entries in the dataDictionary, indexed by documentTypeName
     */
    public Map<String,DocumentEntry> getDocumentEntries() {
        return Collections.unmodifiableMap(this.documentEntries);
    }

    /**
     * @return all transactional document entries in the dataDictionary, indexed by documentTypeName
     */
    public Map getTransactionalDocumentEntries() {
        final Map transactionalDocumentEntries = new LinkedHashMap();
        DocumentEntryVisitor v = new DocumentEntryVisitor() {
            /**
             * @see org.kuali.core.datadictionary.DocumentEntryVisitor#visitEntry(org.kuali.bo.datadictionary.DocumentEntry)
             */
            public void visitEntry(DocumentEntry documentEntry) {
                LOG.debug("calling visitEntry for entry '" + documentEntry.getDocumentTypeName() + "'");

                if (documentEntry instanceof TransactionalDocumentEntry) {
                    transactionalDocumentEntries.put(documentEntry.getDocumentTypeName(), documentEntry);
                }
            }
        };

        visitDocumentEntries(v);

        return Collections.unmodifiableMap(transactionalDocumentEntries);
    }

    /**
     * @return all maintenance document entries in the dataDictionary, indexed by documentTypeName
     */
    public Map getMaintenanceDocumentEntries() {
        final Map maintenanceDocumentEntries = new LinkedHashMap();
        DocumentEntryVisitor v = new DocumentEntryVisitor() {
            /**
             * @see org.kuali.core.datadictionary.DocumentEntryVisitor#visitEntry(org.kuali.bo.datadictionary.DocumentEntry)
             */
            public void visitEntry(DocumentEntry documentEntry) {
                LOG.debug("calling visitEntry for entry '" + documentEntry.getDocumentTypeName() + "'");

                if (documentEntry instanceof MaintenanceDocumentEntry) {
                    maintenanceDocumentEntries.put(documentEntry.getDocumentTypeName(), documentEntry);
                }
            }
        };

        visitDocumentEntries(v);

        return Collections.unmodifiableMap(maintenanceDocumentEntries);
    }

    /**
     * @param docTypeName
     * @return BusinessRules class associated with given document type
     */
    public Class getBusinessRulesClass(String docTypeName) {
        Class businessRulesClass = null;

        DocumentEntry documentEntry = getDocumentEntry(docTypeName);
        if (documentEntry != null) {
            businessRulesClass = documentEntry.getBusinessRulesClass();
        }

        return businessRulesClass;
    }


    /**
     * Given a DocumentEntryVisitor instance, execute its visitEntry method on every DocumentEntry instance in this DataDictionary.
     * 
     * @param visitor
     */
    public void visitDocumentEntries(DocumentEntryVisitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("invalid (null) visitor");
        }
        LOG.debug("calling visitDocumentEntries for visitor " + visitor.getClass().getName() + "'");

        for (Iterator i = documentEntries.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            DocumentEntry documentEntry = (DocumentEntry) e.getValue();
            visitor.visitEntry(documentEntry);
        }
    }

    /**
     * Iterates through all businessObject entries, invoking whatever methods need to be invoked to take care of any last-minute
     * tasks before the dictionary is presumed to be ready for public consumption.
     */
    public void completeInitialization(final KualiGroupService kualiGroupService, final KualiConfigurationService kualiConfigurationService) {
        final DataDictionary currentDictionary = this;

        visitBusinessObjectEntries(new BusinessObjectEntryVisitor() {
            public void visitEntry(BusinessObjectEntry businessObjectEntry) {
                businessObjectEntry.expandAttributeReferences(currentDictionary, validationCompletionUtils);
            }
        });

        visitDocumentEntries(new DocumentEntryVisitor() {
            public void visitEntry(DocumentEntry documentEntry) {
                documentEntry.validateAuthorizations(kualiGroupService);
                documentEntry.validateAuthorizer(kualiConfigurationService, validationCompletionUtils);
                documentEntry.expandAttributeReferences(currentDictionary, validationCompletionUtils);
            };
        });
    };

    public void setAllowOverrides(boolean allowOverrides) {
        LOG.debug("calling setAllowOverrides " + allowOverrides);

        this.allowOverrides = allowOverrides;
    }

    public boolean isAllowOverrides() {
        return allowOverrides;
    }
}