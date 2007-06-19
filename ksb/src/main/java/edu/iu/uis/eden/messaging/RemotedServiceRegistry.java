/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging;

import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.rice.resourceloader.ServiceLocator;


/**
 * A registry of remoted services.
 *
 * @see ServiceDefinition
 * 
 * @author rkirkend
 */
public interface RemotedServiceRegistry extends ServiceLocator {
	
	public static final String FORWARD_HANDLER_SUFFIX = "-forwardHandler";

	public Object getService(QName qName, String url);
	public void removeRemoteServiceFromRegistry(QName serviceName);
	public void registerService(ServiceDefinition serviceDefinition, boolean forceRegistryRefresh);
	public void registerTempService(ServiceDefinition serviceDefinition, Object service);
	public ServerSideRemotedServiceHolder getRemotedServiceHolder(QName qname);
	public Object getLocalService(QName serviceName);
	public Map<QName, ServerSideRemotedServiceHolder> getPublishedServices();
	public Map<QName, ServerSideRemotedServiceHolder> getPublishedTempServices();
}
