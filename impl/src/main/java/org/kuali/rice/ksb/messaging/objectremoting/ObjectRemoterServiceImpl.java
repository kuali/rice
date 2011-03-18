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
package org.kuali.rice.ksb.messaging.objectremoting;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.messaging.JavaServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.config.ServiceBasedServiceDefinitionRegisterer;
import org.kuali.rice.ksb.service.KSBServiceLocator;


public class ObjectRemoterServiceImpl extends BaseLifecycle implements ObjectRemoterService {

	private static final Logger LOG = Logger.getLogger(ObjectRemoterServiceImpl.class);

	private ServiceBasedServiceDefinitionRegisterer defRegisterer;
	private static long counter = 0;

	public ServiceInfo getRemotedClassURL(ObjectDefinition objectDefinition) {
		LOG.debug("Looking for object " + objectDefinition.getClassName());
		objectDefinition.setAtRemotingLayer(true);
		Object target = GlobalResourceLoader.getResourceLoader().getObject(objectDefinition);
		//make sure that we have the class
		if (target == null) {
			LOG.debug("Didn't find object " + objectDefinition);
			return null;
		}

		LOG.debug("Found object " + objectDefinition);
		JavaServiceDefinition serviceDefinition = new JavaServiceDefinition();
		serviceDefinition.setLocalServiceName(objectDefinition.getClassName() + counter++);
		serviceDefinition.setService(target);
		serviceDefinition.validate();
		
		KSBServiceLocator.getServiceDeployer().registerTempService(serviceDefinition, target);
        serviceDefinition.setService(null);
		return new ServiceInfo(serviceDefinition);
	}
	
	public void removeService(QName serviceName) {
		LOG.debug("Removing service " + serviceName + " from service namespace" + ConfigContext.getCurrentContextConfig().getServiceNamespace());
		KSBServiceLocator.getServiceDeployer().removeRemoteServiceFromRegistry(serviceName);
	}

	public void start() throws Exception {
	    this.defRegisterer = new ServiceBasedServiceDefinitionRegisterer("ksb.objectRemoterServiceDefinition");
	    this.defRegisterer.registerServiceDefinition(false);
		setStarted(true);
	}

	public void stop() throws Exception {
	    this.defRegisterer.unregisterServiceDefinition();
		setStarted(false);
	}
}
