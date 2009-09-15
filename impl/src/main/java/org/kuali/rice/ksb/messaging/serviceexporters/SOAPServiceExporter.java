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
package org.kuali.rice.ksb.messaging.serviceexporters;

import java.util.List;

import org.apache.cxf.Bus;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.log4j.Logger;
import org.kuali.rice.ksb.messaging.SOAPServiceDefinition;
import org.kuali.rice.ksb.messaging.ServerSideRemotedServiceHolder;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.bam.BAMServerProxy;
import org.kuali.rice.ksb.messaging.servlet.CXFServletControllerAdapter;
import org.kuali.rice.ksb.security.soap.CXFWSS4JInInterceptor;
import org.kuali.rice.ksb.security.soap.CXFWSS4JOutInterceptor;
import org.kuali.rice.ksb.service.KSBContextServiceLocator;
import org.kuali.rice.ksb.service.KSBServiceLocator;


/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SOAPServiceExporter implements ServiceExporter {

	private static final Logger LOG = Logger.getLogger(SOAPServiceExporter.class);
	
	private ServiceInfo serviceInfo;
	private KSBContextServiceLocator serviceLocator;

	public SOAPServiceExporter(ServiceInfo serviceInfo) {
		this(serviceInfo, null);
	}
	
	public SOAPServiceExporter(ServiceInfo serviceInfo, KSBContextServiceLocator serviceLocator) {
		this.serviceInfo = serviceInfo;
		this.serviceLocator = serviceLocator;
	}

	public ServerSideRemotedServiceHolder getServiceExporter(Object serviceImpl) {
		try {			
			SOAPServiceDefinition serviceDef = (SOAPServiceDefinition) getServiceInfo().getServiceDefinition();
			
			//Determine endpoint address to publish service on
			String serviceAddress = serviceDef.getServicePath();
			if (("/").equals(serviceAddress)){
				serviceAddress = serviceAddress + getServiceInfo().getQname().getLocalPart();
			} else {
				serviceAddress = serviceAddress + "/" + getServiceInfo().getQname().getLocalPart();
			}
			
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
	 * This publishes the cxf service onto the cxf bus.
	 * 
	 * @param serviceImpl
	 * @throws Exception
	 */
	public void publishService(SOAPServiceDefinition serviceDef, Object serviceImpl, String address) throws Exception{
		ServerFactoryBean svrFactory;
		
		//Use the correct bean factory depending on pojo service or jaxws service
		if (((SOAPServiceDefinition)getServiceInfo().getServiceDefinition()).isJaxWsService()){
			LOG.info("Creating JaxWsService " + (getServiceInfo().getQname()));
			svrFactory = new JaxWsServerFactoryBean();
		} else {
			svrFactory = new ServerFactoryBean();
			
			//JAXB Binding not supported for pojo service (CXF-897)
			svrFactory.getServiceFactory().setDataBinding(new AegisDatabinding());
		}
	
		svrFactory.setBus(getCXFBus());
		svrFactory.setServiceName(getServiceInfo().getQname());
		svrFactory.setAddress(address);
		svrFactory.setPublishedEndpointUrl(getServiceInfo().getActualEndpointUrl());
		svrFactory.setServiceBean(serviceImpl);
		svrFactory.setServiceClass(Class.forName(serviceDef.getServiceInterface()));
		
		//Set logging and security interceptors
		svrFactory.getInInterceptors().add(new LoggingInInterceptor());
		svrFactory.getInInterceptors().add(new CXFWSS4JInInterceptor(serviceInfo));
		
		svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
		svrFactory.getOutInterceptors().add(new CXFWSS4JOutInterceptor(serviceInfo));
		
		svrFactory.getInFaultInterceptors().add(new CXFWSS4JInInterceptor(serviceInfo));
		svrFactory.getOutFaultInterceptors().add(new CXFWSS4JOutInterceptor(serviceInfo));
		
		svrFactory.create();
	}

	/** 
	 * This determines if the service has already been published on the CXF bus.
	 * 
	 * @return true if cxf server exists for this service.
	 */
	protected boolean isServicePublished(String serviceAddress){
		
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
