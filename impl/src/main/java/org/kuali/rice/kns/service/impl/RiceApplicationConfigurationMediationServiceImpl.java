/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kns.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kns.bo.ParameterDetailType;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.NamespaceService;
import org.kuali.rice.kns.service.RiceApplicationConfigurationMediationService;
import org.kuali.rice.kns.service.RiceApplicationConfigurationService;
import org.kuali.rice.ksb.messaging.RemoteResourceServiceLocator;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;

//@Transactional
public class RiceApplicationConfigurationMediationServiceImpl implements RiceApplicationConfigurationMediationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RiceApplicationConfigurationMediationServiceImpl.class);
        
    public String getConfigurationParameter( String namespaceCode, String parameterName ){
    	
    	// TODO we will want to do some caching here!!!
    	
    	String parameterValue = null;
    	if ( namespaceCode != null ) {
			NamespaceService nsService = KNSServiceLocator.getNamespaceService();
			String applicationNamespaceCode = nsService.getNamespace(namespaceCode).getApplicationNamespaceCode();
			if (applicationNamespaceCode != null) {
				RiceApplicationConfigurationService rac = findRiceApplicationConfigurationService(applicationNamespaceCode);
				if (rac != null) {
					parameterValue = rac.getConfigurationParameter(parameterName);
				}
			}
		}
    	return parameterValue;
    }
    
    public List<ParameterDetailType> getNonDatabaseComponents() {
    	
    	// TODO there is going to need to be some sort of caching here!
    	
		// TODO also, i think the code that's below here will actually pull in more than
		// one reference to a particular application's config service if it is deployed
		// in a cluster, it needs to only pull a single RiceApplicationConfigurationService
		// implementation per service namespace.  Also, may want to consider load balancing
		// and failover in those cases?  It might be best to try and utilize the client-side
		// KSB proxies that handle a lot of this stuff for us
    	
    	RemoteResourceServiceLocator remoteResourceServiceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
    	List<QName> serviceNames = remoteResourceServiceLocator.getServiceNamesForUnqualifiedName(KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE);
		
		List<ParameterDetailType> nonDatabaseComponents = new ArrayList<ParameterDetailType>();
		for ( QName serviceName : serviceNames ) {
			RiceApplicationConfigurationService rac = findRiceApplicationConfigurationService(serviceName);
			if (rac != null) {
				nonDatabaseComponents.addAll(rac.getNonDatabaseComponents());
			}
		}
		return nonDatabaseComponents;
    }
    
    private RiceApplicationConfigurationService findRiceApplicationConfigurationService(QName serviceName) {
    	try {
    		return (RiceApplicationConfigurationService)GlobalResourceLoader.getService(serviceName);
    	} catch (Exception e) {
    		// if the service doesn't exist an exception is thrown
    		LOG.warn("Failed to locate RiceApplicationConfigurationService with name: " + serviceName);
    	}
    	return null;
    }
    
    private RiceApplicationConfigurationService findRiceApplicationConfigurationService(String namespace) {
    	try {
    		return (RiceApplicationConfigurationService)GlobalResourceLoader.getService(new QName(namespace, KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE));
    	} catch (Exception e) {
    		// if the service doesn't exist an exception is thrown
    		LOG.warn("Failed to locate RiceApplicationConfigurationService with namespace: " + namespace);
    	}
    	return null;
    }
    

}