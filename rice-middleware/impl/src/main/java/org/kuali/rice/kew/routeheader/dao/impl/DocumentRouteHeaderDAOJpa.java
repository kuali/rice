/**
 * Copyright 2005-2014 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;
import org.kuali.rice.kew.routeheader.dao.DocumentRouteHeaderDAO;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentRouteHeaderDAOJpa implements DocumentRouteHeaderDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentRouteHeaderDAOJpa.class);

    private static final String LOCK_TIMEOUT_HINT = "javax.persistence.lock.timeout";
    private static final Long DEFAULT_LOCK_TIMEOUT_SECONDS = Long.valueOf(60 * 60); // default to 1 hour

    public static final String GET_APP_DOC_ID_NAME = "DocumentRouteHeaderValue.GetAppDocId";
    public static final String GET_APP_DOC_ID_QUERY = "SELECT d.appDocId from DocumentRouteHeaderValue "
            + "as d where d.documentId = :documentId";
    public static final String GET_APP_DOC_STATUS_NAME = "DocumentRouteHeaderValue.GetAppDocStatus";
    public static final String GET_APP_DOC_STATUS_QUERY = "SELECT d.appDocStatus from "
            + "DocumentRouteHeaderValue as d where d.documentId = :documentId";
    public static final String GET_DOCUMENT_HEADERS_NAME = "DocumentRouteHeaderValue.GetDocumentHeaders";
    public static final String GET_DOCUMENT_HEADERS_QUERY = "SELECT d from DocumentRouteHeaderValue "
            + "as d where d.documentId IN :documentIds";
    public static final String GET_DOCUMENT_STATUS_NAME = "DocumentRouteHeaderValue.GetDocumentStatus";
    public static final String GET_DOCUMENT_STATUS_QUERY = "SELECT d.docRouteStatus from "
            + "DocumentRouteHeaderValue as d where d.documentId = :documentId";
    public static final String GET_DOCUMENT_ID_BY_DOC_TYPE_APP_ID_NAME =
            "DocumentRouteHeaderValue.GetDocumentIdByDocTypeAndAppId";
    public static final String GET_DOCUMENT_ID_BY_DOC_TYPE_APP_ID_QUERY = "SELECT "
            + "DISTINCT(DH.documentId) FROM DocumentRouteHeaderValue DH, DocumentType DT "
            + "WHERE DH.appDocId = :appDocId AND DH.documentTypeId = DT.documentTypeId  AND DT.name = :name";

	private EntityManager entityManager;
    private DataSource dataSource;

    private DataObjectService dataObjectService;

    @Override
    public Collection<DocumentRouteHeaderValue> findRouteHeaders(Collection<String> documentIds){
        if (documentIds.isEmpty()) {
            return new ArrayList<DocumentRouteHeaderValue>();
        }
        TypedQuery<DocumentRouteHeaderValue> query = getEntityManager().
                createNamedQuery(GET_DOCUMENT_HEADERS_NAME, DocumentRouteHeaderValue.class);
        query.setParameter("documentIds",documentIds);
        return query.getResultList();
    }

    @Override
    public Collection<DocumentRouteHeaderValue> findRouteHeaders(Collection<String> documentIds, boolean clearCache){
        Collection<DocumentRouteHeaderValue> documentRouteHeaderValues = findRouteHeaders(documentIds);
        if(clearCache){
            for(DocumentRouteHeaderValue drhv : documentRouteHeaderValues){
                getEntityManager().refresh(drhv);
            }

        }
        return documentRouteHeaderValues;
    }

    @Override
    public void lockRouteHeader(final String documentId) {
        // passing a hint here on the lock timeout, this will really only work on Oracle since it supports "wait"
        // on a SELECT ... FOR UPDATE but other databases don't
        //
        // one random additional piece of trivia to note, if the timeout comes back non-zero, then EclipseLink just
        // ingores it when sending it to MySQL and issues a plain SELECT ... FOR UPDATE. However if it cames back as 0,
        // it will try to issue a SELECT ... FOR UPDATE NOWAIT even thouh MySQL doesn't support it. This, of course,
        // triggers an exception from the MySQL database
        //
        // the moral of the story? don't ever set the timeout to zero, at least not until EclipseLink fixes that bug
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(LOCK_TIMEOUT_HINT, getTimeoutMilliseconds());
        getEntityManager().find(DocumentRouteHeaderValue.class, documentId, LockModeType.PESSIMISTIC_WRITE, options);
    }

    protected Long getTimeoutMilliseconds() {
        Long secondsToWait = DEFAULT_LOCK_TIMEOUT_SECONDS;
        String timeoutValue = ConfigContext.getCurrentContextConfig().getDocumentLockTimeout();
        if (timeoutValue != null) {
            try {
                secondsToWait = Long.parseLong(timeoutValue);
            } catch (NumberFormatException e) {
                LOG.warn("Failed to parse document lock timeout as it was not a valid number: " + timeoutValue);
            }
        }
        return secondsToWait * 1000;
    }

    @Override
    public DocumentRouteHeaderValue findRouteHeader(String documentId, boolean clearCache) {
        DocumentRouteHeaderValue dv = getDataObjectService().find(DocumentRouteHeaderValue.class,documentId);
        if(clearCache){
            getEntityManager().refresh(dv);
        }
        return dv;
    }

    @Override
    public String getNextDocumentId(){
        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(
                                        getDataSource(), "KREW_DOC_HDR_S");
        return incrementer.nextStringValue();
    }

    public Collection<String> findPendingByResponsibilityIds(Set<String> responsibilityIds) {
        List<String> documentIds = new ArrayList<String>();
        if (responsibilityIds.isEmpty()) {
            return documentIds;
        }
        TypedQuery<String> query =
                getEntityManager().createNamedQuery("ActionRequestValue.FindPendingByResponsibilityIds", String.class);
        query.setParameter("respIds", responsibilityIds);
        return query.getResultList();
    }

    public void clearRouteHeaderSearchValues(String documentId) {

        Query query = getEntityManager().
                createNamedQuery("SearchableAttributeValue.FindSearchableAttributesByDocumentId");
        query.setParameter("documentId",documentId);
        List<SearchableAttributeValue> searchableAttributeValues =
                (List<SearchableAttributeValue>)query.getResultList();
        for(SearchableAttributeValue sa : searchableAttributeValues){
            getDataObjectService().delete(sa);
        }
    }

    @Override
    public Collection<SearchableAttributeValue> findSearchableAttributeValues(String documentId) {
        Query query = getEntityManager().createNamedQuery(
                "SearchableAttributeValue.FindSearchableAttributesByDocumentId");
        query.setParameter("documentId",documentId);
        return query.getResultList();
    }

    @Override
    public DocumentRouteHeaderValueContent getContent(String documentId) {
        DocumentRouteHeaderValueContent content = null;
        Query query = getEntityManager().createNamedQuery("DocumentRouteHeaderValueContent.FindByDocumentId");
        query.setParameter("documentId",documentId);
        if(query.getResultList() != null && !query.getResultList().isEmpty()) {
          content = (DocumentRouteHeaderValueContent)query.getResultList().get(0);
        }
        return content;
    }

    @Override
    public boolean hasSearchableAttributeValue(String documentId, String searchableAttributeKey, String searchableAttributeValue) {
        Query query = getEntityManager().createNamedQuery("SearchableAttributeValue.HasSearchableAttributeValue");
        query.setParameter("documentId",documentId);
        query.setParameter("searchableAttributeKey",searchableAttributeKey);

        if(query.getResultList() != null && !query.getResultList().isEmpty()){
            for(Object ob : query.getResultList()){
                SearchableAttributeValue sav = (SearchableAttributeValue)ob;
                if (StringUtils.equals(sav.getSearchableAttributeDisplayValue(), searchableAttributeValue)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getDocumentStatus(String documentId) {
        String status = null;

        Query query = getEntityManager().createNamedQuery(GET_DOCUMENT_STATUS_NAME);
        query.setParameter("documentId",documentId);
        if(query.getResultList() != null && !query.getResultList().isEmpty()){
            status = (String)query.getResultList().get(0);
        }
        return status;
    }

    @Override
    public void save(SearchableAttributeValue searchableAttribute) {
        getDataObjectService().save(searchableAttribute);
    }

    public String getAppDocId(String documentId) {
        TypedQuery<String> query = getEntityManager().createNamedQuery(GET_APP_DOC_ID_NAME,String.class
        );
        query.setParameter("documentId",documentId);

        String applicationDocId = null;
        if(query.getResultList() != null && !query.getResultList().isEmpty()){
            applicationDocId = query.getResultList().get(0);
        }
        return applicationDocId;

    }

    public String getApplicationIdByDocumentId(String documentId) {
        if (documentId == null) {
            throw new IllegalArgumentException("Encountered a null document ID.");
        }

        String applicationId = null;

        TypedQuery<String> query = getEntityManager().createNamedQuery(
                "DocumentType.GetAppIdByDocumentId",String.class);
        query.setParameter("documentId",documentId);
        if(query.getResultList() != null && !query.getResultList().isEmpty()){
             applicationId = query.getResultList().get(0);
        }
        return applicationId;

    }

    public String getAppDocStatus(String documentId) {
        String applicationDocumentStatus = null;

        TypedQuery<String> query = getEntityManager().createNamedQuery(GET_APP_DOC_STATUS_NAME,String.class);
        query.setParameter("documentId",documentId);
        if(query.getResultList() != null && !query.getResultList().isEmpty()){
            applicationDocumentStatus = query.getResultList().get(0);
        }
        return applicationDocumentStatus;
    }

    public Collection findByDocTypeAndAppId(String documentTypeName,
            String appId) {
        TypedQuery<String> query = getEntityManager().createNamedQuery(GET_DOCUMENT_ID_BY_DOC_TYPE_APP_ID_NAME,
                String.class);
        query.setParameter("appDocId",appId);
        query.setParameter("name",documentTypeName);
        return query.getResultList();
    }

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

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataObjectService getDataObjectService() {
        return dataObjectService;
    }

    @Required
    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
