/**
 * Copyright 2005-2011 The Kuali Foundation
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


import org.apache.cxf.Bus;
import org.apache.cxf.aegis.databinding.AegisDatabinding;
import org.apache.cxf.endpoint.ServerRegistry;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.ksb.api.bus.ServiceDefinition;
import org.kuali.rice.ksb.api.bus.support.SoapServiceDefinition;
import org.kuali.rice.ksb.security.soap.CXFWSS4JInInterceptor;
import org.kuali.rice.ksb.security.soap.CXFWSS4JOutInterceptor;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SOAPServiceExporter extends AbstractWebServiceExporter implements ServiceExporter {

	static final Logger LOG = Logger.getLogger(SOAPServiceExporter.class);
		
	public SOAPServiceExporter(SoapServiceDefinition serviceDefinition, Bus cxfBus, ServerRegistry cxfServerRegistry) {
	    super(serviceDefinition, cxfBus, cxfServerRegistry);
	}

	/**
	 * This publishes the cxf service onto the cxf bus.
	 * 
	 * @param serviceImpl
	 * @throws Exception
	 */
	@Override
    public void publishService(ServiceDefinition serviceDefinition, Object serviceImpl, String address) {
		ServerFactoryBean svrFactory;
		
		SoapServiceDefinition soapServiceDefinition = (SoapServiceDefinition)serviceDefinition;
		
		//Use the correct bean factory depending on pojo service or jaxws service
		if (soapServiceDefinition.isJaxWsService()){
			LOG.info("Creating JaxWsService " + soapServiceDefinition.getServiceName());
			svrFactory = new JaxWsServerFactoryBean();
		} else {
			svrFactory = new ServerFactoryBean();
			
			//JAXB Binding not supported for pojo service (CXF-897)
			svrFactory.getServiceFactory().setDataBinding(new AegisDatabinding());
		}
	
		svrFactory.setBus(getCXFBus());
		svrFactory.setServiceName(soapServiceDefinition.getServiceName());
		svrFactory.setAddress(address);
		svrFactory.setPublishedEndpointUrl(soapServiceDefinition.getEndpointUrl().toExternalForm());
		svrFactory.setServiceBean(serviceImpl);
		
		try {
			svrFactory.setServiceClass(Class.forName(soapServiceDefinition.getServiceInterface()));
		} catch (ClassNotFoundException e) {
			throw new RiceRuntimeException("Failed to publish service " + soapServiceDefinition.getServiceName() + " because service interface could not be loaded: " + soapServiceDefinition.getServiceInterface(), e);
		}
		
		//Set logging and security interceptors
		svrFactory.getInInterceptors().add(new LoggingInInterceptor());
		svrFactory.getInInterceptors().add(new CXFWSS4JInInterceptor(soapServiceDefinition.getBusSecurity()));
		
		svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
		svrFactory.getOutInterceptors().add(new CXFWSS4JOutInterceptor(soapServiceDefinition.getBusSecurity()));
		
		svrFactory.getInFaultInterceptors().add(new CXFWSS4JInInterceptor(soapServiceDefinition.getBusSecurity()));
		svrFactory.getOutFaultInterceptors().add(new CXFWSS4JOutInterceptor(soapServiceDefinition.getBusSecurity()));
		
		svrFactory.create();
	}

}
