/*
 * Copyright 2005-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.service.impl;

import com.google.common.collect.MapMaker;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.component.Component;
import org.kuali.rice.core.api.namespace.Namespace;
import org.kuali.rice.core.api.namespace.NamespaceService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.service.KRADServiceLocatorInternal;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.RiceApplicationConfigurationMediationService;
import org.kuali.rice.krad.service.RiceApplicationConfigurationService;
import org.kuali.rice.ksb.api.KsbApiServiceLocator;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceBus;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class RiceApplicationConfigurationMediationServiceImpl implements RiceApplicationConfigurationMediationService {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RiceApplicationConfigurationMediationServiceImpl.class);

    public List<Component> getNonDatabaseComponents() {
    	
		// TODO I think the code that's below here will actually pull in more than
		// one reference to a particular application's config service if it is deployed
		// in a cluster, it needs to only pull a single RiceApplicationConfigurationService
		// implementation per application id.  Also, may want to consider load balancing
		// and failover in those cases?  It might be best to try and utilize the client-side
		// KSB proxies that handle a lot of this stuff for us
    	

    	Set<QName> serviceNames = findApplicationConfigurationServices();
		
		List<Component> nonDatabaseComponents = new ArrayList<Component>();
		//add cache per serviceName
		for ( QName serviceName : serviceNames ) {
    	    RiceApplicationConfigurationService rac = findRiceApplicationConfigurationService(serviceName);
            if (rac != null) {
                return rac.getNonDatabaseComponents();
            }
		}
		
		return nonDatabaseComponents;
    }
    
    protected Set<QName> findApplicationConfigurationServices() {
    	Set<QName> names = new HashSet<QName>();
    	List<Endpoint> allEndpoints = KsbApiServiceLocator.getServiceBus().getAllEndpoints();
    	for (Endpoint endpoint : allEndpoints) {
    		QName serviceName = endpoint.getServiceConfiguration().getServiceName();
    		if (serviceName.getLocalPart().equals(KRADServiceLocatorInternal.RICE_APPLICATION_CONFIGURATION_SERVICE)) {
    			names.add(serviceName);
    		}
    	}
    	return names;
    }
    
    protected RiceApplicationConfigurationService findRiceApplicationConfigurationService(QName serviceName) {
    	try {
    		return (RiceApplicationConfigurationService) GlobalResourceLoader.getService(serviceName);
    	} catch (Exception e) {
    		// if the service doesn't exist an exception is thrown
    		LOG.warn("Failed to locate RiceApplicationConfigurationService with name: " + serviceName,e);
    	}
    	return null;
    }
        
}
