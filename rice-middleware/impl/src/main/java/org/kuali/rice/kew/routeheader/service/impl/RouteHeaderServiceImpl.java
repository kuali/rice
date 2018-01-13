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
package org.kuali.rice.kew.routeheader.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionItem;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.docsearch.dao.SearchableAttributeDAO;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;
import org.kuali.rice.kew.routeheader.dao.DocumentRouteHeaderDAO;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;
import org.springframework.beans.factory.annotation.Required;

import javax.persistence.OptimisticLockException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RouteHeaderServiceImpl implements RouteHeaderService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RouteHeaderServiceImpl.class);

    private DocumentRouteHeaderDAO routeHeaderDAO;
    private SearchableAttributeDAO searchableAttributeDAO;

    private DataObjectService dataObjectService;

    public DocumentRouteHeaderValue getRouteHeader(String documentId) {
        return getDataObjectService().find(DocumentRouteHeaderValue.class, documentId);
    }

    public DocumentRouteHeaderValue getRouteHeader(String documentId, boolean clearCache) {
    	return getRouteHeaderDAO().findRouteHeader(documentId, clearCache);
    }

    public Collection<DocumentRouteHeaderValue> getRouteHeaders(Collection<String> documentIds) {
         return getRouteHeaderDAO().findRouteHeaders(documentIds);
    }
    
    public Collection<DocumentRouteHeaderValue> getRouteHeaders(Collection<String> documentIds, boolean clearCache) {
    	return getRouteHeaderDAO().findRouteHeaders(documentIds, clearCache);
    }
    
    public Map<String,DocumentRouteHeaderValue> getRouteHeadersForActionItems(Collection<ActionItem> actionItems) {
    	Map<String,DocumentRouteHeaderValue> routeHeaders = new HashMap<String,DocumentRouteHeaderValue>();
    	List<String> documentIds = new ArrayList<String>(actionItems.size());
    	for (ActionItem actionItem : actionItems) {
    		documentIds.add(actionItem.getDocumentId());
    	}
    	Collection<DocumentRouteHeaderValue> actionItemRouteHeaders = getRouteHeaders(documentIds);
    	if (actionItemRouteHeaders != null) {
    		for (DocumentRouteHeaderValue routeHeader : actionItemRouteHeaders) {
    			routeHeaders.put(routeHeader.getDocumentId(), routeHeader);
    		}
    	}
    	return routeHeaders;
    }
    
    public void lockRouteHeader(String documentId) {
        getRouteHeaderDAO().lockRouteHeader(documentId);
        LOG.debug("Successfully locked document [docId=" + documentId + "]");
    }

    public DocumentRouteHeaderValue saveRouteHeader(DocumentRouteHeaderValue routeHeader) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "About to Save the route Header: " + routeHeader.getDocumentId() + " / version="
                    + routeHeader.getVersionNumber() );
            DocumentRouteHeaderValue currHeader = getDataObjectService().find(DocumentRouteHeaderValue.class,
                    routeHeader.getDocumentId());
            if ( currHeader != null ) {
                LOG.debug( "Current Header Version: " + currHeader.getVersionNumber() );
            } else {
                LOG.debug( "Current Header: null" );
            }
            LOG.debug( ExceptionUtils.getStackTrace(new Throwable()) );
        }
        try {
            // before saving, copy off the document content, since it's transient it will get erased during a JPA merge
            DocumentRouteHeaderValueContent content = routeHeader.getDocumentContent();
            DocumentRouteHeaderValue drvPersisted = dataObjectService.save(routeHeader, PersistenceOption.FLUSH);
            // now let's save the content and reattach it to our document
            content.setDocumentId(drvPersisted.getDocumentId());
            content = dataObjectService.save(content);
            drvPersisted.setDocumentContent(content);
            return drvPersisted;
        } catch ( RuntimeException ex ) {
            if ( ex.getCause() instanceof OptimisticLockException) {
                LOG.error( "Optimistic Locking Exception saving document header or content. Offending object: "
                        + ex.getCause()
                        + "; DocumentId = " + routeHeader.getDocumentId() + " ;  Version Number = "
                        + routeHeader.getVersionNumber());
            }
            LOG.error( "Unable to save document header or content. Route Header: " + routeHeader, ex );
            throw ex;
        }
    }

    public void deleteRouteHeader(DocumentRouteHeaderValue routeHeader) {
        dataObjectService.delete(routeHeader);
    }

    public String getNextDocumentId() {
        return getRouteHeaderDAO().getNextDocumentId();
    }

    public Collection findPendingByResponsibilityIds(Set responsibilityIds) {
        return getRouteHeaderDAO().findPendingByResponsibilityIds(responsibilityIds);
    }

    public void clearRouteHeaderSearchValues(String documentId) {
        getRouteHeaderDAO().clearRouteHeaderSearchValues(documentId);
    }
    
    public void updateRouteHeaderSearchValues(String documentId, List<SearchableAttributeValue> searchAttributes) {
        getRouteHeaderDAO().clearRouteHeaderSearchValues(documentId);
        HashSet<String> dupedSet = new HashSet<String>();
        //"de-dupe" for value,key,and doc header id
        for (SearchableAttributeValue searchAttribute : searchAttributes) {
            if(searchAttribute != null){
                String fakeKey = searchAttribute.getSearchableAttributeKey() + "-" + searchAttribute.getSearchableAttributeValue();
                if(!dupedSet.contains(fakeKey)){
                    getRouteHeaderDAO().save(searchAttribute);
                    dupedSet.add(fakeKey);
                }
            }
        }
        LOG.warn("Deduplication adjusted incoming SearchableAttributeValue list from original: " + searchAttributes.size() + " entries into : "  + (searchAttributes.size() - dupedSet.size()) + " entries.");
    }

    public void validateRouteHeader(DocumentRouteHeaderValue routeHeader){
        LOG.debug("Enter validateRouteHeader(..)");
        List errors = new ArrayList();

        if (routeHeader.getDocRouteStatus() == null || routeHeader.getDocRouteStatus().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route status null.", "routeheader.routestatus.empty"));
        } else if (!KewApiConstants.DOCUMENT_STATUSES.containsKey(routeHeader.getDocRouteStatus())){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route status invalid.", "routeheader.routestatus.invalid"));
        }

        if(routeHeader.getDocRouteLevel() == null || routeHeader.getDocRouteLevel().intValue() < 0){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route level invalid.", "routeheader.routelevel.invalid"));
        }

        if(routeHeader.getDateLastModified() == null){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader status modification date empty.", "routeheader.statusmoddate.empty"));
        }

        if(routeHeader.getCreateDate() == null){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader status create date empty.", "routeheader.createdate.empty"));
        }
        if(routeHeader.getDocVersion() == null || routeHeader.getDocVersion().intValue() < 0){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader doc version invalid.", "routeheader.docversion.invalid"));
        }

        if (routeHeader.getInitiatorWorkflowId () == null || routeHeader.getInitiatorWorkflowId().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("RouteHeader initiator null.", "routeheader.initiator.empty"));
        }
        else
        {
           	Principal principal = KimApiServiceLocator.getIdentityService().getPrincipal(routeHeader.getInitiatorWorkflowId());
            if(principal == null)
            {
               	errors.add(new WorkflowServiceErrorImpl("RouteHeader initiator id invalid.", "routeheader.initiator.invalid"));
            }
        }

        if(!StringUtils.isBlank(routeHeader.getDocumentTypeId())){
            DocumentType docType = KEWServiceLocator.getDocumentTypeService().findById(routeHeader.getDocumentTypeId());
            if(docType == null){
                errors.add(new WorkflowServiceErrorImpl("RouteHeader document type id invalid.", "routeheader.doctypeid.invalid"));
            }
        }

        LOG.debug("Exit validateRouteHeader(..) ");
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("RouteHeader Validation Error", errors);
        }
    }

    public String getApplicationIdByDocumentId(String documentId) {
    	return getRouteHeaderDAO().getApplicationIdByDocumentId(documentId);
    }

    public DocumentRouteHeaderValueContent getContent(String documentId) {
    	if (documentId == null) {
    		return new DocumentRouteHeaderValueContent();
    	}
        DocumentRouteHeaderValueContent content = getRouteHeaderDAO().getContent(documentId);
    	if (content == null) {
    		content = new DocumentRouteHeaderValueContent(documentId);
    	}
    	return content;
    }

    public boolean hasSearchableAttributeValue(String documentId, String searchableAttributeKey, String searchableAttributeValue) {
	return getRouteHeaderDAO().hasSearchableAttributeValue(documentId, searchableAttributeKey, searchableAttributeValue);
    }

    public String getDocumentStatus(String documentId) {
    	return getRouteHeaderDAO().getDocumentStatus(documentId);
    }
    
    public String getAppDocId(String documentId) {
 	 	if (documentId == null) {
 	 		return null;
 	 	}
 	 	return getRouteHeaderDAO().getAppDocId(documentId);
    }

    public String getAppDocStatus(String documentId) {
        if (documentId == null) {
            return null;
        }
        return getRouteHeaderDAO().getAppDocStatus(documentId);
    }

    public DocumentRouteHeaderDAO getRouteHeaderDAO() {
        return routeHeaderDAO;
    }

    public void setRouteHeaderDAO(DocumentRouteHeaderDAO routeHeaderDAO) {
        this.routeHeaderDAO = routeHeaderDAO;
    }

	public List<Timestamp> getSearchableAttributeDateTimeValuesByKey(
			String documentId, String key) {
		return getSearchableAttributeDAO().getSearchableAttributeDateTimeValuesByKey(documentId, key);
	}

	public List<BigDecimal> getSearchableAttributeFloatValuesByKey(
			String documentId, String key) {
		return getSearchableAttributeDAO().getSearchableAttributeFloatValuesByKey(documentId, key);
	}

	public List<Long> getSearchableAttributeLongValuesByKey(String documentId,
			String key) {
		return getSearchableAttributeDAO().getSearchableAttributeLongValuesByKey(documentId, key);
	}

	public List<String> getSearchableAttributeStringValuesByKey(
			String documentId, String key) {

		return getSearchableAttributeDAO().getSearchableAttributeStringValuesByKey(documentId, key);
	}

	public void setSearchableAttributeDAO(SearchableAttributeDAO searchableAttributeDAO) {
		this.searchableAttributeDAO = searchableAttributeDAO;
	}

	public SearchableAttributeDAO getSearchableAttributeDAO() {
		return searchableAttributeDAO;
	}

	public Collection findByDocTypeAndAppId(String documentTypeName,
			String appId) {
		return getRouteHeaderDAO().findByDocTypeAndAppId(documentTypeName, appId);
	}

    public DataObjectService getDataObjectService(){
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }
}
