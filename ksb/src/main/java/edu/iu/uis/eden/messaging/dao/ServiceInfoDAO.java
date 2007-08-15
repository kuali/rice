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
package edu.iu.uis.eden.messaging.dao;

import java.util.List;

import edu.iu.uis.eden.messaging.ServiceInfo;

public interface ServiceInfoDAO {

	public void addEntry(ServiceInfo entry);
	public void removeEntry(ServiceInfo entry);
	public List<ServiceInfo> fetchAll();
	public List<ServiceInfo> fetchAllActive();
	public List<ServiceInfo> findLocallyPublishedServices(String ipNumber, String messageEntity);
	public void removeLocallyPublishedServices(String ipNumber, String messageEntity);
	public ServiceInfo findServiceInfo(Long serviceInfoId);
}