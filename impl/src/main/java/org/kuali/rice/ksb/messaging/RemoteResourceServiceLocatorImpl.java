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
package org.kuali.rice.ksb.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.exception.RiceRemoteServiceConnectionException;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoaderContainer;
import org.kuali.rice.ksb.messaging.exceptionhandling.DefaultMessageExceptionHandler;
import org.kuali.rice.ksb.messaging.exceptionhandling.MessageExceptionHandler;
import org.kuali.rice.ksb.messaging.objectremoting.ObjectRemoterService;
import org.kuali.rice.ksb.messaging.objectremoting.RemoteObjectCleanup;
import org.kuali.rice.ksb.messaging.serviceconnectors.ServiceConnectorFactory;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.util.KSBConstants;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RemoteResourceServiceLocatorImpl extends ResourceLoaderContainer implements Runnable, RemoteResourceServiceLocator {

	private static final Logger LOG = Logger.getLogger(RemoteResourceServiceLocatorImpl.class);

	private Random randomNumber = new Random();

	private boolean started;

	private ScheduledFuture future;

	private Map<QName, List<RemotedServiceHolder>> clients = Collections.synchronizedMap(new HashMap<QName, List<RemotedServiceHolder>>());
	// You can't synchronize on an instance that may be replaced by a setter
	// unless you want to potentially find a bunch of orphaned data in the
	// database over time.
	private Object clientsMutex = new Object();
	
	public RemoteResourceServiceLocatorImpl(QName name) {
		super(name);
	}

	public void removeService(ServiceInfo serviceInfo) {
		QName serviceName = serviceInfo.getQname();
		if ( LOG.isInfoEnabled() ) {
			LOG.info("Removing service '" + serviceName + "'...");
		}
		List<RemotedServiceHolder> clientProxies = this.getClients().get(serviceName);
		// these could be null in the case that they were removed by another
		// thread (the thread pool) prior to entry into this method
		if (clientProxies != null) {
			boolean removed = removeServiceFromCollection(serviceInfo, clientProxies);
			if (!removed) {
				if ( LOG.isInfoEnabled() ) {
					LOG.info("There was no client proxy removed for the given service: " + serviceName);
				}
			}
			if (clientProxies.isEmpty()) {
				List<RemotedServiceHolder> removedList = this.getClients().remove(serviceName);
				if (removedList.isEmpty()) {
					LOG.warn("No client proxy was removed for the given service " + serviceName);
				}
			}
		}
	}

	/**
	 * Removes a service (its RemotedServiceHolder wrapper) from the list of
	 * services. This isn't very efficient but for time reasons hashcode and
	 * equals wasn't implemented on the RemotedServiceHolder and IPTable, which
	 * is a member of the RemotedServiceHolder.
	 *
	 * @param serviceInfo
	 * @param serviceList
	 * @return boolean indicating if the entry was removed from the list
	 */
	private boolean removeServiceFromCollection(ServiceInfo serviceInfo, List<RemotedServiceHolder> serviceList) {
	    	List<ServiceHolder> servicesToRemove = new ArrayList<ServiceHolder>();

		for (ServiceHolder remotedServiceHolder : serviceList) {
		    try {
			if (remotedServiceHolder.getServiceInfo().getEndpointUrl().equals(serviceInfo.getEndpointUrl())) {
			    servicesToRemove.add(remotedServiceHolder);
			}
		    } catch (Exception e) {
			LOG.warn("An exception was thrown when attempting to compare endpoint URLs", e);
		    }
		}
		if (! servicesToRemove.isEmpty()) {
		    for (ServiceHolder serviceToRemove : servicesToRemove) {
    			serviceToRemove.getServiceInfo().setAlive(false);
    			List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
    			serviceInfos.add(serviceToRemove.getServiceInfo());
    			KSBServiceLocator.getServiceRegistry().markServicesDead(serviceInfos);
		    }
		    return serviceList.removeAll(servicesToRemove);
		}
		return false;
	}

	/**
	 * Fetches a service from the client proxies configured in this resource
	 * loader.
	 */
	public Object getService(QName serviceName) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("ResourceLoader " + getName() + " fetching service " + serviceName);
		}

		//go to our remotely deployed services first
		RemotedServiceRegistry remoteRegistry = KSBServiceLocator.getServiceDeployer();
		Object service = remoteRegistry.getLocalService(serviceName);
		if (service != null) {
			return service;
		}

		List<RemotedServiceHolder> clientProxies = getAllServices(serviceName);
		if (clientProxies == null || clientProxies.isEmpty()) {
			return null;
		}
		// randomly get a proxy for 'load balancing'
		ServiceHolder serviceHolder = getRemotedServiceHolderFromList(clientProxies);
		try {
		    service = serviceHolder.getService();
		} catch (Exception e) {
		    LOG.error("Caught exception getting service " + serviceName);
		    this.removeService(serviceHolder.getServiceInfo());
		    return getService(serviceName);
		}
		if (service != null) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("Located a remote proxy to service " + serviceName);
			}
		}
		return service;
	}

	public Object getService(QName qName, String url) {
		List<RemotedServiceHolder> clientProxies = getAllServices(qName);
		if (clientProxies == null || clientProxies.isEmpty()) {
			return null;
		}
		for (ServiceHolder holder : clientProxies) {
			if (holder.getServiceInfo().getEndpointUrl().equals(url)) {
			    	try {
			    	    return holder.getService();
			    	} catch (Exception e) {
			    	    this.removeService(holder.getServiceInfo());
			    	}
			}
		}
		return null;
	}
	
	public List<QName> getServiceNamesForUnqualifiedName(String unqualifiedServiceName) {
		List<QName> names = new ArrayList<QName>();
		for (QName serviceName : getClients().keySet()) {
			if (serviceName.getLocalPart().equals(unqualifiedServiceName)) {
				names.add(serviceName);
			}
		}
		return names;
	}

	public ServiceHolder getRemotedServiceHolderFromList(List<RemotedServiceHolder> remotedServices) {
		return remotedServices.get(this.randomNumber.nextInt(remotedServices.size()));
	}

	/**
	 * Returns an immutable list of services.
	 */
	public List<RemotedServiceHolder> getAllServices(QName qName) {
		List<RemotedServiceHolder> clientProxies = this.getClients().get(qName);
		if (clientProxies == null) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("Client proxies are null, Re-aquiring services.  Service Namespace " + ConfigContext.getCurrentContextConfig().getServiceNamespace());
			}
			
			// TODO: What is this all about?
			run();
			
			clientProxies = this.getClients().get(qName);
			if (clientProxies == null || clientProxies.size() == 0) {
				if ( LOG.isDebugEnabled() ) {
					Map<QName, List<RemotedServiceHolder>> x = this.getClients();
					if (x != null) {
						for (QName b : x.keySet()) {
							LOG.debug(b.getNamespaceURI() + " " + b.getLocalPart());
						}
					}
				}
				throw new RiceRemoteServiceConnectionException("No remote services available for client access when attempting to lookup '" + qName + "'");
			}
		}
		// be sure to return an immutable copy of the list
		return Collections.unmodifiableList(new ArrayList<RemotedServiceHolder>(clientProxies));
	}

	public void refresh() {
	    run();
	}

	public void run() {
		if (!isStarted()) {
			return;
		}
		LOG.debug("Checking for new services on the bus");
		List<ServiceInfo> servicesOnBus = null;
		if (ConfigContext.getCurrentContextConfig().getDevMode()) {
			servicesOnBus = new ArrayList<ServiceInfo>();
			for (ServiceHolder remoteServiceHolder : KSBServiceLocator.getServiceDeployer().getPublishedServices().values()) {
				servicesOnBus.add(remoteServiceHolder.getServiceInfo());
			}
		} else {
			servicesOnBus = KSBServiceLocator.getServiceRegistry().fetchAllActive();
		}

		synchronized ( clientsMutex ) {
			if (new RoutingTableDiffCalculator().calculateClientSideUpdate(this.getClients(), servicesOnBus)) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("Located new services on the bus, numServices=" + servicesOnBus.size());
				}
				Map<QName, List<RemotedServiceHolder>> updatedRemoteServicesMap = new HashMap<QName, List<RemotedServiceHolder>>();
				for (Iterator<ServiceInfo> iter = servicesOnBus.iterator(); iter.hasNext();) {
					ServiceInfo entry = iter.next();
					if (entry.getAlive()) {
						try {
							registerClient(entry, updatedRemoteServicesMap);
						} catch (Exception e) {
							LOG.error("Unable to register client " + entry, e);
						}
					}
				}
				this.setClients(updatedRemoteServicesMap);
			} else {
				LOG.debug("No new services on the bus.");
			}
		}
	}

	private void registerClient(ServiceInfo serviceInfo, Map<QName, List<RemotedServiceHolder>> clientMap) {



		if (clientMap.get(serviceInfo.getQname()) == null) {
			clientMap.put(serviceInfo.getQname(), new ArrayList<RemotedServiceHolder>());
		}
		installAlternateEndpoint(serviceInfo);
		clientMap.get(serviceInfo.getQname()).add(new RemotedServiceHolder(serviceInfo));
	}
	
	protected void installAlternateEndpoint(ServiceInfo serviceInfo) {
	List<AlternateEndpointLocation> alternateEndpointLocations = (List<AlternateEndpointLocation>) ConfigContext
		.getCurrentContextConfig().getObject(KSBConstants.Config.KSB_ALTERNATE_ENDPOINT_LOCATIONS);
	if (alternateEndpointLocations != null) {
	    for (AlternateEndpointLocation alternateEndpointLocation : alternateEndpointLocations) {
		if (Pattern.matches(".*" + alternateEndpointLocation.getEndpointHostReplacementPattern() + ".*", serviceInfo
			.getEndpointUrl())) {
		    Pattern myPattern = Pattern.compile(alternateEndpointLocation.getEndpointHostReplacementPattern());
		    Matcher myMatcher = myPattern.matcher(serviceInfo.getEndpointUrl());
		    String alternateEndpoint = myMatcher.replaceFirst(alternateEndpointLocation
			    .getEndpointHostReplacementValue());
		    if ( LOG.isInfoEnabled() ) {
		    	LOG.info("Found an alternate url host value ("
					    + alternateEndpointLocation.getEndpointHostReplacementValue() + ") for endpoint: "
					    + serviceInfo.getEndpointUrl() + " -> instead using: " + alternateEndpoint);
		    }
		    serviceInfo.setEndpointAlternateUrl(alternateEndpoint);
		    break;
		}
	    }
	}
	List<AlternateEndpoint> alternateEndpoints = (List<AlternateEndpoint>) ConfigContext.getCurrentContextConfig().getObject(
		KSBConstants.Config.KSB_ALTERNATE_ENDPOINTS);
	if (alternateEndpoints != null) {
	    for (AlternateEndpoint alternateEndpoint : alternateEndpoints) {
		if (Pattern.matches(alternateEndpoint.getEndpointUrlPattern(), serviceInfo.getEndpointUrl())) {
			if ( LOG.isInfoEnabled() ) {
				LOG.info("Found an alternate url for endpoint: " + serviceInfo.getEndpointUrl() + " -> instead using: "
					    + alternateEndpoint.getActualEndpoint());
			}
		    serviceInfo.setEndpointAlternateUrl(alternateEndpoint.getActualEndpoint());
		    break;
		}
	    }
	}
    }


	public boolean isStarted() {
		return this.started;
	}

	public void start() throws Exception {
		LOG.info("Starting the RemoteResourceServiceLocator...");

		int refreshRate = ConfigContext.getCurrentContextConfig().getRefreshRate();
		this.future = KSBServiceLocator.getScheduledPool().scheduleWithFixedDelay(this, 30, refreshRate, TimeUnit.SECONDS);
		this.started = true;
		run();
		LOG.info("...RemoteResourceServiceLocator started.");
	}

	public void stop() throws Exception {
		LOG.info("Stopping the RemoteResourceServiceLocator...");
		if (this.future != null) {
			if (!this.future.cancel(true)) {
				LOG.warn("Failed to cancel the RemoteResourceServiceLocator service.");
			}
			this.future = null;
		}
		this.started = false;
		LOG.info("...RemoteResourceServiceLocator stopped.");
	}

	public Object getObject(ObjectDefinition definition) {
		if (definition.isAtRemotingLayer()) {
			return null;
		}
		if (StringUtils.isEmpty(definition.getServiceNamespace())) {
			return null;
		}
		QName objectRemoterName = new QName(definition.getServiceNamespace(), KSBConstants.ServiceNames.OBJECT_REMOTER);
		ObjectRemoterService classRemoter = (ObjectRemoterService)GlobalResourceLoader.getService(objectRemoterName);
		ServiceInfo serviceInfo = classRemoter.getRemotedClassURL(definition);

		if (serviceInfo == null) {
			return null;
		}

		try {
			RemoteObjectCleanup remoteCleanup = new RemoteObjectCleanup(objectRemoterName, serviceInfo.getQname());
			if (TransactionSynchronizationManager.isActualTransactionActive()) {
			    TransactionSynchronizationManager.registerSynchronization(remoteCleanup);
			}
			return ServiceConnectorFactory.getServiceConnector(serviceInfo).getService();
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		}
	}

	public MessageExceptionHandler getMessageExceptionHandler(QName qname) {
		List<RemotedServiceHolder> remotedServices = getAllServices(qname);
		if (remotedServices == null || remotedServices.isEmpty()) {
			throw new RiceRuntimeException("No services found for name " + qname);
		}
		ServiceHolder serviceHolder = getRemotedServiceHolderFromList(remotedServices);
		if (serviceHolder != null) {
			String messageExceptionHandlerName = serviceHolder.getServiceInfo().getServiceDefinition().getMessageExceptionHandler();
			if (messageExceptionHandlerName == null) {
				messageExceptionHandlerName = DefaultMessageExceptionHandler.class.getName();
			}
			return (MessageExceptionHandler) GlobalResourceLoader.getObject(new ObjectDefinition(messageExceptionHandlerName));
		}
		throw new RiceRuntimeException("No service with QName " + qname + " found");
	}

	public Map<QName, List<RemotedServiceHolder>> getClients() {
		synchronized ( clientsMutex ) {
		    return this.clients;			
		}
	}

	public void setClients(Map<QName, List<RemotedServiceHolder>> clients) {
		synchronized ( clientsMutex ) {
			this.clients = clients;
		}
	}
}
