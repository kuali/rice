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
package org.kuali.rice.ksb.messaging.serviceconnectors;

import java.util.Map;

import org.apache.cxf.binding.BindingFactoryManager;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSBindingFactory;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.log4j.Logger;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.security.credentials.CredentialsSource;
import org.kuali.rice.ksb.messaging.BusClientFailureProxy;
import org.kuali.rice.ksb.messaging.RESTServiceDefinition;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.bam.BAMClientProxy;
import org.kuali.rice.ksb.security.soap.CredentialsOutHandler;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * implementation of {@link ResourceFacade}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ResourceFacadeImpl implements ResourceFacade {

	private static final Logger LOG = Logger.getLogger(ResourceFacadeImpl.class);

	private final ServiceInfo serviceInfo;
	private CredentialsSource credentialsSource;

	public ResourceFacadeImpl(final ServiceInfo serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.ksb.messaging.serviceconnectors.ResourceFacade#getResource(java.lang.Class)
	 */
	public <R> R getResource(Class<R> resourceClass) {
		if (resourceClass == null) throw new IllegalArgumentException("resourceClass argument must not be null");

		RESTServiceDefinition restServiceDefinition = (RESTServiceDefinition) serviceInfo.getServiceDefinition();

		if (!restServiceDefinition.hasClass(resourceClass.getName())) {
			throw new IllegalArgumentException("Service " + serviceInfo.getServiceName() +
					" does not contain an implementation of type " + resourceClass.getName());
		}

		return getServiceProxy(resourceClass);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.ksb.messaging.serviceconnectors.ResourceFacade#getResource(java.lang.String)
	 */
	public <R> R getResource(String resourceName) {
		RESTServiceDefinition restServiceDefinition = (RESTServiceDefinition) serviceInfo.getServiceDefinition();

		String resourceClassName = null;

		Map<String, String> resourceToClassNameMap = restServiceDefinition.getResourceToClassNameMap();

		if (resourceName != null && resourceToClassNameMap != null)
			resourceClassName = resourceToClassNameMap.get(resourceName);
		else
			resourceClassName = restServiceDefinition.getResourceClass();

		if (resourceClassName == null)
			throw new RiceRuntimeException("No resource class name was found for the specified resourceName: " + resourceName);

		Class<?> resourceClass = null;

		try {
			resourceClass = Class.forName(resourceClassName);
		} catch (ClassNotFoundException e) {
			throw new RiceRuntimeException("Configured resource class " + resourceClassName +
					" in service " + serviceInfo.getServiceName() + " is not loadable", e);
		}

        return (R)getServiceProxy(resourceClass);
	}

	/**
	 * This method ...
	 *
	 * @param resourceClass
	 * @return
	 */
	private <R> R getServiceProxy(Class<R> resourceClass) {
		JAXRSClientFactoryBean clientFactory;

        clientFactory = new JAXRSClientFactoryBean();
        clientFactory.setBus(KSBServiceLocator.getCXFBus());

        clientFactory.setResourceClass(resourceClass);
        clientFactory.setAddress(serviceInfo.getActualEndpointUrl());
        BindingFactoryManager bindingFactoryManager = KSBServiceLocator.getCXFBus().getExtension(BindingFactoryManager.class);
        JAXRSBindingFactory bindingFactory = new JAXRSBindingFactory();
        bindingFactory.setBus(KSBServiceLocator.getCXFBus());

        bindingFactoryManager.registerBindingFactory(JAXRSBindingFactory.JAXRS_BINDING_ID, bindingFactory);

        //Set logging interceptors
        if (LOG.isDebugEnabled()) {
        	clientFactory.getOutInterceptors().add(new LoggingOutInterceptor());
        }

        if (getCredentialsSource() != null) {
            clientFactory.getOutInterceptors().add(new CredentialsOutHandler(getCredentialsSource(), serviceInfo));
        }

        if (LOG.isDebugEnabled()) {
        	clientFactory.getInInterceptors().add(new LoggingInInterceptor());
        }

        Object service = clientFactory.create();
        return (R)getServiceProxyWithFailureMode(service, serviceInfo);
	}

	public boolean isSingleResourceService() {
		RESTServiceDefinition restServiceDefinition = (RESTServiceDefinition) serviceInfo.getServiceDefinition();
		return restServiceDefinition.getResourceToClassNameMap() == null;
	}

	public void setCredentialsSource(final CredentialsSource credentialsSource) {
		this.credentialsSource = credentialsSource;
	}

	protected CredentialsSource getCredentialsSource() {
		return this.credentialsSource;
	}

	protected Object getServiceProxyWithFailureMode(final Object service,
			final ServiceInfo serviceInfo) {
		Object bamWrappedClientProxy = BAMClientProxy
				.wrap(service, serviceInfo);
		return BusClientFailureProxy.wrap(bamWrappedClientProxy, serviceInfo);
	}

}
