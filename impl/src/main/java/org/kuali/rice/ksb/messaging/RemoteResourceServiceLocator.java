/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.ksb.messaging;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.ksb.messaging.exceptionhandling.MessageExceptionHandler;


/**
 * A {@link ResourceLoader} which locates endpoints to remoted services.
 *
 * @see ResourceLoader
 * @see RemotedServiceHolder
 * @see ServiceInfo
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RemoteResourceServiceLocator extends ResourceLoader {

	public List<RemotedServiceHolder> getAllServices(QName qName);
	public Map<QName, List<RemotedServiceHolder>> getClients();
	public List<QName> getServiceNamesForUnqualifiedName(String unqualifiedServiceName);
	public void removeService(ServiceInfo serviceInfo);
	public Object getService(QName qName, String url);
	public MessageExceptionHandler getMessageExceptionHandler(QName qName);
	public void refresh();

}
