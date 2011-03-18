/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.namespace.QName;

import org.kuali.rice.ksb.messaging.FlattenedServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.dao.ServiceInfoDAO;


public class ServiceInfoDAOJpaImpl implements ServiceInfoDAO {
	
	@PersistenceContext
	EntityManager entityManager;
	
    public void addEntry(ServiceInfo entry) {
        if(entry.getMessageEntryId() == null) {
        	entityManager.persist(entry.getSerializedServiceNamespace());
        	entry.setFlattenedServiceDefinitionId(entry.getSerializedServiceNamespace().getFlattenedServiceDefinitionId());
            entityManager.persist(entry);
        }
        else {
        	entityManager.merge(entry.getSerializedServiceNamespace());
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
    public List<ServiceInfo> fetchActiveByNameLocalPart(String localPart) {
    	Query query = entityManager.createNamedQuery("ServiceInfo.FetchActiveByName");
    	query.setParameter("serviceName", "{%}" + localPart);
    	return (List<ServiceInfo>) query.getResultList();    	
    }
    
    @SuppressWarnings("unchecked")
    public List<ServiceInfo> fetchActiveByName(QName serviceName) {
    	Query query = entityManager.createNamedQuery("ServiceInfo.FetchActiveByName");
    	query.setParameter("serviceName", serviceName.toString());
    	return (List<ServiceInfo>) query.getResultList();    	
    }
    
    @SuppressWarnings("unchecked")
    public List<ServiceInfo> fetchActiveByNamespace(String serviceNamespace) {
    	Query query = entityManager.createNamedQuery("ServiceInfo.FetchActiveByName");
    	query.setParameter("serviceNamespace", "{"+serviceNamespace+"}%");
    	return (List<ServiceInfo>) query.getResultList();    	
    }
    
    @SuppressWarnings("unchecked")
    public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String serviceNamespace) {
    	Query query = entityManager.createNamedQuery("ServiceInfo.FindLocallyPublishedServices");
    	query.setParameter("serverIp", ipNumber);
        if (serviceNamespace != null) {
    	    query.setParameter("serviceNamespace", serviceNamespace);
        }
    	return (List<ServiceInfo>) query.getResultList();
    }

    public void removeEntry(ServiceInfo entry) {
    	//Query query = entityManager.createNamedQuery("ServiceInfo.DeleteByEntry");
    	//query.setParameter("messageEntryId", entry.getMessageEntryId());
		//query.executeUpdate();
    	//entityManager.remove(entityManager.getReference(ServiceInfo.class, entry.getMessageEntryId()));
    	entityManager.remove(entityManager.find(FlattenedServiceDefinition.class, entry.getFlattenedServiceDefinitionId()));
    	entityManager.remove(entityManager.find(ServiceInfo.class, entry.getMessageEntryId()));
    }

    public ServiceInfo findServiceInfo(Long serviceInfoId) {
    	return entityManager.find(ServiceInfo.class, serviceInfoId);
    }

    public void removeLocallyPublishedServices(String ipNumber, String serviceNamespace) {
    	List<ServiceInfo> localServices = findLocallyPublishedServices(ipNumber, serviceNamespace);
    	for (ServiceInfo localService : localServices) {
    		entityManager.remove(entityManager.find(FlattenedServiceDefinition.class, localService.getFlattenedServiceDefinitionId()));
    		entityManager.remove(entityManager.find(ServiceInfo.class, localService.getMessageEntryId()));
    	}
    }

	public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

	public FlattenedServiceDefinition findFlattenedServiceDefinition(Long flattenedServiceDefinitionId) {
		return entityManager.find(FlattenedServiceDefinition.class, flattenedServiceDefinitionId);
	}
}
