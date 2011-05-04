/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.ksb.messaging.service.impl;

import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.framework.persistence.ojb.DataAccessUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.ksb.messaging.FlattenedServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.dao.ServiceInfoDAO;
import org.kuali.rice.ksb.messaging.service.ServiceRegistry;
import org.kuali.rice.ksb.service.KSBContextServiceLocator;
import org.kuali.rice.ksb.service.KSBServiceLocator;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;


public class ServiceRegistryImpl implements ServiceRegistry {

	private ServiceInfoDAO dao;
	protected KSBContextServiceLocator serviceLocator;
	
	/**
	 * @return the serviceLocator
	 */
	public KSBContextServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	/**
	 * @param serviceLocator the serviceLocator to set
	 */
	public void setServiceLocator(KSBContextServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}

	public void saveEntry(ServiceInfo entry) {
		try {
			if (ObjectUtils.isNull(entry.getSerializedServiceNamespace())) {
				entry.setSerializedServiceNamespace(new FlattenedServiceDefinition(serviceLocator==null ?
						KSBServiceLocator.getMessageHelper().serializeObject(entry.getServiceDefinition()) :
								serviceLocator.getMessageHelper().serializeObject(entry.getServiceDefinition())));
			} else {
				entry.getSerializedServiceNamespace().setFlattenedServiceDefinitionData(serviceLocator==null ?
						KSBServiceLocator.getMessageHelper().serializeObject(entry.getServiceDefinition()) :
								serviceLocator.getMessageHelper().serializeObject(entry.getServiceDefinition()));
			}
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		}
		getDao().addEntry(entry);
	}

	public List<ServiceInfo> fetchAll() {
		return getDao().fetchAll();
	}

	public List<ServiceInfo> fetchAllActive() {
	    return dao.fetchAllActive();
	}
	
	public List<ServiceInfo> fetchActiveByName(QName serviceName) {
		return getDao().fetchActiveByName(serviceName);		
	}
	
	public List<ServiceInfo> fetchActiveByNamespace(String serviceNamespace) {
		return getDao().fetchActiveByNamespace(serviceNamespace);
	}
	
	public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String serviceNamespace) {
		if (ConfigContext.getCurrentContextConfig().getDevMode()) {
			return new ArrayList<ServiceInfo>();
		}
		return getDao().findLocallyPublishedServices(ipNumber, serviceNamespace);
	}

	public void removeEntry(ServiceInfo entry) {
		getDao().removeEntry(entry);
	}

	public void removeLocallyPublishedServices(String ipNumber, String serviceNamespace) {
		getDao().removeLocallyPublishedServices(ipNumber, serviceNamespace);
	}

	public ServiceInfoDAO getDao() {
		return this.dao;
	}

	public void setDao(ServiceInfoDAO dao) {
		this.dao = dao;
	}

	public void removeEntries(List<ServiceInfo> serviceEntries) {
		for (ServiceInfo info : serviceEntries) {
			removeEntry(info);
		}
	}

	public void saveEntries(List<ServiceInfo> serviceEntries) {
		for (ServiceInfo info : serviceEntries) {
			saveEntry(info);
		}
	}

	public void markServicesDead(List<ServiceInfo> serviceEntries) {
		for (ServiceInfo info : serviceEntries) {
			// there is contention on these records from multiple nodes and odds
			// are the
			// one we have in memory is stale. refetch and mork dead.
			ServiceInfo currentInfo = getDao().findServiceInfo(info.getMessageEntryId());
			currentInfo.setAlive(false);
			try {
				saveEntry(currentInfo);
			} catch (Exception e) {
				boolean isOptimisticLockExp = DataAccessUtils.isOptimisticLockFailure(e);
				// suppress optimistic lock exceptions, it's collision with
				// other nodes
				if (!isOptimisticLockExp) {
					throw (RuntimeException) e;
				}
			}

		}
	}
	
	public FlattenedServiceDefinition getFlattenedServiceDefinition(Long flattenedServiceDefinitionId) {
		return getDao().findFlattenedServiceDefinition(flattenedServiceDefinitionId);
	}
}
