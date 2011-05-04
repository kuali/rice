/*
 * Copyright 2007 The Kuali Foundation
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

import javax.xml.namespace.QName;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.ksb.messaging.FlattenedServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.dao.ServiceInfoDAO;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;



public class ServiceInfoDAOOjbImpl extends PersistenceBrokerDaoSupport implements ServiceInfoDAO {

    public void addEntry(ServiceInfo entry) {
    	if (ObjectUtils.isNotNull(entry.getSerializedServiceNamespace())) {
    		getPersistenceBrokerTemplate().store(entry.getSerializedServiceNamespace());
    	}
    	getPersistenceBrokerTemplate().store(entry);
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInfo> fetchAll() {
    	return (List<ServiceInfo>) getPersistenceBrokerTemplate().getCollectionByQuery(
    			new QueryByCriteria(ServiceInfo.class));
    }

    @SuppressWarnings("unchecked")
    public List<ServiceInfo> fetchAllActive() {
		Criteria crit = new Criteria();
		crit.addEqualTo("alive", Boolean.TRUE);
		return (List<ServiceInfo>) getPersistenceBrokerTemplate().getCollectionByQuery(
				new QueryByCriteria(ServiceInfo.class, crit));
    }
    
    @SuppressWarnings("unchecked")
    public List<ServiceInfo> fetchActiveByName(QName serviceName) {
		Criteria crit = new Criteria();
		crit.addEqualTo("alive", Boolean.TRUE);
		crit.addEqualTo("serviceName", serviceName.toString());
		return (List<ServiceInfo>) getPersistenceBrokerTemplate().getCollectionByQuery(
				new QueryByCriteria(ServiceInfo.class, crit));
    }
        
    @SuppressWarnings("unchecked")
    public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String serviceNamespace) {
    	Criteria crit = new Criteria();
    	crit.addEqualTo("serverIp", ipNumber);
        if (serviceNamespace != null) {
    	    crit.addEqualTo("serviceNamespace", serviceNamespace);
        }
    	return (List<ServiceInfo>) getPersistenceBrokerTemplate().getCollectionByQuery(
    			new QueryByCriteria(ServiceInfo.class, crit));
    }

    public void removeEntry(ServiceInfo entry) {
    	getPersistenceBrokerTemplate().delete(entry);
    	/*Criteria crit = new Criteria();
		crit.addEqualTo("messageEntryId", entry.getMessageEntryId());
		getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(ServiceInfo.class, crit));*/
    }

    public ServiceInfo findServiceInfo(Long serviceInfoId) {
    	return (ServiceInfo) getPersistenceBrokerTemplate().getObjectById(ServiceInfo.class, serviceInfoId);
    }

	public FlattenedServiceDefinition findFlattenedServiceDefinition(Long flattenedServiceDefinitionId) {
		Criteria crit = new Criteria();
    	crit.addEqualTo("flattenedServiceDefinitionId", flattenedServiceDefinitionId);
    	return (FlattenedServiceDefinition) getPersistenceBrokerTemplate().getObjectByQuery(
    			new QueryByCriteria(FlattenedServiceDefinition.class, crit));

	}

    public void removeLocallyPublishedServices(String ipNumber, String serviceNamespace) {
    	List<ServiceInfo> localServices = findLocallyPublishedServices(ipNumber, serviceNamespace);
    	for (ServiceInfo serviceInfo : localServices) {
    		getPersistenceBrokerTemplate().delete(serviceInfo);
    	}
    }

}
