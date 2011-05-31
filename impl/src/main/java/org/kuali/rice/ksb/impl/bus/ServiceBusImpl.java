package org.kuali.rice.ksb.impl.bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.lifecycle.BaseLifecycle;
import org.kuali.rice.ksb.api.bus.Endpoint;
import org.kuali.rice.ksb.api.bus.ServiceBus;
import org.kuali.rice.ksb.api.bus.ServiceConfiguration;
import org.kuali.rice.ksb.api.bus.ServiceDefinition;
import org.kuali.rice.ksb.api.registry.ServiceEndpoint;
import org.kuali.rice.ksb.api.registry.ServiceInfo;
import org.kuali.rice.ksb.api.registry.ServiceRegistry;
import org.kuali.rice.ksb.impl.registry.diff.CompleteServiceDiff;
import org.kuali.rice.ksb.impl.registry.diff.LocalServicesDiff;
import org.kuali.rice.ksb.impl.registry.diff.RemoteServicesDiff;
import org.kuali.rice.ksb.impl.registry.diff.ServiceRegistryDiffCalculator;
import org.kuali.rice.ksb.messaging.serviceexporters.ServiceExportManager;
import org.kuali.rice.ksb.messaging.threadpool.KSBScheduledPool;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ServiceBusImpl extends BaseLifecycle implements ServiceBus, InitializingBean, DisposableBean {
	
	private static final Logger LOG = Logger.getLogger(ServiceBusImpl.class);
	
	private final Object serviceLock = new Object();
	private final Object synchronizeLock = new Object();
	private final Random randomNumber = new Random();
	
	// injected values
	private String instanceId;
	private ServiceRegistry serviceRegistry;
	private ServiceRegistryDiffCalculator diffCalculator;
	private ServiceExportManager serviceExportManager;
	private KSBScheduledPool scheduledPool;
	
	private ScheduledFuture<?> registrySyncFuture;
	
	/**
	 * Contains endpoints for services which were published by this client application.
	 */
	private final Map<QName, LocalService> localServices;
	
	/**
	 * Contains endpoints for services which exist remotely.  This list may not be
	 * entirely complete as entries get lazily loaded into it as services are requested.
	 */
	private final Map<QName, Set<RemoteService>> clientRegistryCache;
		
	public ServiceBusImpl() {
		this.localServices = new HashMap<QName, LocalService>();
		this.clientRegistryCache = new HashMap<QName, Set<RemoteService>>();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isBlank(instanceId)) {
			throw new IllegalStateException("a valid instanceId was not injected");
		}
		if (serviceRegistry == null) {
			throw new IllegalStateException("serviceRegistry was not injected");
		}
		if (diffCalculator == null) {
			throw new IllegalStateException("diffCalculator was not injected");
		}
		if (scheduledPool == null) {
			throw new IllegalStateException("scheduledPool was not injected");
		}
	}
	
	@Override
	public void start() throws Exception {
		startSynchronizationThread();
		super.start();
	}
		
	protected boolean isDevMode() {
		return ConfigContext.getCurrentContextConfig().getDevMode();
	}

	protected void startSynchronizationThread() {
		synchronized (synchronizeLock) {
			LOG.info("Starting Service Bus synchronization thread...");
			if (!isDevMode()) {
				int refreshRate = ConfigContext.getCurrentContextConfig().getRefreshRate();
				Runnable runnable = new Runnable() {
					public void run() {
						synchronize();
					}
				};
				this.registrySyncFuture = scheduledPool.scheduleWithFixedDelay(runnable, 30, refreshRate, TimeUnit.SECONDS);
			}
			LOG.info("...Service Bus synchronization thread successfully started.");
		}
	}
	
	@Override
	public void destroy() throws Exception {
		LOG.info("Stopping the Service Bus...");
		stopSynchronizationThread();
		serviceRegistry.takeInstanceOffline(getInstanceId());
		LOG.info("...Service Bus successfully stopped.");
	}
	
	protected void stopSynchronizationThread() {
		synchronized (synchronizeLock) {
			// remove services from the bus
			if (this.registrySyncFuture != null) {
				if (!this.registrySyncFuture.cancel(false)) {
					LOG.warn("Failed to cancel registry sychronization.");
				}
				this.registrySyncFuture = null;
			}
		}
	}

	@Override
	public String getInstanceId() {
		return this.instanceId;
	}
	
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	@Override
	public List<Endpoint> getEndpoints(QName serviceName) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName cannot be null");
		}
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		synchronized (serviceLock) {
			endpoints.addAll(getRemoteEndpoints(serviceName));
			Endpoint localEndpoint = getLocalEndpoint(serviceName);
			if (localEndpoint != null) {
				for (Iterator<Endpoint> iterator = endpoints.iterator(); iterator.hasNext();) {
					Endpoint endpoint = (Endpoint) iterator.next();
					if (localEndpoint.getServiceConfiguration().equals(endpoint.getServiceConfiguration())) {
						iterator.remove();
						break;
					}
				}
				// add at first position, just because we like the local endpoint the best, it's our friend ;)
				endpoints.add(0, localEndpoint);
			}
		}
		return Collections.unmodifiableList(endpoints);
	}
	
	@Override
	public List<Endpoint> getRemoteEndpoints(QName serviceName) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName cannot be null");
		}
		List<Endpoint> endpoints = new ArrayList<Endpoint>();
		synchronized (serviceLock) {
			Set<RemoteService> remoteServices = clientRegistryCache.get(serviceName);
			if (remoteServices != null) {
				for (RemoteService remoteService : remoteServices) {
					endpoints.add(remoteService.getEndpoint());
				}
			}
		}
		return Collections.unmodifiableList(endpoints);
	}

	@Override
	public Endpoint getLocalEndpoint(QName serviceName) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName cannot be null");
		}
		synchronized (serviceLock) {
			LocalService localService = localServices.get(serviceName);
			if (localService != null) {
				return localService.getEndpoint();
			}
			return null;
		}
	}

	@Override
	public Map<QName, Endpoint> getLocalEndpoints() {
		Map<QName, Endpoint> localEndpoints = new HashMap<QName, Endpoint>();
		synchronized (serviceLock) {
			for (QName localServiceName : localServices.keySet()) {
				LocalService localService = localServices.get(localServiceName);
				localEndpoints.put(localServiceName, localService.getEndpoint());
			}
		}
		return Collections.unmodifiableMap(localEndpoints);
	}

	@Override
	public List<Endpoint> getAllEndpoints() {
		List<Endpoint> allEndpoints = new ArrayList<Endpoint>();
		synchronized (serviceLock) {
			for (QName serviceName : this.localServices.keySet()) {
				allEndpoints.add(this.localServices.get(serviceName).getEndpoint());
			}
			for (QName serviceName : this.clientRegistryCache.keySet()) {
				Set<RemoteService> remoteServices = clientRegistryCache.get(serviceName);
				for (RemoteService remoteService : remoteServices) {
					allEndpoints.add(remoteService.getEndpoint());
				}
			}
		}
		return Collections.unmodifiableList(allEndpoints);
	}

	@Override
	public Endpoint getEndpoint(QName serviceName) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName cannot be null");
		}
		Endpoint availableEndpoint = null;
		synchronized (serviceLock) {
			// look at local services first
			availableEndpoint = getLocalEndpoint(serviceName);
			if (availableEndpoint == null) {
				 // TODO - would be better to return an Endpoint that contained an internal proxy to all the services so fail-over would be easier to implement!
				Set<RemoteService> remoteServices = clientRegistryCache.get(serviceName);
				if (remoteServices != null && !remoteServices.isEmpty()) {
					// TODO - this should also probably check the current status of the service?
					RemoteService[] remoteServiceArray = remoteServices.toArray(new RemoteService[0]);
					RemoteService availableRemoteService = remoteServiceArray[this.randomNumber.nextInt(remoteServiceArray.length)];
					availableEndpoint = availableRemoteService.getEndpoint();
				}
			}
		}
		return availableEndpoint;
	}
	
	@Override
	public Endpoint getConfiguredEndpoint(ServiceConfiguration serviceConfiguration) {
		if (serviceConfiguration == null) {
			throw new IllegalArgumentException("serviceConfiguration cannot be null");
		}
		synchronized (serviceLock) {
			Endpoint localEndpoint = getLocalEndpoint(serviceConfiguration.getServiceName());
			if (localEndpoint != null && localEndpoint.getServiceConfiguration().equals(serviceConfiguration)) {
				return localEndpoint;
			}
			List<Endpoint> remoteEndpoints = getRemoteEndpoints(serviceConfiguration.getServiceName());
			for (Endpoint remoteEndpoint : remoteEndpoints) {
				if (remoteEndpoint.getServiceConfiguration().equals(serviceConfiguration)) {
					return remoteEndpoint;
				}
			}
		}
		return null;
	}

	
	@Override
	public Object getService(QName serviceName) {
		Endpoint availableEndpoint = getEndpoint(serviceName);
		if (availableEndpoint == null) {
			return null;
		}
		return availableEndpoint.getService();
	}

	@Override
	public ServiceConfiguration publishService(ServiceDefinition serviceDefinition, boolean synchronize) {
		if (serviceDefinition == null) {
			throw new IllegalArgumentException("serviceDefinition cannot be null");
		}
		LocalService localService = new LocalService(getInstanceId(), serviceDefinition);
		synchronized (serviceLock) {
			serviceExportManager.exportService(serviceDefinition);
			localServices.put(serviceDefinition.getServiceName(), localService);
		}
		if (synchronize) {
			synchronize();
		}
		return localService.getEndpoint().getServiceConfiguration();
	}

	@Override
	public List<ServiceConfiguration> publishServices(List<ServiceDefinition> serviceDefinitions, boolean synchronize) {
		if (serviceDefinitions == null) {
			throw new IllegalArgumentException("serviceDefinitions list cannot be null");
		}
		List<ServiceConfiguration> serviceConfigurations = new ArrayList<ServiceConfiguration>();
		synchronized (serviceLock) {
			for (ServiceDefinition serviceDefinition : serviceDefinitions) {
				ServiceConfiguration serviceConfiguration = publishService(serviceDefinition, false);
				serviceConfigurations.add(serviceConfiguration);
			}
		}
		if (synchronize) {
			synchronize();
		}
		return Collections.unmodifiableList(serviceConfigurations);
	}

	@Override
	public boolean removeService(QName serviceName, boolean synchronize) {
		if (serviceName == null) {
			throw new IllegalArgumentException("serviceName cannot be null");
		}
		boolean serviceRemoved = false;
		synchronized (serviceLock) {
			LocalService localService = localServices.remove(serviceName);
			serviceRemoved = localService != null;
			serviceExportManager.removeService(serviceName);
		}
		if (serviceRemoved && synchronize) {
			synchronize();
		}
		return serviceRemoved;
	}

	@Override
	public List<Boolean> removeServices(List<QName> serviceNames, boolean synchronize) {
		if (serviceNames == null) {
			throw new IllegalArgumentException("serviceNames cannot be null");
		}
		boolean serviceRemoved = false;
		List<Boolean> servicesRemoved = new ArrayList<Boolean>();
		synchronized (serviceLock) {
			for (QName serviceName : serviceNames) {
				serviceExportManager.removeService(serviceName);
				LocalService localService = localServices.remove(serviceName);
				if (localService != null) {
					servicesRemoved.add(Boolean.TRUE);
					serviceRemoved = true;
				} else {
					servicesRemoved.add(Boolean.FALSE);
				}
			}
		}
		if (serviceRemoved && synchronize) {
			synchronize();
		}
		return servicesRemoved;
	}

	@Override
	public void synchronize() {
		if (!isDevMode()) {
			synchronized (synchronizeLock) {
				List<LocalService> localServicesList;
				List<RemoteService> clientRegistryCacheList;
				synchronized (serviceLock) {
					// first, flatten the lists
					localServicesList = new ArrayList<LocalService>(this.localServices.values());
					clientRegistryCacheList = new ArrayList<RemoteService>();
					for (Set<RemoteService> remoteServices : this.clientRegistryCache.values()) {
						clientRegistryCacheList.addAll(remoteServices);
					}
				}
				CompleteServiceDiff serviceDiff = diffCalculator.diffServices(getInstanceId(), localServicesList, clientRegistryCacheList);
			
				RemoteServicesDiff remoteServicesDiff = serviceDiff.getRemoteServicesDiff();
				processRemoteServiceDiff(remoteServicesDiff);
			
				LocalServicesDiff localServicesDiff = serviceDiff.getLocalServicesDiff();
				processLocalServiceDiff(localServicesDiff);
			}
		}
	}
		
	protected void processRemoteServiceDiff(RemoteServicesDiff remoteServicesDiff) {
		// note that since there is a gap between when the original services are acquired, the diff, and this subsequent critical section
		// the list of local and client registry services could have changed, so that needs to be considered in the remaining code
		synchronized (serviceLock) {
			// first, let's update what we know about the remote services
			List<RemoteService> removedServices = remoteServicesDiff.getRemovedServices();
			for (RemoteService removedRemoteService : removedServices) {
				Set<RemoteService> remoteServiceSet = this.clientRegistryCache.get(removedRemoteService.getServiceName());
				if (remoteServiceSet != null) {
					boolean wasRemoved = remoteServiceSet.remove(removedRemoteService);
					if (!wasRemoved) {
						LOG.warn("Failed to remove remoteService during synchronization: " + removedRemoteService);
					}
				}
			}
			List<ServiceInfo> newServices = remoteServicesDiff.getNewServices();
			for (ServiceInfo newService : newServices) {
				Set<RemoteService> remoteServiceSet = clientRegistryCache.get(newService.getServiceName());
				if (remoteServiceSet == null) {
					remoteServiceSet = new HashSet<RemoteService>();
					clientRegistryCache.put(newService.getServiceName(), remoteServiceSet);
				}
				remoteServiceSet.add(new RemoteService(newService, this.serviceRegistry));
			}
		}
	}
	
	protected void processLocalServiceDiff(LocalServicesDiff localServicesDiff) {
		Set<String> removeServiceEndpointIds = new HashSet<String>();
		Set<ServiceEndpoint> publishServiceEndpoints = new HashSet<ServiceEndpoint>();
		for (ServiceInfo serviceToRemove : localServicesDiff.getServicesToRemoveFromRegistry()) {
			removeServiceEndpointIds.add(serviceToRemove.getServiceId());
		}
		for (LocalService localService : localServicesDiff.getLocalServicesToPublish()) {
			publishServiceEndpoints.add(localService.getServiceEndpoint());
		}
		if (!removeServiceEndpointIds.isEmpty() || !publishServiceEndpoints.isEmpty()) {
			this.serviceRegistry.removeAndPublish(removeServiceEndpointIds, publishServiceEndpoints);
		}
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	
	public void setDiffCalculator(ServiceRegistryDiffCalculator diffCalculator) {
		this.diffCalculator = diffCalculator;
	}
	
	public void setServiceExportManager(ServiceExportManager serviceExportManager) {
		this.serviceExportManager = serviceExportManager;
	}
	
	public void setScheduledPool(KSBScheduledPool scheduledPool) {
		this.scheduledPool = scheduledPool;
	}
	
}
