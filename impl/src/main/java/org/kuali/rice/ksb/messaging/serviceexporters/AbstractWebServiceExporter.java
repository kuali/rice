/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.ksb.messaging.serviceexporters;

import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.log4j.Logger;
import org.kuali.rice.ksb.messaging.ServerSideRemotedServiceHolder;
import org.kuali.rice.ksb.messaging.ServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.bam.BAMServerProxy;
import org.kuali.rice.ksb.messaging.servlet.CXFServletControllerAdapter;
import org.kuali.rice.ksb.service.KSBContextServiceLocator;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * Abstract ServiceExporter for web services 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public abstract class AbstractWebServiceExporter {

    static final Logger LOG = Logger.getLogger(AbstractWebServiceExporter.class);
    
    protected ServiceInfo serviceInfo;

    public abstract void publishService(ServiceDefinition serviceDef, Object serviceImpl, String address) throws Exception;

    protected KSBContextServiceLocator serviceLocator;

    public AbstractWebServiceExporter(ServiceInfo serviceInfo, KSBContextServiceLocator serviceLocator) {
        this.serviceInfo = serviceInfo;
        this.serviceLocator = serviceLocator;
    }
    
    public ServerSideRemotedServiceHolder getServiceExporter(Object serviceImpl) {
    	try {			
    		ServiceDefinition serviceDef = getServiceInfo().getServiceDefinition();
    		
    		String serviceAddress = getServiceAddress(serviceDef);
    		
    		//Publish the CXF service if it hasn't already been published
    		if (!(isServicePublished(serviceAddress))){
    			publishService(serviceDef, serviceImpl, serviceAddress);
    		}
    		
    		//Create a CXF mvc controller for this service
    		CXFServletControllerAdapter cxfController = new CXFServletControllerAdapter(this.getServiceInfo());
    		
    		return new ServerSideRemotedServiceHolder(BAMServerProxy.wrap(cxfController, this.getServiceInfo()), this.serviceInfo.getServiceDefinition().getService(), getServiceInfo());
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }

    /**
     * @return the address where the service is (or will be) published
     */
    protected String getServiceAddress(ServiceDefinition serviceDef) {
        //Determine endpoint address to publish service on
        String serviceAddress = serviceDef.getServicePath();
        if (("/").equals(serviceAddress)){
        	serviceAddress = serviceAddress + getServiceInfo().getQname().getLocalPart();
        } else {
        	serviceAddress = serviceAddress + "/" + getServiceInfo().getQname().getLocalPart();
        }
        return serviceAddress;
    }

    /** 
     * This determines if the service has already been published on the CXF bus.
     * 
     * @return true if cxf server exists for this service.
     */
    protected boolean isServicePublished(String serviceAddress) {
    	
    	ServerRegistry serverRegistry = getCXFServerRegistry();
    	List<Server> servers = serverRegistry.getServers();
    	
    	for (Server server:servers){		
    		String endpointAddress = server.getEndpoint().getEndpointInfo().getAddress();
    
    		if (endpointAddress.equals(serviceAddress)){
    			LOG.info("Service already published on CXF, not republishing: " + serviceAddress);
    			return true;
    		}		
    	}
    	
    	return false;
    }

    public ServiceInfo getServiceInfo() {
    	return this.serviceInfo;
    }

    protected Bus getCXFBus() {
    	if (this.serviceLocator != null) {
    		return serviceLocator.getCXFBus();
    	}
    	return KSBServiceLocator.getCXFBus();
    }

    protected ServerRegistry getCXFServerRegistry() {
    	if (this.serviceLocator != null) {
    		return serviceLocator.getCXFServerRegistry();
    	}
    	return KSBServiceLocator.getCXFServerRegistry();
    }

}
