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
package org.kuali.rice.ksb.messaging.dao;

import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.ksb.messaging.FlattenedServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;

public interface ServiceInfoDAO {

	public void addEntry(ServiceInfo entry);
	public void removeEntry(ServiceInfo entry);
	public List<ServiceInfo> fetchAll();
	public List<ServiceInfo> fetchAllActive();
	public List<ServiceInfo> fetchActiveByName(QName serviceName);
	public List<ServiceInfo> fetchActiveByNamespace(String serviceNamespace);
	public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String serviceNamespace);
	public void removeLocallyPublishedServices(String ipNumber, String serviceNamespace);
	public ServiceInfo findServiceInfo(Long serviceInfoId);
	public FlattenedServiceDefinition findFlattenedServiceDefinition(Long flattenedServiceDefinitionId);
}
