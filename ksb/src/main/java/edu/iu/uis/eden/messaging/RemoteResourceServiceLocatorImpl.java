package edu.iu.uis.eden.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.exceptions.RiceRuntimeException;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoaderContainer;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledFuture;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.iu.uis.eden.messaging.exceptionhandling.DefaultMessageExceptionHandler;
import edu.iu.uis.eden.messaging.exceptionhandling.MessageExceptionHandler;
import edu.iu.uis.eden.messaging.objectremoting.ObjectRemoterService;
import edu.iu.uis.eden.messaging.objectremoting.RemoteObjectCleanup;
import edu.iu.uis.eden.messaging.serviceconnectors.ServiceConnectorFactory;

public class RemoteResourceServiceLocatorImpl extends ResourceLoaderContainer implements Runnable, RemoteResourceServiceLocator {

	private static final Logger LOG = Logger.getLogger(RemoteResourceServiceLocatorImpl.class);

	private Random randomNumber = new Random();

	private boolean started;

	private ScheduledFuture future;

	private Map<QName, List<RemotedServiceHolder>> clients = Collections.synchronizedMap(new HashMap<QName, List<RemotedServiceHolder>>());

	public RemoteResourceServiceLocatorImpl(QName name) {
		super(name);
	}

	public synchronized void removeService(ServiceInfo serviceInfo, Object service) {
		QName serviceName = serviceInfo.getQname();
		LOG.info("Removing service '" + serviceName + "'...");
		List<RemotedServiceHolder> clientProxies = this.clients.get(serviceName);
		// these could be null in the case that they were removed by another
		// thread (the thread pool) prior to entry into this method
		if (clientProxies != null) {
			boolean removed = removeServiceFromCollection(service, clientProxies);
			if (!removed) {
				LOG.info("There was no client proxy removed for the given service: " + serviceName);
			}
			if (clientProxies.isEmpty()) {
				List<RemotedServiceHolder> removedList = this.clients.remove(serviceName);
				if (!removedList.isEmpty()) {
					LOG.warn("No client proxy was removed for the given service " + serviceName);
				}
			}
		}
	}

	/**
	 * Removes a service (it's RemotedServiceHolder wrapper) from the list of
	 * services. This isn't very effecient but for time reasons hashcode and
	 * equals wasn't implemented on the RemotedServiceHolder and IPTable, which
	 * is a member of te RemotedServiceHolder.
	 * 
	 * @param service
	 * @param serviceList
	 * @return boolean indicating if the entry was removed from the list
	 */
	private synchronized boolean removeServiceFromCollection(Object service, List<RemotedServiceHolder> serviceList) {
		RemotedServiceHolder serviceToRemove = null;
		for (RemotedServiceHolder remotedServiceHolder : serviceList) {
			if (remotedServiceHolder.getService().equals(service)) {
				serviceToRemove = remotedServiceHolder;
			}
		}
		if (serviceToRemove != null) {

			serviceToRemove.getServiceInfo().setAlive(false);
			List<ServiceInfo> serviceInfos = new ArrayList<ServiceInfo>();
			serviceInfos.add(serviceToRemove.getServiceInfo());
			KSBServiceLocator.getIPTableService().markServicesDead(serviceInfos);
			return serviceList.remove(serviceToRemove);
		}
		
		return false;
	}

	/**
	 * Fetches a service from the client proxies configured in this resource
	 * loader.
	 */
	public synchronized Object getService(QName serviceName) {
		LOG.debug("ResourceLoader " + getName() + " fetching service " + serviceName);

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
		service = getRemotedServiceHolderFromList(clientProxies).getService();
		if (service != null) {
			LOG.debug("Located a remote proxy to service " + serviceName);
		}
		return service;
	}

	public synchronized Object getService(QName qName, String url) {
		List<RemotedServiceHolder> clientProxies = getAllServices(qName);
		if (clientProxies == null || clientProxies.isEmpty()) {
			return null;
		}
		for (RemotedServiceHolder holder : clientProxies) {
			if (holder.getServiceInfo().getEndpointUrl().equals(url)) {
				return holder.getService();
			}
		}
		return null;
	}

	public RemotedServiceHolder getRemotedServiceHolderFromList(List<RemotedServiceHolder> remotedServices) {
		return remotedServices.get(this.randomNumber.nextInt(remotedServices.size()));
	}

