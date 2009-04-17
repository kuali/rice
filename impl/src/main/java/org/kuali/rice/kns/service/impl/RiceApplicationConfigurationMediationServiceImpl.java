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
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.RiceApplicationConfigurationMediationService;
import org.kuali.rice.kns.service.RiceApplicationConfigurationService;
import org.kuali.rice.ksb.messaging.RemoteResourceServiceLocator;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;

//@Transactional
public class RiceApplicationConfigurationMediationServiceImpl implements RiceApplicationConfigurationMediationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RiceApplicationConfigurationMediationServiceImpl.class);
    
    /**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.service.RiceApplicationConfigurationMediationService#getConfigurationParameter(java.lang.String)
	 */
//    public String getConfigurationParameter( String namespace, String parameterName ){
//    	String parameterValue = null;
//    	if ( namespace != null ) {
//			ServiceRegistry reg = KSBServiceLocator.getIPTableService();
//			
//			// TODO: Do we look up by namespace or QName?  Do the 
//			// RiceApplicationConfigurationServices need to be registered by
//			// that name, or should we not assume as much and just perform
//			// an instanceof instead?
//			
//			// also, we probably need to do some caching here!
//		
//			QName qname = new QName(namespace, KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE);
//			List<ServiceInfo> services = reg.fetchActiveByQName(qname);			
//			for ( ServiceInfo si : services ) {
//				qname = si.getQname();
//				// TODO: If we already had the QName in order to perform the
//				// database lookup, why didn't we just call this directly?
//				ServerSideRemotedServiceHolder h = KSBServiceLocator.getServiceDeployer().getRemotedServiceHolder(qname);
//				Object s = h.getService();
//				if ( s instanceof RiceApplicationConfigurationService ) {
//					RiceApplicationConfigurationService rac = (RiceApplicationConfigurationService)s;
//					parameterValue = rac.getConfigurationParameter(parameterName);
//				}
//			}
//		}
//    	return parameterValue;
//    }
    
    public String getConfigurationParameter( String namespace, String parameterName ){
    	
    	// TODO we will want to do some caching here!!!
    	
    	String parameterValue = null;
    	if ( namespace != null ) {
    		RiceApplicationConfigurationService rac = findRiceApplicationConfigurationService(namespace);
    		if (rac != null) {
    			parameterValue = rac.getConfigurationParameter(parameterName);
			}
		}
    	return parameterValue;
    }

    
//    public List<ParameterDetailType> getNonDatabaseComponents() {
//    	// TODO there is probably going to need to be some sort of caching here!
//		// TODO also, i think the code that's below here will actually pull in more than
//		// one reference to a particular application's config service if it is deployed
//		// in a cluster, it needs to only pull a single RiceApplicationConfigurationService
//		// implementation per service namespace.  Also, may want to consider load balancing
//		// and failover in those cases?  It might be best to try and utilize the client-side
//		// KSB proxies that handle a lot of this stuff for us
//    	
//    	RemoteResourceServiceLocator remoteResourceServiceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
//    	List<QName> serviceNames = remoteResourceServiceLocator.getServiceNamesForUnqualifiedName(KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE);
//		
//		List<ParameterDetailType> nonDatabaseComponents = new ArrayList<ParameterDetailType>();
//		ServiceRegistry registry = KSBServiceLocator.getIPTableService();
//		List<ServiceInfo> services = registry.fetchActiveByName(KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE);
//		for ( ServiceInfo serviceInfo : services ) {
//			ServerSideRemotedServiceHolder holder = KSBServiceLocator.getServiceDeployer().getRemotedServiceHolder(serviceInfo.getQname());
//			Object service = holder.getService();
//			if ( service instanceof RiceApplicationConfigurationService ) {
//				RiceApplicationConfigurationService rac = (RiceApplicationConfigurationService)service;
//				nonDatabaseComponents.addAll(rac.getNonDatabaseComponents());
//			}
//		}
//		return nonDatabaseComponents;
//    }
    
    public List<ParameterDetailType> getNonDatabaseComponents() {
    	// TODO there is probably going to need to be some sort of caching here!
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
    	return (RiceApplicationConfigurationService)GlobalResourceLoader.getService(serviceName);
    }
    
    private RiceApplicationConfigurationService findRiceApplicationConfigurationService(String namespace) {
    	return (RiceApplicationConfigurationService)GlobalResourceLoader.getService(new QName(namespace, KNSServiceLocator.RICE_APPLICATION_CONFIGURATION_SERVICE));
    }
    

}