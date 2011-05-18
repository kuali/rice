/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.doctype.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.dao.DocumentTypeDAO;
import org.kuali.rice.kew.doctype.service.DocumentTypePermissionService;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.xml.DocumentTypeXmlParser;
import org.kuali.rice.kew.xml.export.DocumentTypeXmlExporter;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.ksb.api.bus.services.KsbApiServiceLocator;


/**
 * The standard implementation of the DocumentTypeService.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentTypeServiceImpl.class);
    protected static final String XML_FILE_PARSE_ERROR = "general.error.parsexml";

    public static final String DOCUMENT_TYPE_ID_CACHE_GROUP = "DocumentTypeId";
    public static final String DOCUMENT_TYPE_NAME_CACHE_GROUP = "DocumentTypeName";
    public static final String DOCUMENT_TYPE_DTO_ID_CACHE_GROUP = "DocumentTypeDTOId";
    public static final String DOCUMENT_TYPE_DTO_NAME_CACHE_GROUP = "DocumentTypeDTOName";

    public static final String DOCUMENT_TYPE_ID_CACHE_PREFIX = DOCUMENT_TYPE_ID_CACHE_GROUP + ":";
    public static final String DOCUMENT_TYPE_NAME_CACHE_PREFIX = DOCUMENT_TYPE_NAME_CACHE_GROUP + ":";
    public static final String DOCUMENT_TYPE_DTO_ID_CACHE_PREFIX = DOCUMENT_TYPE_DTO_ID_CACHE_GROUP + ":";
    public static final String DOCUMENT_TYPE_DTO_NAME_CACHE_PREFIX = DOCUMENT_TYPE_DTO_NAME_CACHE_GROUP + ":";
    public static final String CURRENT_ROOTS_IN_CACHE_KEY = "DocumentType:CurrentRootsInCache";


    private DocumentTypeDAO documentTypeDAO;

    public Collection<DocumentType> find(DocumentType documentType, String docTypeParentName, boolean climbHierarchy) {
        DocumentType docTypeParent = this.findByName(docTypeParentName);
        Collection<DocumentType> documentTypes = getDocumentTypeDAO().find(documentType, docTypeParent, climbHierarchy);
        //since we're here put them in the cache
        for (Object documentType1 : documentTypes)
        {
            insertIntoCache((DocumentType) documentType1);
        }
        return documentTypes;
    }

    public DocumentType findById(Long documentTypeId) {
    	if (documentTypeId == null) {
    		return null;
    	}
    	DocumentType documentType = fetchFromCacheById(documentTypeId);
    	if (documentType == null) {
    		documentType = getDocumentTypeDAO().findById(documentTypeId);
    		insertIntoCache(documentType);
    	}
        return documentType;
    }

    public DocumentType findByDocumentId(String documentId) {
    	if (documentId == null) {
    		return null;
    	}
    	Long documentTypeId = getDocumentTypeDAO().findDocumentTypeIdByDocumentId(documentId);
    	return findById(documentTypeId);
    }

    public DocumentType findByName(String name) {
    	return this.findByName(name, true, true);
    }

    public DocumentType findByNameCaseInsensitive(String name) {
    	return this.findByName(name, false,true);
    }

    /**
     * 
     * This method seaches for a DocumentType by document name.
     * 
     * @param name
     * @param caseSensitive
     * @deprecated Use findByName(String name, boolean caseSensitive, boolean checkCache)
     * @return
     */
    protected DocumentType findByName(String name, boolean caseSensitive) {
    	if (name == null) {
    		return null;
    	}
    	DocumentType documentType = fetchFromCacheByName(name);
        if (documentType == null) {
        	documentType = getDocumentTypeDAO().findByName(name, caseSensitive);
        	insertIntoCache(documentType);
        }
    	return documentType;
    }

    /**
     * 
     * This method seaches for a DocumentType by document name.
     * 
     * @param name DocumentType name
     * @param caseSensitive If false, case will be ignored
     * @param checkCache if false the cache will not be checked.
     * @return
     */
    protected DocumentType findByName(String name, boolean caseSensitive, boolean checkCache) {
    	if (name == null) {
    		return null;
    	}
    	
    	DocumentType documentType = null;
    	if(checkCache){
    		documentType = fetchFromCacheByName(name);
    	}
        if (documentType == null) {
        	documentType = getDocumentTypeDAO().findByName(name, caseSensitive);
        	insertIntoCache(documentType);
        }
    	return documentType;
    }

    /**
     * Fetches the DocumentType from the cache with the given document type name.  If there is no entry in the cache for the given
     * document type name, null is returned.
     */
    protected DocumentType fetchFromCacheByName(String documentTypeName) {
    	return (DocumentType) KsbApiServiceLocator.getCacheAdministrator().getFromCache(getNameCacheKey(documentTypeName));
    }

    /**
     * Fetches the DocumentType from the cache with the given document type id.  If there is no entry in the cache for the given
     * document type id, null is returned.
     */
    protected DocumentType fetchFromCacheById(Long documentTypeId) {
    	return (DocumentType) KsbApiServiceLocator.getCacheAdministrator().getFromCache(getIdCacheKey(documentTypeId));
    }

    /**
     * Returns the cache key for the given document type ID.
     */
    protected String getIdCacheKey(Long documentTypeId) {
    	return DOCUMENT_TYPE_ID_CACHE_PREFIX + documentTypeId.toString();
    }

    /**
     * Returns the cache key for the given document type name.
     */
    protected String getNameCacheKey(String documentTypeName) {
    	return DOCUMENT_TYPE_NAME_CACHE_PREFIX + documentTypeName;
    }

    /**
     * Inserts the given DocumentType into the name and id caches.  If the DocumentType is already in the cache,
     * these entries should  be overwritten.
     *
     * <p>If the given DocumentType does not represent the current version of the DocumentType then it
     * should not be inserted into the name cache.  This is because different versions of DocumentTypes have
     * different IDs but they all have the same name.  We want only the most recent version of the DocumentType
     * to be cached by name.
     */
    protected void insertIntoCache(DocumentType documentType) {
    	if (documentType == null) {
    		return;
    	}
    	//don't cache by name if this isn't the current version
    	if (documentType.getCurrentInd().booleanValue()) {
    		KsbApiServiceLocator.getCacheAdministrator().putInCache(getNameCacheKey(documentType.getName()), documentType, DOCUMENT_TYPE_NAME_CACHE_GROUP);
    	}

    	KsbApiServiceLocator.getCacheAdministrator().putInCache(getIdCacheKey(documentType.getDocumentTypeId()), documentType, DOCUMENT_TYPE_ID_CACHE_GROUP);
    }

    /**
     * Fetches the DocumentType from the cache with the given document type name.  If there is no entry in the cache for the given
     * document type name, null is returned.
     */
    protected DocumentTypeDTO fetchDTOFromCacheByName(String documentTypeName) {
    	return (DocumentTypeDTO) KsbApiServiceLocator.getCacheAdministrator().getFromCache(getDTONameCacheKey(documentTypeName));
    }

    /**
     * Fetches the DocumentType from the cache with the given document type id.  If there is no entry in the cache for the given
     * document type id, null is returned.
     */
    protected DocumentTypeDTO fetchDTOFromCacheById(Long documentTypeId) {
    	return (DocumentTypeDTO) KsbApiServiceLocator.getCacheAdministrator().getFromCache(getDTOIdCacheKey(documentTypeId));
    }

    /**
     * Returns the cache key for the given document type ID.
     */
    protected String getDTOIdCacheKey(Long documentTypeId) {
    	return DOCUMENT_TYPE_DTO_ID_CACHE_PREFIX + documentTypeId.toString();
    }

    /**
     * Returns the cache key for the given document type name.
     */
    protected String getDTONameCacheKey(String documentTypeName) {
    	return DOCUMENT_TYPE_DTO_NAME_CACHE_PREFIX + documentTypeName;
    }

    /**
     * Inserts the given DocumentType into the name and id caches.  If the DocumentType is already in the cache,
     * these entries should  be overwritten.
     *
     * <p>If the given DocumentType does not represent the current version of the DocumentType then it
     * should not be inserted into the name cache.  This is because different versions of DocumentTypes have
     * different IDs but they all have the same name.  We want only the most recent version of the DocumentType
     * to be cached by name.
     */
    protected void insertDTOIntoCache(DocumentTypeDTO documentType) {
    	if (documentType == null) {
    		return;
    	}
    	//don't cache by name if this isn't the current version
    	if ( documentType.getDocTypeCurrentInd().equals(KEWConstants.ACTIVE_CD) ) {
    		KsbApiServiceLocator.getCacheAdministrator().putInCache(getDTONameCacheKey(documentType.getName()), documentType, DOCUMENT_TYPE_DTO_NAME_CACHE_GROUP);
    	}

    	KsbApiServiceLocator.getCacheAdministrator().putInCache(getDTOIdCacheKey(documentType.getDocTypeId()), documentType, DOCUMENT_TYPE_DTO_ID_CACHE_GROUP);
    }

    /**
     * Flushes all DocumentTypes from the cache.
     */
    public void flushCache() {
    	// invalidate locally because if we're doing an upload of a document hierarchy we can't wait the 5 secs for this nodes cache
		//to be accurate-the data going in the db depends on it being accurate now.  This means the cache will be cleared multiple times
    	//over during an upload and the subsequent notification to this node.
    	LOG.info("clearing DocumentType cache because of local update");
    	KsbApiServiceLocator.getCacheAdministrator().flushGroup(DOCUMENT_TYPE_ID_CACHE_GROUP);
    	KsbApiServiceLocator.getCacheAdministrator().flushGroup(DOCUMENT_TYPE_NAME_CACHE_GROUP);
    	KsbApiServiceLocator.getCacheAdministrator().flushGroup(DOCUMENT_TYPE_DTO_ID_CACHE_GROUP);
    	KsbApiServiceLocator.getCacheAdministrator().flushGroup(DOCUMENT_TYPE_DTO_NAME_CACHE_GROUP);
    	KsbApiServiceLocator.getCacheAdministrator().flushGroup(DocumentTypePermissionService.DOC_TYPE_PERM_CACHE_GROUP);
    	KsbApiServiceLocator.getCacheAdministrator().flushEntry(CURRENT_ROOTS_IN_CACHE_KEY);
    }

    public void clearCacheForAttributeUpdate(RuleAttribute ruleAttribute) {
    	if (ruleAttribute.getRuleAttributeId() != null) {
    		List documentTypeAttributes = this.documentTypeDAO.findDocumentTypeAttributes(ruleAttribute);
    		if (documentTypeAttributes.size() != 0) {
    			flushCache();
    		}
    	}
    }

    public void versionAndSave(DocumentType documentType) {
    	try {
    		// at this point this save is designed to version the document type by creating an entire new record if this is going to be an update and
    		// not a create just throw and exception to be on the safe side
    		if (documentType.getDocumentTypeId() != null && documentType.getVersionNumber() != null) {
    			throw new RuntimeException("DocumentType configured for update and not versioning which we support");
    		}

    		// grab the old document. Don't Use Cached Version!
    		DocumentType oldDocumentType = findByName(documentType.getName(), true, false);
    		// reset the children on the oldDocumentType
    		//oldDocumentType.resetChildren();
    		Long existingDocTypeId = null;
    		if (oldDocumentType != null) {
        		existingDocTypeId = oldDocumentType.getDocumentTypeId();
        		if (existingDocTypeId.longValue() > 0) {
        		    // set version number on the new doc type using the max version from the database
        		    Integer maxVersionNumber = documentTypeDAO.getMaxVersionNumber(documentType.getName());
        			documentType.setVersion((maxVersionNumber != null) ? new Integer(maxVersionNumber.intValue() + 1) : new Integer(0));
        			oldDocumentType.setCurrentInd(Boolean.FALSE);
        			if ( LOG.isInfoEnabled() ) { 
        				LOG.info("Saving old document type Id " + oldDocumentType.getDocumentTypeId() + " name '" + oldDocumentType.getName() + "' (current = " + oldDocumentType.getCurrentInd() + ")");
        			}
        			save(oldDocumentType, false);
        		}
    		}
    		// check to see that no current documents exist in database
    		if (!CollectionUtils.isEmpty(documentTypeDAO.findAllCurrentByName(documentType.getName()))) {
    		    String errorMsg = "Found invalid 'current' document with name '" + documentType.getName() + "'.  None should exist.";
    		    LOG.error(errorMsg);
    		    throw new RuntimeException(errorMsg);
    		}
            // set up the previous current doc type on the new doc type
            documentType.setPreviousVersionId(existingDocTypeId);
            documentType.setCurrentInd(Boolean.TRUE);
    		save(documentType, false);
    		if ( LOG.isInfoEnabled() ) { 
    			LOG.info("Saved current document type Id " + documentType.getDocumentTypeId() + " name '" + documentType.getName() + "' (current = " + documentType.getCurrentInd() + ")");
    		}
    		//attach the children to this new parent.  cloning the children would probably be a better way to go here...
    		if (ObjectUtils.isNotNull(existingDocTypeId)) {
    		    // documentType.getPreviousVersion() should not be null at this point
                for (Iterator iterator = getChildDocumentTypes(existingDocTypeId).iterator(); iterator.hasNext();) {
//    			for (Iterator iterator = oldDocumentType.getChildrenDocTypes().iterator(); iterator.hasNext();) {
    				DocumentType child = (DocumentType) iterator.next();
    				child.setDocTypeParentId(documentType.getDocumentTypeId());
    				save(child, false);
    				if ( LOG.isInfoEnabled() ) { 
    					LOG.info("Saved child document type Id " + child.getDocumentTypeId() + " name '" + child.getName() + "' (parent = " + child.getDocTypeParentId() + ", current = " + child.getCurrentInd() + ")");
    				}
    			}
    		}
    		// initiate a save of this document type's parent document type, this will force a
    		// version check which should reveal (via an optimistic lock exception) whether or
    		// not there is a concurrent transaction
    		// which has modified the parent (and therefore made it non-current)
    		// be sure to get the parent doc type directly from the db and not from the cache
    		if (documentType.getDocTypeParentId() != null) {
    			DocumentType parent = getDocumentTypeDAO().findById(documentType.getDocTypeParentId());
    			save(parent, false);
    			if ( LOG.isInfoEnabled() ) { 
    				LOG.info("Saved parent document type Id " + parent.getDocumentTypeId() + " name '" + parent.getName() + "' (current = " + parent.getCurrentInd() + ")");
    			}
    		}

    		// finally, flush the cache and notify the rule cache of the DocumentType change
    		flushCache();
    		KEWServiceLocator.getRuleService().notifyCacheOfDocumentTypeChange(documentType);
    	} finally {
    		// the double flush here is necessary because of a series of events which occur inside of
    		// notifyCacheOfDocumentTypeChange, see the documentation inside that service method for
    		// more information on the problem.  Essentially, the method ends up invoking methods on
    		// this service which re-cache document types, however the document types that get
    		// re-cached are ones pulled from the OJB cache that don't have the proper children
    		// on them
    		//
    		// also we flush in the finally block because if an exception is thrown then it's still possible
    		// the the "oldDocumentType" which was fetched from the cache has had it's dbLockVerNbr incremented
    		flushCache();
    	}
    }

    public void save(DocumentType documentType, boolean flushCache) {
    	getDocumentTypeDAO().save(documentType);
    	if (flushCache) {
    		// always clear the entire cache
    		flushCache();
        	KEWServiceLocator.getRuleService().notifyCacheOfDocumentTypeChange(documentType);
        	flushCache();
    	}
    }

    public void save(DocumentType documentType) {
    	save(documentType, true);
    }

    public DocumentTypeDAO getDocumentTypeDAO() {
        return documentTypeDAO;
    }

    public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
        this.documentTypeDAO = documentTypeDAO;
    }

    public DocumentTypeDTO getDocumentTypeVO(Long documentTypeId) {
        DocumentTypeDTO dto = fetchDTOFromCacheById(documentTypeId);
        if ( dto == null ) {
            DocumentType docType = findById(documentTypeId);
            if ( docType == null ) {
            	return null;
            }
        	dto = DTOConverter.convertDocumentType(docType);
        	insertDTOIntoCache(dto);
        }
        
        return dto;
    }

    public DocumentTypeDTO getDocumentTypeVO(String documentTypeName) {
        DocumentTypeDTO dto = fetchDTOFromCacheByName(documentTypeName);
        if ( dto == null ) {
            DocumentType docType = findByName(documentTypeName);
            if ( docType == null ) {
            	return null;
            }
        	dto = DTOConverter.convertDocumentType(docType);
        	insertDTOIntoCache(dto);
        }
        return dto;
    }

    public synchronized List findAllCurrentRootDocuments() {
    	List currentRootsInCache = (List) KsbApiServiceLocator.getCacheAdministrator().getFromCache(CURRENT_ROOTS_IN_CACHE_KEY);
    	//we can do this because we whack the entire cache when a new document type comes into the picture.
    	if (currentRootsInCache == null) {
    		currentRootsInCache = getDocumentTypeDAO().findAllCurrentRootDocuments();
    		KsbApiServiceLocator.getCacheAdministrator().putInCache(CURRENT_ROOTS_IN_CACHE_KEY, currentRootsInCache);
    	}
    	return currentRootsInCache;
    }

    public List findAllCurrent() {
        return getDocumentTypeDAO().findAllCurrent();
    }

    public List<DocumentType> findPreviousInstances(String documentTypeName) {
        return getDocumentTypeDAO().findPreviousInstances(documentTypeName);
    }

    public DocumentType findRootDocumentType(DocumentType docType) {
        if (docType.getParentDocType() != null) {
            return findRootDocumentType(docType.getParentDocType());
        } else {
            return docType;
        }
    }

    public void loadXml(InputStream inputStream, String principalId) {
        DocumentTypeXmlParser parser = new DocumentTypeXmlParser();
        try {
            parser.parseDocumentTypes(inputStream);
        } catch (Exception e) {
            WorkflowServiceErrorException wsee = new WorkflowServiceErrorException("Error parsing documentType XML file", new WorkflowServiceErrorImpl("Error parsing documentType XML file", XML_FILE_PARSE_ERROR));
            wsee.initCause(e);
            throw wsee;
        }
    }

    public Element export(ExportDataSet dataSet) {
        DocumentTypeXmlExporter exporter = new DocumentTypeXmlExporter();
        return exporter.export(dataSet);
    }
    
    @Override
	public boolean supportPrettyPrint() {
		return true;
	}

    public List getChildDocumentTypes(Long documentTypeId) {
    	List childDocumentTypes = new ArrayList();
    	List childIds = getDocumentTypeDAO().getChildDocumentTypeIds(documentTypeId);
    	for (Iterator iter = childIds.iterator(); iter.hasNext();) {
			Long childDocumentTypeId = (Long) iter.next();
			childDocumentTypes.add(findById(childDocumentTypeId));
		}
    	return childDocumentTypes;
    }

}
