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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.dao.ServiceInfoDAO;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class ServiceInfoDAOJpaImpl extends PersistenceBrokerDaoSupport implements ServiceInfoDAO {
	
	@PersistenceContext
	EntityManager entityManager;
	
    public void addEntry(ServiceInfo entry) {
        if(entry.getMessageEntryId() == null) {
            entityManager.persist(entry);
        }
        else {
            entityManager.merge(entry);
        }
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInfo> fetchAll() {
    	Query query = entityManager.createNamedQuery("ServiceInfo.FetchAll");
    	return (List<ServiceInfo>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInfo> fetchAllActive() {
    	Query query = entityManager.createNamedQuery("ServiceInfo.FetchAllActive");
    	return (List<ServiceInfo>) query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String serviceNamespace) {
    	Query query = entityManager.createNamedQuery("ServiceInfo.FindLocallyPublishedServices");
    	query.setParameter("serverIp", ipNumber);
    	query.setParameter("serviceNamespace", serviceNamespace);
    	return (List<ServiceInfo>) query.getResultList();
    }

    public void removeEntry(ServiceInfo entry) {
    	Query query = entityManager.createNamedQuery("ServiceInfo.DeleteByEntry");
    	query.setParameter("messageEntryId", entry.getMessageEntryId());
		query.executeUpdate();
    }

    public ServiceInfo findServiceInfo(Long serviceInfoId) {
    	return (ServiceInfo) entityManager.find(ServiceInfo.class, serviceInfoId);
    }

    public void removeLocallyPublishedServices(String ipNumber, String serviceNamespace) {
    	Query query = entityManager.createNamedQuery("ServiceInfo.DeleteLocallyPublishedServices");
    	query.setParameter("serverIp", ipNumber);
    	query.setParameter("serviceNamespace", serviceNamespace);
		query.executeUpdate();
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
