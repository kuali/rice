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

import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.log4j.Logger;
import org.kuali.rice.ksb.messaging.RESTServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.service.KSBContextServiceLocator;


/**
 * ServiceExporter for RESTful services.  This class handles the service binding and exposure via CXF.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RESTServiceExporter extends AbstractWebServiceExporter implements ServiceExporter {

	private static final Logger LOG = Logger.getLogger(RESTServiceExporter.class);

	private ServiceInfo serviceInfo;
	private KSBContextServiceLocator serviceLocator;

	public RESTServiceExporter(ServiceInfo serviceInfo) {
		this(serviceInfo, null);
	}

	public RESTServiceExporter(ServiceInfo serviceInfo, KSBContextServiceLocator serviceLocator) {
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
		RESTServiceDefinition restServiceDef = (RESTServiceDefinition)serviceDef;

		LOG.info("Creating JAXRSService " + (getServiceInfo().getQname()));
		JAXRSServerFactoryBean svrFactory = new JAXRSServerFactoryBean();
        svrFactory.setBus(getCXFBus());

        List<Object> resources = restServiceDef.getResources();
        if (resources != null && !resources.isEmpty()) {
        	svrFactory.setServiceBeans(resources);
        } else {
        Class<?> resourceClass = this.getClass().getClassLoader().loadClass(restServiceDef.getResourceClass());
		svrFactory.setResourceClasses(resourceClass);
		svrFactory.setResourceProvider(resourceClass, new SingletonResourceProvider(serviceImpl));
        }

        svrFactory.setServiceName(getServiceInfo().getQname());
        svrFactory.setAddress(address);
        svrFactory.setExtensionMappings(restServiceDef.getExtensionMappings());
        svrFactory.setLanguageMappings(restServiceDef.getLanguageMappings());

        List<Object> providers = restServiceDef.getProviders();
        if (providers != null)
        	svrFactory.setProviders(providers);

        BindingFactoryManager bindingFactoryManager = getCXFBus().getExtension(BindingFactoryManager.class);
        JAXRSBindingFactory bindingFactory = new JAXRSBindingFactory();
        bindingFactory.setBus(getCXFBus());
        bindingFactoryManager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, bindingFactory);

		//Set logging interceptors
        if (LOG.isDebugEnabled()) {
        	svrFactory.getInInterceptors().add(new LoggingInInterceptor());
        }
//        svrFactory.getInInterceptors().add(new RESTConnector.VerifyingInInterceptor());
        if (LOG.isDebugEnabled()) {
        	svrFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        }
//		svrFactory.getOutInterceptors().add(new RESTConnector.SigningOutInterceptor());

		svrFactory.setPublishedEndpointUrl(getServiceInfo().getActualEndpointUrl());
		Server server = svrFactory.create();
	}

}
