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
package org.kuali.rice.kew.doctype.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.jdom.Element;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.dao.DocumentTypeDAO;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.xml.DocumentTypeXmlParser;
import org.kuali.rice.kew.xml.export.DocumentTypeXmlExporter;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.kuali.rice.krad.util.KRADUtils;
import org.springframework.beans.factory.annotation.Required;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

/**
 * The standard implementation of the DocumentTypeService.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * This class does not support KEW REMOTE mode.
 * KULRICE-7770 added an expicit check for this class in GlobalResourceDelegatingSpringCreator.java
 */
public class DocumentTypeServiceImpl implements DocumentTypeService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentTypeServiceImpl.class);
    protected static final String XML_FILE_PARSE_ERROR = "general.error.parsexml";

    private DocumentTypeDAO documentTypeDAO;
    private DataObjectService dataObjectService;

    public Collection<DocumentType> find(DocumentType documentType, String docTypeParentName, boolean climbHierarchy) {
       DocumentType docTypeParent = this.findByName(docTypeParentName);
       return getDocumentTypeDAO().find(documentType, docTypeParent, climbHierarchy);
    }

    public DocumentType findById(String documentTypeId) {
    	if (documentTypeId == null) {
    		return null;
    	}
        return getDataObjectService().find(DocumentType.class,documentTypeId);
    }

    public DocumentType findByDocumentId(String documentId) {
    	if (documentId == null) {
    		return null;
    	}

        return documentTypeDAO.findDocumentTypeByDocumentId(documentId);
    }

    public DocumentType findByName(String name) {
    	return this.findByName(name, true);
    }

    public DocumentType findByNameCaseInsensitive(String name) {
    	return this.findByName(name, false);
    }

    /**
     * 
     * This method seaches for a DocumentType by document name.
     * 
     * @param name
     * @param caseSensitive
     * @return
     */
    private DocumentType findByName(String name, boolean caseSensitive) {
    	if (name == null) {
    		return null;
    	}
        return documentTypeDAO.findByName(name, caseSensitive);
    }

    public DocumentType versionAndSave(DocumentType documentType) {
        // at this point this save is designed to version the document type by creating an entire new record if this is going to be an update and
        // not a create just throw and exception to be on the safe side
        if (documentType.getDocumentTypeId() != null && documentType.getVersionNumber() != null) {
            throw new RuntimeException("DocumentType configured for update and not versioning which we support");
        }

        // grab the old document. Don't Use Cached Version!
        DocumentType oldDocumentType = findByName(documentType.getName());
        // reset the children on the oldDocumentType
        //oldDocumentType.resetChildren();
        String existingDocTypeId = null;
        if (oldDocumentType != null) {
            existingDocTypeId = oldDocumentType.getDocumentTypeId();
            // set version number on the new doc type using the max version from the database
            Integer maxVersionNumber = documentTypeDAO.getMaxVersionNumber(documentType.getName());
            documentType.setVersion((maxVersionNumber != null) ? new Integer(maxVersionNumber.intValue() + 1) : new Integer(0));
            oldDocumentType.setCurrentInd(Boolean.FALSE);
            if ( LOG.isInfoEnabled() ) {
                LOG.info("Saving old document type Id " + oldDocumentType.getDocumentTypeId() + " name '" + oldDocumentType.getName() + "' (current = " + oldDocumentType.getCurrentInd() + ")");
            }
            oldDocumentType = save(oldDocumentType);
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
        documentType = save(documentType);
        if ( LOG.isInfoEnabled() ) {
            LOG.info("Saved current document type Id " + documentType.getDocumentTypeId() + " name '" + documentType.getName() + "' (current = " + documentType.getCurrentInd() + ")");
        }
        //attach the children to this new parent.  cloning the children would probably be a better way to go here...
        if (existingDocTypeId != null) {
            // documentType.getPreviousVersion() should not be null at this point
            for (Iterator iterator = getChildDocumentTypes(existingDocTypeId).iterator(); iterator.hasNext();) {
//    			for (Iterator iterator = oldDocumentType.getChildrenDocTypes().iterator(); iterator.hasNext();) {
                DocumentType child = (DocumentType) iterator.next();
                child.setDocTypeParentId(documentType.getDocumentTypeId());
                save(child);
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
            DocumentType parent = getDataObjectService().find(DocumentType.class,documentType.getDocTypeParentId());
            save(parent);
            if ( LOG.isInfoEnabled() ) {
                LOG.info("Saved parent document type Id " + parent.getDocumentTypeId() + " name '" + parent.getName() + "' (current = " + parent.getCurrentInd() + ")");
            }
        }
        return documentType;
    }

    public String findParentNameByName(String documentTypeName) {
        return getDocumentTypeDAO().findParentNameByName(documentTypeName);
    }

    public DocumentType save(DocumentType documentType) {
        // any time that we save a document type, let's grab an optimistic lock on it's parent document type, that's
        // because perhaps another transaction created a new version of the parent doc type, if we don't optimistic
        // lock and fail this transaction, then we will end up linking to a non-current parent version!
        if (documentType.getParentId() != null) {
            getDocumentTypeDAO().incrementOptimisticLock(documentType.getParentId());
        }
        return getDataObjectService().save(documentType, PersistenceOption.FLUSH);
    }

    public DocumentTypeDAO getDocumentTypeDAO() {
        return documentTypeDAO;
    }

    public void setDocumentTypeDAO(DocumentTypeDAO documentTypeDAO) {
        this.documentTypeDAO = documentTypeDAO;
    }

    public synchronized List findAllCurrentRootDocuments() {
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                isNull("docTypeParentId"),
                equal("currentInd", Boolean.TRUE)
        );
        QueryResults<DocumentType> results = getDataObjectService().findMatching(DocumentType.class, builder.build());
        return results.getResults();
    }

    public List findAllCurrent() {
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                equal("currentInd", Boolean.TRUE)
        );
        QueryResults<DocumentType> results = getDataObjectService().findMatching(DocumentType.class, builder.build());
        return results.getResults();
    }

    public List<DocumentType> findPreviousInstances(String documentTypeName) {
        QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(
                equal("name",documentTypeName),
                equal("currentInd", Boolean.FALSE)
        );
        QueryResults<DocumentType> results = getDataObjectService().findMatching(DocumentType.class, builder.build());
        return results.getResults();
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

    public List getChildDocumentTypes(String documentTypeId) {
    	List childDocumentTypes = new ArrayList();
    	List childIds = getDocumentTypeDAO().getChildDocumentTypeIds(documentTypeId);
    	for (Iterator iter = childIds.iterator(); iter.hasNext();) {
			String childDocumentTypeId = (String) iter.next();
			childDocumentTypes.add(findById(childDocumentTypeId));
		}
    	return childDocumentTypes;
    }


    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
