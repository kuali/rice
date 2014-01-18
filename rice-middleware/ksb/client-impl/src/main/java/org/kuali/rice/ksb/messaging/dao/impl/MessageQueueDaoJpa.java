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
package org.kuali.rice.ksb.messaging.dao.impl;

import org.kuali.rice.core.api.config.CoreConfigHelper;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.util.RiceUtilities;
import org.kuali.rice.ksb.messaging.PersistedMessageBO;
import org.kuali.rice.ksb.messaging.PersistedMessagePayload;
import org.kuali.rice.ksb.messaging.dao.MessageQueueDAO;
import org.kuali.rice.ksb.util.KSBConstants;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

public class MessageQueueDaoJpa implements MessageQueueDAO {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MessageQueueDaoJpa.class);

    private EntityManager entityManager;

    public List<PersistedMessageBO> findAll() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Returning all persisted messages");
        }

        Query query = entityManager.createNamedQuery("PersistedMessageBO.FindAll");
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PersistedMessageBO> findAll(int maxRows) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Finding next " + maxRows + " messages");
        }

        TypedQuery<PersistedMessageBO> query = entityManager.createNamedQuery("PersistedMessageBO.FindAll",
                PersistedMessageBO.class);
        query.setMaxResults(maxRows);

        return query.getResultList();
    }

    public PersistedMessagePayload findByPersistedMessageByRouteQueueId(Long routeQueueId) {
        return entityManager.find(PersistedMessagePayload.class, routeQueueId);
    }

    public PersistedMessageBO findByRouteQueueId(Long routeQueueId) {
        return entityManager.find(PersistedMessageBO.class, routeQueueId);
    }

    public List<PersistedMessageBO> findByServiceName(QName serviceName, String methodName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Finding messages for service name " + serviceName);
        }

        TypedQuery<PersistedMessageBO> query = entityManager.createNamedQuery("PersistedMessageBO.FindByServiceName",
                PersistedMessageBO.class);
        query.setParameter("serviceName", serviceName.toString());
        query.setParameter("methodName", methodName);

        return query.getResultList();
    }

    public List<PersistedMessageBO> findByValues(Map<String, String> criteriaValues, int maxRows) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PersistedMessageBO> query = builder.createQuery(PersistedMessageBO.class);
        Root<PersistedMessageBO> message = query.from(PersistedMessageBO.class);
        Predicate predicate = builder.conjunction();
        for (Map.Entry<String, String> entry : criteriaValues.entrySet()) {
            predicate = builder.and(predicate, builder.equal(message.get(entry.getKey()), entry.getValue()));
        }
        query.where(predicate);
        TypedQuery<PersistedMessageBO> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    public List<PersistedMessageBO> getNextDocuments(Integer maxDocuments) {
        String applicationId = CoreConfigHelper.getApplicationId();

        TypedQuery<PersistedMessageBO> query = entityManager.createNamedQuery("PersistedMessageBO.GetNextDocuments",
                PersistedMessageBO.class);
        query.setParameter("applicationId", applicationId);
        query.setParameter("queueStatus", KSBConstants.ROUTE_QUEUE_EXCEPTION);
        query.setParameter("ipNumber", RiceUtilities.getIpNumber());

        if (maxDocuments != null) {
            query.setMaxResults(maxDocuments);
        }

        return query.getResultList();
    }

    public void remove(PersistedMessageBO routeQueue) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Removing message " + routeQueue);
        }
        if (routeQueue.getRouteQueueId() == null) {
            throw new RiceRuntimeException("can't delete a PersistedMessageBO with no id");
        }

        routeQueue = entityManager.merge(routeQueue);
        entityManager.remove(routeQueue);

        if (routeQueue.getPayload() != null) {
            PersistedMessagePayload payload = entityManager.merge(routeQueue.getPayload());
            entityManager.remove(payload);
        }
    }

    public PersistedMessageBO save(PersistedMessageBO routeQueue) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Persisting message " + routeQueue);
        }
        routeQueue = entityManager.merge(routeQueue);
        entityManager.flush();
        PersistedMessagePayload payload = routeQueue.getPayload();
        if (payload != null) {
            payload.setRouteQueueId(routeQueue.getRouteQueueId());
            payload = entityManager.merge(payload);
            entityManager.flush();
            routeQueue.setPayload(payload);
        }
        return routeQueue;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