	public synchronized List<RemotedServiceHolder> getAllServices(QName qName) {
		List<RemotedServiceHolder> clientProxies = this.clients.get(qName);
		if (clientProxies == null) {
			LOG.debug("Client proxies are null, Re-aquiring services.  Message Entity " + Core.getCurrentContextConfig().getMessageEntity());
			run();
			clientProxies = this.clients.get(qName);
			if (clientProxies == null || clientProxies.size() == 0) {
				throw new RiceRuntimeException("No remote services available for client access when attempting to lookup '" + qName + "'");
			}
		}
		return clientProxies;
	}

	public synchronized void run() {
		if (!isStarted()) {
			return;
		}
		LOG.debug("Checking for new services on the bus");
		List<ServiceInfo> servicesOnBus = null;
		if (Core.getCurrentContextConfig().getDevMode()) {
			servicesOnBus = new ArrayList<ServiceInfo>();
			for (RemotedServiceHolder remoteServiceHolder : KSBServiceLocator.getServiceDeployer().getPublishedServices().values()) {
				servicesOnBus.add(remoteServiceHolder.getServiceInfo());
			}
		} else {
			servicesOnBus = KSBServiceLocator.getIPTableService().fetchAll();	
		}

		if (new RoutingTableDiffCalculator().calculateClientSideUpdate(this.clients, servicesOnBus)) {
			LOG.debug("Located new services on the bus, numServices=" + servicesOnBus.size());
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
			this.clients = updatedRemoteServicesMap;
		} else {
			LOG.debug("No new services on the bus.");
		}
	}

	private void registerClient(ServiceInfo serviceInfo, Map<QName, List<RemotedServiceHolder>> clientMap) {

		RemotedServiceHolder serviceHolder;
		try {
			serviceHolder = ServiceConnectorFactory.getServiceConnector(serviceInfo).getServiceHolder();
		} catch (Exception e) {
			LOG.error("Failed to register client service " + serviceInfo.getQname(), e);
			this.removeService(serviceInfo, null);
			return;
		}

		if (clientMap.get(serviceInfo.getQname()) == null) {
			clientMap.put(serviceInfo.getQname(), new ArrayList<RemotedServiceHolder>());
		}
		clientMap.get(serviceInfo.getQname()).add(serviceHolder);
	}


	public boolean isStarted() {
		return this.started;
	}

	public void start() throws Exception {
		LOG.info("Starting the RemoteResourceServiceLocator...");

		int refreshRate = Core.getCurrentContextConfig().getRefreshRate();
		this.future = KSBServiceLocator.getThreadPool().scheduleWithFixedDelay(this, 5, refreshRate, TimeUnit.SECONDS);
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
		if (StringUtils.isEmpty(definition.getMessageEntity())) {
			return null;
		}
		QName objectRemoterName = new QName(definition.getMessageEntity(), KSBServiceLocator.OBJECT_REMOTER);
		ObjectRemoterService classRemoter = (ObjectRemoterService)GlobalResourceLoader.getService(objectRemoterName);
		ServiceInfo serviceInfo = classRemoter.getRemotedClassURL(definition);
		
		if (serviceInfo == null) {
			return null;
		}
		
		try {
			RemoteObjectCleanup remoteCleanup = new RemoteObjectCleanup(objectRemoterName, serviceInfo.getQname());
			KSBServiceLocator.getTransactionManager().getTransactionManager().getTransaction().registerSynchronization(remoteCleanup);
			
			return ServiceConnectorFactory.getServiceConnector(serviceInfo).getServiceHolder().getService();
		} catch (Exception e) {
			throw new RiceRuntimeException(e);
		}
	}

	public synchronized MessageExceptionHandler getMessageExceptionHandler(QName qname) {
		List<RemotedServiceHolder> remotedServices = getAllServices(qname);
		if (remotedServices == null || remotedServices.isEmpty()) {
			throw new RiceRuntimeException("No services found for name " + qname);
		}
		RemotedServiceHolder serviceHolder = getRemotedServiceHolderFromList(remotedServices);
		if (serviceHolder != null) {
			String messageExceptionHandlerName = serviceHolder.getServiceInfo().getServiceDefinition().getMessageExceptionHandler();
			if (messageExceptionHandlerName == null) {
				messageExceptionHandlerName = DefaultMessageExceptionHandler.class.getName();
			}
			return (MessageExceptionHandler) GlobalResourceLoader.getObject(new ObjectDefinition(messageExceptionHandlerName));
		}
		throw new RiceRuntimeException("No service with QName " + qname + " found");
	}
}