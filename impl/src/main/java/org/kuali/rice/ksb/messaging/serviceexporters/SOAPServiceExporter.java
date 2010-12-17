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


import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.log4j.Logger;
import org.kuali.rice.ksb.messaging.SOAPServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.security.soap.CXFWSS4JInInterceptor;
import org.kuali.rice.ksb.security.soap.CXFWSS4JOutInterceptor;
import org.kuali.rice.ksb.service.KSBContextServiceLocator;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SOAPServiceExporter extends AbstractWebServiceExporter implements ServiceExporter {

	static final Logger LOG = Logger.getLogger(SOAPServiceExporter.class);
	
	public SOAPServiceExporter(ServiceInfo serviceInfo) {
		this(serviceInfo, null);
	}
	
	public SOAPServiceExporter(ServiceInfo serviceInfo, KSBContextServiceLocator serviceLocator) {
	    super(serviceInfo, serviceLocator);
	}

	/**
	 * This publishes the cxf service onto the cxf bus.
	 * 
	 * @param serviceImpl
	 * @throws Exception
	 */
	@Override
    public void publishService(ServiceDefinition serviceDef, Object serviceImpl, String address) throws Exception{
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
		svrFactory.setServiceClass(Class.forName(((SOAPServiceDefinition)serviceDef).getServiceInterface()));
		
		//Set logging and security interceptors
		svrFactory.getInInterceptors().add(new LoggingInInterceptor());
		svrFactory.getInInterceptors().add(new CXFWSS4JInInterceptor(serviceInfo));
		
		svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
		svrFactory.getOutInterceptors().add(new CXFWSS4JOutInterceptor(serviceInfo));
		
		svrFactory.getInFaultInterceptors().add(new CXFWSS4JInInterceptor(serviceInfo));
		svrFactory.getOutFaultInterceptors().add(new CXFWSS4JOutInterceptor(serviceInfo));
		
		svrFactory.create();
	}

}
