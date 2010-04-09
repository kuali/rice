/*
 * Copyright 2005-2009 The Kuali Foundation
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
package org.kuali.rice.kew.routeheader.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.database.platform.DatabasePlatform;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.exception.LockingException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;
import org.kuali.rice.kew.routeheader.dao.DocumentRouteHeaderDAO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;


public class DocumentRouteHeaderDAOJpaImpl implements DocumentRouteHeaderDAO {

	@PersistenceContext(unitName="kew-unit")
	private EntityManager entityManager;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentRouteHeaderDAOJpaImpl.class);

    
    /**
	 * @return the entityManager
	 */
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	/**
	 * @param entityManager the entityManager to set
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void saveRouteHeader(DocumentRouteHeaderValue routeHeader) {   	
    	DocumentRouteHeaderValueContent documentContent = routeHeader.getDocumentContent();    	
//    	List<SearchableAttributeValue> searchableAttributes = routeHeader.getSearchableAttributeValues();
    	
    	if (routeHeader.getRouteHeaderId() == null){
    		entityManager.persist(routeHeader);
    	} else {
    		OrmUtils.merge(entityManager, routeHeader);
    	}
        
        //Save document content (document content retrieved via a service call)
        documentContent.setRouteHeaderId(routeHeader.getRouteHeaderId());
        entityManager.merge(documentContent);
        
        /*
        //Save searchable attributes
        for (SearchableAttributeValue searchableAttributeValue:searchableAttributes){
        	searchableAttributeValue.setRouteHeaderId(routeHeader.getRouteHeaderId());
        	if (searchableAttributeValue.getSearchableAttributeValueId() == null){
        		entityManager.persist(searchableAttributeValue);
        	} else {
        		entityManager.merge(searchableAttributeValue);
        	}
        }
        */
    }

    public DocumentRouteHeaderValueContent getContent(Long routeHeaderId) {
    	Query query = entityManager.createNamedQuery("DocumentRouteHeaderValueContent.FindByRouteHeaderId");
    	query.setParameter("routeHeaderId", routeHeaderId);
        return (DocumentRouteHeaderValueContent)query.getSingleResult();
    }

    public void clearRouteHeaderSearchValues(Long routeHeaderId) {
    	List<SearchableAttributeValue> searchableAttributeValues = findSearchableAttributeValues(routeHeaderId);
    	for (SearchableAttributeValue searchableAttributeValue:searchableAttributeValues){
    		entityManager.remove(searchableAttributeValue);
    	}
    }
   
    private List<SearchableAttributeValue> findSearchableAttributeValues(Long routeHeaderId){
    	List<SearchableAttributeValue> searchableAttributeValues = new ArrayList<SearchableAttributeValue>();
    	
    	for (int i=1;i<=4; i++){
    		String namedQuery = "";
    		switch (i) {
				case 1: namedQuery = "SearchableAttributeFloatValue.FindByRouteHeaderId"; break;
				case 2: namedQuery = "SearchableAttributeDateTimeValue.FindByRouteHeaderId"; break;
				case 3: namedQuery = "SearchableAttributeLongValue.FindByRouteHeaderId";break;
				case 4: namedQuery = "SearchableAttributeStringValue.FindByRouteHeaderId"; break;
    		}
	    	Query query = entityManager.createNamedQuery(namedQuery);
	    	query.setParameter("routeHeaderId", routeHeaderId);   	
	    	searchableAttributeValues.addAll(query.getResultList());
    	}    	

    	return searchableAttributeValues;
    }

    public void lockRouteHeader(final Long routeHeaderId, final boolean wait) {
    	String sql = getPlatform().getLockRouteHeaderQuerySQL(routeHeaderId, wait);
    	try{
	    	Query query = entityManager.createNativeQuery(sql);
	    	query.setParameter(1, routeHeaderId);
	    	query.getSingleResult();
    	} catch (Exception e){
    		//FIXME: Should this check for hibernate LockAcquisitionException
    		throw new LockingException("Could not aquire lock on document, routeHeaderId=" + routeHeaderId, e);
    	}
    }

    public DocumentRouteHeaderValue findRouteHeader(Long routeHeaderId) {
    	return findRouteHeader(routeHeaderId, false);
    }

    public DocumentRouteHeaderValue findRouteHeader(Long routeHeaderId, boolean clearCache) {
        Query query = entityManager.createNamedQuery("DocumentRouteHeaderValue.FindByRouteHeaderId");
        query.setParameter("routeHeaderId", routeHeaderId);

        //TODO: What cache do we clear when using JPA
        if (clearCache) {
        	//this.getPersistenceBrokerTemplate().clearCache();
        }
        
	    DocumentRouteHeaderValue routeHeader = (DocumentRouteHeaderValue) query.getSingleResult(); 
//	    routeHeader.setSearchableAttributeValues(findSearchableAttributeValues(routeHeaderId));
	    return routeHeader;
    }

    public Collection<DocumentRouteHeaderValue> findRouteHeaders(Collection<Long> routeHeaderIds) {
    	return findRouteHeaders(routeHeaderIds, false);
    }
    
    public Collection<DocumentRouteHeaderValue> findRouteHeaders(Collection<Long> routeHeaderIds, boolean clearCache) {
    	if (routeHeaderIds == null || routeHeaderIds.isEmpty()) {
    		return null;
    	}
    	Criteria crit = new Criteria(DocumentRouteHeaderValue.class.getName());
    	crit.in("routeHeaderId", routeHeaderIds);
    	
    	//TODO: What cache do we clear when using JPA
        if (clearCache) {
        	//this.getPersistenceBrokerTemplate().clearCache();
        }
    	
    	return new QueryByCriteria(entityManager, crit).toQuery().getResultList();
    }
    
    public void deleteRouteHeader(DocumentRouteHeaderValue routeHeader) {
    	// need to clear action list cache for users who have this item in their action list
    	ActionListService actionListSrv = KEWServiceLocator.getActionListService();
    	Collection actionItems = actionListSrv.findByRouteHeaderId(routeHeader.getRouteHeaderId());
    	for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
    		ActionItem actionItem = (ActionItem) iter.next();
    		try {
    			KEWServiceLocator.getUserOptionsService().saveRefreshUserOption(actionItem.getPrincipalId());
    		} catch (Exception e) {
    			LOG.error("error saving refreshUserOption", e);
    		}
    	}
    	
    	DocumentRouteHeaderValue attachedRouteHeader = findRouteHeader(routeHeader.getRouteHeaderId());
    	entityManager.remove(attachedRouteHeader);
    }

    public Long getNextRouteHeaderId() {
        return getPlatform().getNextValSQL("KREW_DOC_HDR_S", entityManager);
    }

    protected DatabasePlatform getPlatform() {
    	return (DatabasePlatform)GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
    }

    public Collection findPendingByResponsibilityIds(Set responsibilityIds) {

        if (responsibilityIds.isEmpty()) {
            return new ArrayList();
        }

        String respIds = "(";
        int index = 0;
        for (Iterator iterator = responsibilityIds.iterator(); iterator.hasNext(); index++) {
            Long responsibilityId = (Long) iterator.next();
            respIds += responsibilityId + (index == responsibilityIds.size()-1 ? "" : ",");
        }
        respIds += ")";

        String query = "SELECT DISTINCT(doc_hdr_id) FROM KREW_ACTN_RQST_T "+
        	"WHERE (STAT_CD='" +
        	KEWConstants.ACTION_REQUEST_INITIALIZED+
        	"' OR STAT_CD='"+
        	KEWConstants.ACTION_REQUEST_ACTIVATED+
        	"') AND RSP_ID IN "+respIds;

        LOG.debug("Query to find pending documents for requeue: " + query);
        
        List<Long> idList = new ArrayList<Long>();
        for (Object tempId : entityManager.createNativeQuery(query).getResultList()) {
        	idList.add(((BigDecimal) tempId).longValueExact());
        }

        return idList; //(List<Long>)entityManager.createNativeQuery(query).getResultList();
    }

    public boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue) {
    	return hasSearchableAttributeValue(documentId, searchableAttributeKey, searchableAttributeValue, "SearchableAttributeDateTimeValue.FindByKey")
    		|| hasSearchableAttributeValue(documentId, searchableAttributeKey, searchableAttributeValue, "SearchableAttributeStringValue.FindByKey")
    		|| hasSearchableAttributeValue(documentId, searchableAttributeKey, searchableAttributeValue, "SearchableAttributeLongValue.FindByKey")
    		|| hasSearchableAttributeValue(documentId, searchableAttributeKey, searchableAttributeValue, "SearchableAttributeFloatValue.FindByKey");
    }
    
    private boolean hasSearchableAttributeValue(Long documentId, String searchableAttributeKey, String searchableAttributeValue, String namedQuery) {
    	Query query = entityManager.createNamedQuery(namedQuery);
        query.setParameter("routeHeaderId", documentId);
        query.setParameter("searchableAttributeKey", searchableAttributeKey);
        Collection results = query.getResultList();
        if (!results.isEmpty()) {
            for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                SearchableAttributeValue attribute = (SearchableAttributeValue) iterator.next();
                if (StringUtils.equals(attribute.getSearchableAttributeDisplayValue(), searchableAttributeValue)) {
                    return true;
                }
            }
        }
        return false;    	
    }

    public String getServiceNamespaceByDocumentId(Long documentId) {
    	if (documentId == null) {
    		throw new IllegalArgumentException("Encountered a null document ID.");
    	}
    	
    	String serviceNamespace = null;
    	
    	try {
            String sql = "SELECT DT.SVC_NMSPC FROM KREW_DOC_TYP_T DT, KREW_DOC_HDR_T DH "+
            	"WHERE DH.DOC_TYP_ID=DT.DOC_TYP_ID AND "+
            	"DH.DOC_HDR_ID=?";
        	
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, documentId);
            
            serviceNamespace = (String)query.getSingleResult();
    	} catch (EntityNotFoundException enfe) {
    		throw new WorkflowRuntimeException(enfe.getMessage());
		}
    	
    	return serviceNamespace;
    }

    public String getDocumentStatus(Long documentId) {
    	DocumentRouteHeaderValue document = findRouteHeader(documentId);

		return document.getDocRouteStatus();
    }
    
    public String getAppDocId(Long documentId) {
    	Query query = entityManager.createNamedQuery("DocumentRouteHeaderValue.GetAppDocId");
        query.setParameter("routeHeaderId", documentId);
        return (String) query.getSingleResult(); 
 	 }
    
    public void save(SearchableAttributeValue searchableAttributeValue) {   	
    	if (searchableAttributeValue.getSearchableAttributeValueId() == null){
    		entityManager.persist(searchableAttributeValue);
    	} else {
    		entityManager.merge(searchableAttributeValue);
    	}
    }

	public Collection findByDocTypeAndAppId(String documentTypeName,
			String appId) {
    	try {
            String sql = 
        	 	"SELECT DISTINCT " +
        		"    (docHdr.doc_hdr_id) " +
        		"FROM " +
        		"    KREW_DOC_HDR_T docHdr, " +
        		"    KREW_DOC_TYP_T docTyp " +
        		"WHERE " +
        		"    docHdr.APP_DOC_ID     = ? " +
        		"    AND docHdr.DOC_TYP_ID = docTyp.DOC_TYP_ID " +
        		"    AND docTyp.DOC_TYP_NM = ?";
        	
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, appId);
            query.setParameter(2, documentTypeName);
            Collection<Long> idCollection = new ArrayList<Long>();
            for (Object tempId : query.getResultList()) {
            	idCollection.add(((BigDecimal)tempId).longValueExact());
            }
            return idCollection;
    	} catch (EntityNotFoundException enfe) {
    		throw new WorkflowRuntimeException(enfe.getMessage());
		}
	}


}
