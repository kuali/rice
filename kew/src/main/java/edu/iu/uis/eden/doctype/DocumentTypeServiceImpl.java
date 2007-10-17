/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.doctype;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.doctype.dao.DocumentTypeDAO;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.server.BeanConverter;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.xml.DocumentTypeXmlParser;
import edu.iu.uis.eden.xml.export.DocumentTypeXmlExporter;

/**
 * The standard implementation of the DocumentTypeService.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentTypeServiceImpl.class);
    private static final String XML_FILE_PARSE_ERROR = "general.error.parsexml";

    public static final String DOCUMENT_TYPE_ID_CACHE_GROUP = "DocumentTypeId";
    public static final String DOCUMENT_TYPE_NAME_CACHE_GROUP = "DocumentTypeName";

    public static final String DOCUMENT_TYPE_ID_CACHE_PREFIX = DOCUMENT_TYPE_ID_CACHE_GROUP + ":";
    public static final String DOCUMENT_TYPE_NAME_CACHE_PREFIX = DOCUMENT_TYPE_NAME_CACHE_GROUP + ":";
    public static final String CURRENT_ROOTS_IN_CACHE_KEY = "DocumentType:CurrentRootsInCache";


    private DocumentTypeDAO documentTypeDAO;

    public Collection find(DocumentType documentType, String docTypeParentName, boolean climbHierarchy) {
        DocumentType docTypeParent = this.findByName(docTypeParentName);
        Collection documentTypes = getDocumentTypeDAO().find(documentType, docTypeParent, climbHierarchy);
        //since we're here put them in the cache
        for (Iterator iter = documentTypes.iterator(); iter.hasNext();) {
			insertIntoCache((DocumentType)iter.next());
		}
        return documentTypes;
    }

    public DocumentType findById(Long documentTypeId) {
    	if (documentTypeId == null) {
    		return null;
    	}
    	DocumentType documentType = fetchFromCacheById(documentTypeId);
    	if (documentType == null) {
    		documentType = getDocumentTypeDAO().findByDocId(documentTypeId);
    		insertIntoCache(documentType);
    	}
        return documentType;
    }

    public DocumentType findByDocumentId(Long documentId) {
    	if (documentId == null) {
    		return null;
    	}
    	Long documentTypeId = getDocumentTypeDAO().findDocumentTypeIdByDocumentId(documentId);
    	return findById(documentTypeId);
    }

    public DocumentType findByName(String name) {
    	if (name == null) {
    		return null;
    	}
        DocumentType documentType = fetchFromCacheByName(name);
        if (documentType == null) {
        	documentType = getDocumentTypeDAO().findByName(name);
        	insertIntoCache(documentType);
        }
    	return documentType;
    }

    /**
     * Fetches the DocumentType from the cache with the given document type name.  If there is no entry in the cache for the given
     * document type name, null is returned.
     */
    protected DocumentType fetchFromCacheByName(String documentTypeName) {
    	return (DocumentType)KEWServiceLocator.getCacheAdministrator().getFromCache(getNameCacheKey(documentTypeName));
    }

    /**
     * Fetches the DocumentType from the cache with the given document type id.  If there is no entry in the cache for the given
     * document type id, null is returned.
     */
    protected DocumentType fetchFromCacheById(Long documentTypeId) {
    	return (DocumentType)KEWServiceLocator.getCacheAdministrator().getFromCache(getIdCacheKey(documentTypeId));
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
    		KEWServiceLocator.getCacheAdministrator().putInCache(getNameCacheKey(documentType.getName()), documentType, DOCUMENT_TYPE_NAME_CACHE_GROUP);
    	}

    	KEWServiceLocator.getCacheAdministrator().putInCache(getIdCacheKey(documentType.getDocumentTypeId()), documentType, DOCUMENT_TYPE_ID_CACHE_GROUP);
    }

    /**
     * Flushes all DocumentTypes from the cache.
     */
    protected void flushCache() {
    	// invalidate locally because if we're doing an upload of a document hierarchy we can't wait the 5 secs for this nodes cache
		//to be accurate-the data going in the db depends on it being accurate now.  This means the cache will be cleared multiple times
    	//over during an upload and the subsequent notification to this node.
    	LOG.info("clearing DocumentType cache because of local update");
    	KEWServiceLocator.getCacheAdministrator().flushGroup(DOCUMENT_TYPE_ID_CACHE_GROUP);
    	KEWServiceLocator.getCacheAdministrator().flushGroup(DOCUMENT_TYPE_NAME_CACHE_GROUP);
    	KEWServiceLocator.getCacheAdministrator().flushEntry(CURRENT_ROOTS_IN_CACHE_KEY);
    }

    public void clearCacheForAttributeUpdate(RuleAttribute ruleAttribute) {
    	List documentTypeAttributes = this.documentTypeDAO.findDocumentTypeAttributes(ruleAttribute);
    	if (documentTypeAttributes.size() != 0) {
    		flushCache();
    	}
    }

    public void versionAndSave(DocumentType documentType) {
    	try {
    		// at this point this save is designed to version the document type by creating an entire new record if this is going to be an update and
    		// not a create just throw and exception to be on the safe side
    		if (documentType.getDocumentTypeId() != null && documentType.getLockVerNbr() != null) {
    			throw new RuntimeException("DocumentType configured for update and not versioning which we support");
    		}

    		// flush the old document type from the cache so we get a
    		DocumentType oldDocumentType = findByName(documentType.getName());
    		// reset the children on the oldDocumentType
    		//oldDocumentType.resetChildren();
    		Long existingDocTypeId = (oldDocumentType == null ? null : oldDocumentType.getDocumentTypeId());
    		if (oldDocumentType != null && existingDocTypeId.longValue() > 0) {
    			documentType.setPreviousVersionId(existingDocTypeId);
    			//TODO this used to be a query that I can't see being necessary if all our fetch code is working...
    			//delete if you're looking at this message in 2.4 release cycle
    			documentType.setVersion(new Integer(oldDocumentType.getVersion().intValue() + 1));
    			oldDocumentType.setCurrentInd(Boolean.FALSE);
    			LOG.debug("Saving old document type Id " + oldDocumentType.getDocumentTypeId() + " name " + oldDocumentType.getName());
    			save(oldDocumentType, false);
    		}
    		documentType.setCurrentInd(Boolean.TRUE);

    		save(documentType, false);
    		//attach the children to this new parent.  cloning the children would probably be a better way to go here...
    		if (documentType.getPreviousVersion() != null) {
    			for (Iterator iterator = oldDocumentType.getChildrenDocTypes().iterator(); iterator.hasNext();) {
    				DocumentType child = (DocumentType) iterator.next();
    				child.setDocTypeParentId(documentType.getDocumentTypeId());
    				save(child, false);
    			}
    		}
    		// initiate a save of this document type's parent document type, this will force a
    		// version check which should reveal (via an optimistic lock exception) whether or
    		// not there is a concurrent transaction
    		// which has modified the parent (and therefore made it non-current)
    		// be sure to get the parent doc type directly from the db and not from the cache
    		if (documentType.getDocTypeParentId() != null) {
    			DocumentType parent = getDocumentTypeDAO().findByDocId(documentType.getDocTypeParentId());
    			save(parent, false);
    		}

    		// finally, flush the cache and notify the rule cache of the DocumentType change
    		flushCache();
    		KEWServiceLocator.getRuleService().notifyCacheOfDocumentTypeChange(documentType);
    	} finally {
    		// the double flush here is necessary because of a series of events which occur inside of
    		// notifyCacheOfDocumentTypeChange, see the documentation inside that service method for
    		// more information on the problem.  Esentially, the method ends up invoking methods on
    		// this service which re-cache document types, however the document types that get
    		// re-cached are ones pulled from the OJB cache that don't have the proper children
    		// on them
    		//
    		// also we flush in the finally block because if an exception is thrown then it's still possible
    		// the the "oldDocumentType" which was fetched from the cache has had it's dbLockVerNbr incremented
    		flushCache();
    	}
    }

    protected void save(DocumentType documentType, boolean flushCache) {
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

    public DocumentTypeVO getDocumentTypeVO(Long documentTypeId) {
        DocumentType docType = findById(documentTypeId);
        return BeanConverter.convertDocumentType(docType);
    }

    public DocumentTypeVO getDocumentTypeVO(String documentTypeName) {
        DocumentType documentType = findByName(documentTypeName);
        return BeanConverter.convertDocumentType(documentType);
    }

    public synchronized List findAllCurrentRootDocuments() {
    	List currentRootsInCache = (List) KEWServiceLocator.getCacheAdministrator().getFromCache(CURRENT_ROOTS_IN_CACHE_KEY);
    	//we can do this because we whack the entire cache when a new document type comes into the picture.
    	if (currentRootsInCache == null) {
    		currentRootsInCache = getDocumentTypeDAO().findAllCurrentRootDocuments();
    		KEWServiceLocator.getCacheAdministrator().putInCache(CURRENT_ROOTS_IN_CACHE_KEY, currentRootsInCache);
    	}
    	return currentRootsInCache;
    }

    public List findAllCurrent() {
        return getDocumentTypeDAO().findAllCurrent();
    }

    public DocumentType findRootDocumentType(DocumentType docType) {
        if (docType.getParentDocType() != null) {
            return findRootDocumentType(docType.getParentDocType());
        } else {
            return docType;
        }
    }

    public void loadXml(InputStream inputStream, WorkflowUser user) {
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

    public List getChildDocumentTypes(DocumentType documentType) {
    	List childDocumentTypes = new ArrayList();
    	List childIds = getDocumentTypeDAO().getChildDocumentTypeIds(documentType.getDocumentTypeId());
    	for (Iterator iter = childIds.iterator(); iter.hasNext();) {
			Long documentTypeId = (Long) iter.next();
			childDocumentTypes.add(findById(documentTypeId));
		}
    	return childDocumentTypes;
    }

}