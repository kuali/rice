/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.dao.impl;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.namespace.QName;

import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.jpa.criteria.Criteria;
import org.kuali.rice.core.jpa.criteria.QueryByCriteria;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.ksb.messaging.PersistedMessage;
import org.kuali.rice.ksb.messaging.PersistedMessagePayload;
import org.kuali.rice.ksb.messaging.dao.MessageQueueDAO;
import org.kuali.rice.ksb.util.KSBConstants;


public class MessageQueueDAOJpaImpl implements MessageQueueDAO {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MessageQueueDAOJpaImpl.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
    public List<PersistedMessage> findAll() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning all persisted messages");
        }
        
        Query query = entityManager.createNamedQuery("PersistedMessage.FindAll");
        return (List<PersistedMessage>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PersistedMessage> findAll(int maxRows) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Finding next " + maxRows + " messages");
        }
        
        Query query = entityManager.createNamedQuery("PersistedMessage.FindAll");
        query.setMaxResults(maxRows);
        
        return (List<PersistedMessage>) query.getResultList();
    }

    
    public PersistedMessagePayload findByPersistedMessageByRouteQueueId(Long routeQueueId) {
        return (PersistedMessagePayload) entityManager.find(PersistedMessagePayload.class, routeQueueId);
    }

    
    public PersistedMessage findByRouteQueueId(Long routeQueueId) {
        return (PersistedMessage) entityManager.find(PersistedMessage.class, routeQueueId);
    }

    @SuppressWarnings("unchecked")
    public List<PersistedMessage> findByServiceName(QName serviceName, String methodName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Finding messages for service name " + serviceName);
        }
        
        Query query = entityManager.createNamedQuery("PersistedMessage.FindByServiceName");
        query.setParameter("serviceName", serviceName);
        query.setParameter("methodName", methodName);
        
        return (List<PersistedMessage>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PersistedMessage> findByValues(Map<String, String> criteriaValues, int maxRows) {
        Criteria criteria = new Criteria(PersistedMessage.class.getName());
        for(Map.Entry<String, String> entry : criteriaValues.entrySet()) {
            criteria.eq(entry.getKey(), entry.getValue());
        }
        
        QueryByCriteria query = new QueryByCriteria(entityManager, criteria);
        
        return query.toQuery().getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PersistedMessage> getNextDocuments(Integer maxDocuments) {
        String serviceNamespace = ConfigContext.getCurrentContextConfig().getServiceNamespace();
        
        Query query = entityManager.createNamedQuery("PersistedMessage.GetNextDocuments");
        query.setParameter("serviceNamespace", serviceNamespace);
        query.setParameter("queueStatus", KSBConstants.ROUTE_QUEUE_EXCEPTION);
        query.setParameter("ipNumber", RiceUtilities.getIpNumber());
        
        if (maxDocuments != null)
            query.setMaxResults(maxDocuments);
        
        return (List<PersistedMessage>) query.getResultList();
    }

    
    public void remove(PersistedMessage routeQueue) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing message " + routeQueue);
        }
        
        PersistedMessagePayload persistedMessagePayload = (PersistedMessagePayload) entityManager.find(PersistedMessagePayload.class, routeQueue.getRouteQueueId());
        PersistedMessage persistedMessage = (PersistedMessage) entityManager.find(PersistedMessage.class, routeQueue.getRouteQueueId());
        
        if(persistedMessagePayload != null) {
            entityManager.remove(persistedMessagePayload);
        }
        
        if(persistedMessage != null) {
            entityManager.remove(persistedMessage);
        }
    }

    
    public void save(PersistedMessage routeQueue) {
        if(routeQueue.getRouteQueueId() == null) {
            entityManager.persist(routeQueue);
            
            routeQueue.getPayload().setRouteQueueId(routeQueue.getRouteQueueId());
            entityManager.persist(routeQueue.getPayload());
        }
        else {
            entityManager.merge(routeQueue);
            entityManager.merge(routeQueue.getPayload());
        }
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
