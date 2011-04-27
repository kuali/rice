/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.routeheader.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.kew.actionitem.ActionItem;
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
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.entity.KimPrincipal;


public class RouteHeaderServiceImpl implements RouteHeaderService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RouteHeaderServiceImpl.class);

    private DocumentRouteHeaderDAO routeHeaderDAO;
    private SearchableAttributeDAO searchableAttributeDAO;

    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId) {
        return getRouteHeaderDAO().findRouteHeader(routeHeaderId);
    }

    public DocumentRouteHeaderValue getRouteHeader(Long routeHeaderId, boolean clearCache) {
    	return getRouteHeaderDAO().findRouteHeader(routeHeaderId, clearCache);
    }

    public Collection<DocumentRouteHeaderValue> getRouteHeaders(Collection<Long> routeHeaderIds) {
    	return getRouteHeaderDAO().findRouteHeaders(routeHeaderIds);
    }
    
    public Collection<DocumentRouteHeaderValue> getRouteHeaders(Collection<Long> routeHeaderIds, boolean clearCache) {
    	return getRouteHeaderDAO().findRouteHeaders(routeHeaderIds, clearCache);
    }
    
    public Map<Long,DocumentRouteHeaderValue> getRouteHeadersForActionItems(Collection<ActionItem> actionItems) {
    	Map<Long,DocumentRouteHeaderValue> routeHeaders = new HashMap<Long,DocumentRouteHeaderValue>();
    	List<Long> routeHeaderIds = new ArrayList<Long>(actionItems.size());
    	for (ActionItem actionItem : actionItems) {
    		routeHeaderIds.add(actionItem.getRouteHeaderId());
    	}
    	Collection<DocumentRouteHeaderValue> actionItemRouteHeaders = getRouteHeaders(routeHeaderIds);
    	if (actionItemRouteHeaders != null) {
    		for (DocumentRouteHeaderValue routeHeader : actionItemRouteHeaders) {
    			routeHeaders.put(routeHeader.getRouteHeaderId(), routeHeader);
    		}
    	}
    	return routeHeaders;
    }
    
    public void lockRouteHeader(Long routeHeaderId, boolean wait) {
        getRouteHeaderDAO().lockRouteHeader(routeHeaderId, wait);
        LOG.debug("Successfully locked document [docId=" + routeHeaderId + "]");
    }

    public void saveRouteHeader(DocumentRouteHeaderValue routeHeader) {
        this.getRouteHeaderDAO().saveRouteHeader(routeHeader);
    }

    public void deleteRouteHeader(DocumentRouteHeaderValue routeHeader) {
        getRouteHeaderDAO().deleteRouteHeader(routeHeader);
    }

    public Long getNextRouteHeaderId() {
        return getRouteHeaderDAO().getNextRouteHeaderId();
    }

    public Collection findPendingByResponsibilityIds(Set responsibilityIds) {
        return getRouteHeaderDAO().findPendingByResponsibilityIds(responsibilityIds);
    }

    public void clearRouteHeaderSearchValues(Long routeHeaderId) {
        getRouteHeaderDAO().clearRouteHeaderSearchValues(routeHeaderId);
    }
    
    public void updateRouteHeaderSearchValues(Long routeHeaderId, List<SearchableAttributeValue> searchAttributes) {
    	getRouteHeaderDAO().clearRouteHeaderSearchValues(routeHeaderId);
    	for (SearchableAttributeValue searchAttribute : searchAttributes) {
    		getRouteHeaderDAO().save(searchAttribute);
    	}
    }

    public void validateRouteHeader(DocumentRouteHeaderValue routeHeader){
        LOG.debug("Enter validateRouteHeader(..)");
        List errors = new ArrayList();

        if (routeHeader.getDocRouteStatus() == null || routeHeader.getDocRouteStatus().trim().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route status null.", "routeheader.routestatus.empty"));
        } else if (!KEWConstants.DOCUMENT_STATUSES.containsKey(routeHeader.getDocRouteStatus())){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route status invalid.", "routeheader.routestatus.invalid"));
        }

        if(routeHeader.getDocRouteLevel() == null || routeHeader.getDocRouteLevel().intValue() < 0){
            errors.add(new WorkflowServiceErrorImpl("RouteHeader route level invalid.", "routeheader.routelevel.invalid"));
        }

        if(routeHeader.getStatusModDate() == null){
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
           	KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipal(routeHeader.getInitiatorWorkflowId());
            if(principal == null)
            {
               	errors.add(new WorkflowServiceErrorImpl("RouteHeader initiator id invalid.", "routeheader.initiator.invalid"));
            }
        }

        if(routeHeader.getDocumentTypeId() != null && routeHeader.getDocumentTypeId().intValue() != 0){
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

    public String getServiceNamespaceByDocumentId(Long documentId) {
    	return getRouteHeaderDAO().getServiceNamespaceByDocumentId(documentId);
    }

    public DocumentRouteHeaderValueContent getContent(Long routeHeaderId) {
    	if (routeHeaderId == null) {
    		return new DocumentRouteHeaderValueContent();
    	}
    	DocumentRouteHeaderValueContent content = getRouteHeaderDAO().getContent(routeHeaderId);
    	if (content == null) {
    		content = new DocumentRouteHeaderValueContent(routeHeaderId);
    	}
    	return content;
    }

    public boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue) {
	return getRouteHeaderDAO().hasSearchableAttributeValue(documentId, searchableAttributeKey, searchableAttributeValue);
    }

    public String getDocumentStatus(Long documentId) {
    	return getRouteHeaderDAO().getDocumentStatus(documentId);
    }
    
    public String getAppDocId(Long documentId) {
 	 	if (documentId == null) {
 	 		return null;
 	 	}
 	 	return getRouteHeaderDAO().getAppDocId(documentId);
    }

    public DocumentRouteHeaderDAO getRouteHeaderDAO() {
        return routeHeaderDAO;
    }

    public void setRouteHeaderDAO(DocumentRouteHeaderDAO routeHeaderDAO) {
        this.routeHeaderDAO = routeHeaderDAO;
    }

	public List<Timestamp> getSearchableAttributeDateTimeValuesByKey(
			Long documentId, String key) {
		return getSearchableAttributeDAO().getSearchableAttributeDateTimeValuesByKey(documentId, key);
	}

	public List<BigDecimal> getSearchableAttributeFloatValuesByKey(
			Long documentId, String key) {
		return getSearchableAttributeDAO().getSearchableAttributeFloatValuesByKey(documentId, key);
	}

	public List<Long> getSearchableAttributeLongValuesByKey(Long documentId,
			String key) {
		return getSearchableAttributeDAO().getSearchableAttributeLongValuesByKey(documentId, key);
	}

	public List<String> getSearchableAttributeStringValuesByKey(
			Long documentId, String key) {

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
}
