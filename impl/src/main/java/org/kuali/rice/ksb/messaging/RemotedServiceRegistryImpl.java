/*
 * Copyright 2006-2011 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.ksb.messaging.callforwarding.ForwardedCallHandler;
import org.kuali.rice.ksb.messaging.callforwarding.ForwardedCallHandlerImpl;
import org.kuali.rice.ksb.messaging.service.ServiceRegistry;
import org.kuali.rice.ksb.messaging.serviceexporters.ServiceExporterFactory;
import org.kuali.rice.ksb.service.KSBContextServiceLocator;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.util.KSBConstants;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RemotedServiceRegistryImpl implements RemotedServiceRegistry, Runnable {

	private static final Logger LOG = Logger.getLogger(RemotedServiceRegistryImpl.class);

	private Map<QName, ServerSideRemotedServiceHolder> publishedServices = Collections.synchronizedMap(new HashMap<QName, ServerSideRemotedServiceHolder>());

	private Map<QName, ServerSideRemotedServiceHolder> publishedTempServices = Collections.synchronizedMap(new HashMap<QName, ServerSideRemotedServiceHolder>());

	/**
	 * A Map for encapsulating copies of the ServiceDefinition objects and their checksums, so that it is not necessary to regenerate the checksums
	 * during every registry refresh, and so that isSame() checks on non-serialized ServiceDefinitions can be performed properly to determine when
	 * the checksums require recomputation.
	 */
	private Map<QName, ServiceInfo> serviceInfoCopies = Collections.synchronizedMap(new HashMap<QName, ServiceInfo>());
	
	/**
	 * A Map for temporarily storing the values located in the serviceInfoCopies Map during a registry refresh.
	 */
	private Map<QName, ServiceInfo> serviceInfoCopyHolder = Collections.synchronizedMap(new HashMap<QName, ServiceInfo>());
	
	private String serverIp;
	
	/**
	 * lookup QNameS of published services from request URLs
	 */
	private ServiceNameFinder publishedServiceNameFinder = new ServiceNameFinder();

	/**
	 * lookup QNameS of published temp services from request URLs
	 */
	private ServiceNameFinder publishedTempServiceNameFinder = new ServiceNameFinder();

	private boolean started;

	private ScheduledFuture future;

	protected ServiceRegistry serviceRegistry;
	
	protected KSBContextServiceLocator serviceLocator;
	
	public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		((Controller) handler).handleRequest(request, response);
	}

	private void registerService(ServiceInfo entry, Object serviceImpl) throws Exception {
		ServerSideRemotedServiceHolder serviceHolder = ServiceExporterFactory.getServiceExporter(entry, serviceLocator).getServiceExporter(serviceImpl);
		this.publishedServices.put(entry.getQname(), serviceHolder);
		this.serviceInfoCopies.put(entry.getQname(), this.serviceInfoCopyHolder.get(entry.getQname()));
		this.publishedServiceNameFinder.register(entry, entry.getQname());
	}

	/**
	 * Constructs a new ServiceInfo object based on the given ServiceDefinition, and also creates/updates another ServiceInfo for encapsulating copies
	 * of the ServiceDefinition and its checksum. 
	 * 
	 * @param serviceDef The ServiceDefinition to encapsulate inside of a ServiceInfo object.
	 * @return A new ServiceInfo constructed from the provided ServiceDefinition and an associated ServiceInfo copy.
	 */
	private ServiceInfo createServiceInfoAndServiceInfoCopy(ServiceDefinition serviceDef) {
		ServiceInfo copiedInfo = this.serviceInfoCopies.get(serviceDef.getServiceName());
		
		if (copiedInfo == null) {
			// Create a new ServiceInfo copy if one does not exist.
			copiedInfo = new ServiceInfo();
			copiedInfo.setServiceDefinition((ServiceDefinition) ObjectUtils.deepCopy(serviceDef));
			copiedInfo.setChecksum(copiedInfo.objectToChecksum(serviceDef));
		} else if (!serviceDef.isSame(copiedInfo.getServiceDefinition(serviceLocator.getMessageHelper()))) {
			// Update the existing ServiceInfo copy if its ServiceDefinition copy fails an isSame() check with the given ServiceDefinition.
			copiedInfo.setServiceDefinition((ServiceDefinition) ObjectUtils.deepCopy(serviceDef));
			copiedInfo.setChecksum(copiedInfo.objectToChecksum(serviceDef));
		}
		this.serviceInfoCopyHolder.put(serviceDef.getServiceName(), copiedInfo);


        if (serviceDef.getServiceEndPoint() == null) {
            System.err.println("the service def:" + serviceDef);
            //serviceDef.validate();
        }
		return new ServiceInfo(serviceDef, this.serverIp, copiedInfo.getChecksum());
	}
	
	private ServiceInfo getForwardHandlerServiceInfo(ServiceDefinition serviceDef) {
		ForwardedCallHandler callHandler = new ForwardedCallHandlerImpl();
		
		ServiceDefinition serviceDefinition = new JavaServiceDefinition();
		serviceDefinition.setBusSecurity(serviceDef.getBusSecurity());
		if (serviceDef.getLocalServiceName() == null && serviceDef.getServiceName() != null) {
			serviceDefinition.setServiceName(new QName(serviceDef.getServiceName().getNamespaceURI(), serviceDef.getServiceName().getLocalPart() + KSBConstants.FORWARD_HANDLER_SUFFIX));
		} else {
			serviceDefinition.setLocalServiceName(serviceDef.getLocalServiceName() + KSBConstants.FORWARD_HANDLER_SUFFIX);
			serviceDefinition.setServiceNameSpaceURI(serviceDef.getServiceNameSpaceURI());
		}
		serviceDefinition.setMessageExceptionHandler(serviceDef.getMessageExceptionHandler());
		serviceDefinition.setMillisToLive(serviceDef.getMillisToLive());
		serviceDefinition.setPriority(serviceDef.getPriority());
		serviceDefinition.setQueue(serviceDef.getQueue());
		serviceDefinition.setRetryAttempts(serviceDef.getRetryAttempts());
		serviceDefinition.setService(callHandler);
		serviceDefinition.validate();

		return createServiceInfoAndServiceInfoCopy(serviceDefinition);
	}

	
	public void registerService(ServiceDefinition serviceDefinition, boolean forceRegistryRefresh) {
		if (serviceDefinition == null) {
			throw new RuntimeException("Service Definition is null");
		}
		final Config config = ConfigContext.getCurrentContextConfig();
		
		@SuppressWarnings("unchecked")
		List<ServiceDefinition> services = (List<ServiceDefinition>) config.getObject(Config.BUS_DEPLOYED_SERVICES);
		if (services == null) {
			services = new ArrayList<ServiceDefinition>();
			config.putObject(Config.BUS_DEPLOYED_SERVICES, services);
		}
		
		//removing any existing services already registered with the same QName - allows client apps to override rice pushlished services
		removeServicesWithName(serviceDefinition.getServiceName(), services);
		services.add(serviceDefinition);
		
		// force an immediate registry of the service
		if (forceRegistryRefresh) {
			run();
		}
	}
	
	/**
	 * removes any services from the list with the same QName.
	 * 
	 * @param name the QName to search for
	 * @param services the list of services
	 */
	private void removeServicesWithName(QName name, List<ServiceDefinition> services) {
		for (Iterator<ServiceDefinition> i = services.iterator(); i.hasNext(); ) {
			final ServiceDefinition service = i.next();
			if (service.getServiceName().equals(name)) {
				LOG.debug("removing existing service with QName: " + service.getServiceName());
				i.remove();
			}
		}
	}

	public void registerTempService(ServiceDefinition serviceDefinition, Object service) {
		ServiceInfo serviceInfo = new ServiceInfo(serviceDefinition);
		Object existingService = getService(serviceInfo.getQname());
		if (existingService != null) {
			throw new RuntimeException("Service with that name is already registered");
		}
		try {
			ServerSideRemotedServiceHolder serviceHolder = 
				ServiceExporterFactory.getServiceExporter(serviceInfo, serviceLocator).getServiceExporter(service);
			this.publishedTempServices.put(serviceInfo.getQname(), serviceHolder);
			this.publishedTempServiceNameFinder.register(serviceInfo, serviceInfo.getQname());
			
			LOG.debug("Registered temp service " + serviceDefinition.getServiceName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ServerSideRemotedServiceHolder getRemotedServiceHolder(QName qname) {
		final ServerSideRemotedServiceHolder serviceHolder = this.publishedServices.get(qname);
		return serviceHolder != null ? serviceHolder : this.publishedTempServices.get(qname);
	}

	public Object getService(QName qName, String url) {
		ServerSideRemotedServiceHolder serviceHolder = this.publishedServices.get(qName);
		if (serviceHolder != null && serviceHolder.getServiceInfo().getEndpointUrl().equals(url)) {
			return serviceHolder.getInjectedPojo();
		}

		serviceHolder = this.publishedTempServices.get(qName);
		if (serviceHolder != null && serviceHolder.getServiceInfo().getEndpointUrl().equals(url)) {
			return serviceHolder.getInjectedPojo();
		}

		return null;
	}

	public Object getLocalService(QName serviceName) {
		ServerSideRemotedServiceHolder serviceHolder = this.publishedServices.get(serviceName);
		if (serviceHolder == null) {
			return null;
		}
		return serviceHolder.getInjectedPojo();
	}

	public Object getService(QName qname) {
		ServiceHolder serviceHolder = getRemotedServiceHolder(qname);
		if (serviceHolder != null) {
		    	try {
			Object service = serviceHolder.getService();
			return service;
		    	} catch (Exception e) {
		    	    this.removeRemoteServiceFromRegistry(qname);
		}
		}
		if (!StringUtils.isEmpty(qname.getNamespaceURI())) {
			return getService(new QName(qname.getLocalPart()));
		}
		return null;
	}

	/**
	 * This method is used to obtain the qualified service name for the service deployed at the given url.
	 *  
	 * @param url
	 * @return the service
	 */
	public QName getServiceName(String url){
		QName qname = null;
		qname = this.publishedServiceNameFinder.lookup(url);

		// try temp services map
		if (qname == null) {
			qname = this.publishedTempServiceNameFinder.lookup(url);
		}
			
		return qname;
	}

	public void removeRemoteServiceFromRegistry(QName serviceName) {
		ServerSideRemotedServiceHolder serviceHolder;
		serviceHolder = this.publishedTempServices.remove(serviceName);
		if (serviceHolder != null){
			this.publishedTempServiceNameFinder.remove(serviceHolder.getServiceInfo().getEndpointUrl());
		}
	}

	public void refresh() {
	    run();
	}

	public synchronized void run() {
	    	String serviceNamespace = ConfigContext.getCurrentContextConfig().getServiceNamespace();
		LOG.debug("Checking for newly published services on service namespace " + serviceNamespace + " ...");
		this.serverIp = RiceUtilities.getIpNumber();

		String serviceServletUrl = (String) ConfigContext.getConfig(ClassLoaderUtils.getDefaultClassLoader()).getProperty(Config.SERVICE_SERVLET_URL);
		if (serviceServletUrl == null) {
			throw new RuntimeException("No service url provided to locate services.  This is configured in the KSBConfigurer.");
		}

		// First, we need to get the list of services that we should be publishing
		
		List<ServiceDefinition> javaServices = (List<ServiceDefinition>) ConfigContext.getCurrentContextConfig().getObject(Config.BUS_DEPLOYED_SERVICES);
		// convert the ServiceDefinitions into ServiceInfos for diff comparison
		List<ServiceInfo> configuredJavaServices = new ArrayList<ServiceInfo>();
		for (ServiceDefinition serviceDef: javaServices) {
            configuredJavaServices.add(createServiceInfoAndServiceInfoCopy(serviceDef));
			configuredJavaServices.add(getForwardHandlerServiceInfo(serviceDef));
		}

		List<ServiceInfo> configuredServices = new ArrayList<ServiceInfo>();
		configuredServices.addAll(configuredJavaServices);
		List<ServiceInfo> fetchedServices = null;
		
		// Next, let's find the services that we have already published in the registry
	 	
		if (ConfigContext.getCurrentContextConfig().getDevMode() || ConfigContext.getCurrentContextConfig().getBatchMode()) {
			fetchedServices = new ArrayList<ServiceInfo>();
		} else {
			//TODO we are not verifying that this read is not being done in dev mode in a test
			fetchedServices = this.getServiceRegistry().findLocallyPublishedServices(RiceUtilities.getIpNumber(), serviceNamespace);
		}
		
		RoutingTableDiffCalculator diffCalc = new RoutingTableDiffCalculator();
		diffCalc.setEnMessageHelper(serviceLocator.getMessageHelper());
		boolean needUpdated = diffCalc.calculateServerSideUpdateLists(configuredServices, fetchedServices);
		if (needUpdated) {
			boolean updateSRegTable = true;
	 	 	if (ConfigContext.getCurrentContextConfig().getDevMode() || ConfigContext.getCurrentContextConfig().getBatchMode()) {
	 	 		updateSRegTable = false;
	 	 	}
	 	 	if (updateSRegTable) {
				getServiceRegistry().saveEntries(diffCalc.getServicesNeedUpdated());
				getServiceRegistry().removeEntries(diffCalc.getServicesNeedRemoved());
			}
			this.publishedServices.clear();
			this.serviceInfoCopies.clear();
			publishServiceList(diffCalc.getMasterServiceList());
		} else if (this.publishedServices.isEmpty()) {
			publishServiceList(configuredServices);
		}
		this.serviceInfoCopyHolder.clear();
		LOG.debug("...Finished checking for remote services.");
	}

	private void publishServiceList(List<ServiceInfo> services) {
		for (ServiceInfo serviceInfo : services) {
			try {
				registerService(serviceInfo, serviceInfo.getServiceDefinition(serviceLocator.getMessageHelper()).getService());
			} catch (Exception e) {
				LOG.error("Encountered error registering service " + serviceInfo.getQname(), e);
				this.publishedServices.remove(serviceInfo.getQname());
				this.serviceInfoCopies.remove(serviceInfo.getQname());
				this.publishedServiceNameFinder.remove(serviceInfo.getEndpointUrl());
				continue;
			}
		}
	}

	public boolean isStarted() {
		return this.started;
	}

	public synchronized void start() throws Exception {
		if (isStarted()) {
			return;
		}
		LOG.info("Starting the Service Registry...");
		run();
		if (!ConfigContext.getCurrentContextConfig().getDevMode()) {
			int refreshRate = ConfigContext.getCurrentContextConfig().getRefreshRate();
			this.future = serviceLocator.getScheduledPool()==null?KSBServiceLocator.getScheduledPool().scheduleWithFixedDelay(this, 30, refreshRate, TimeUnit.SECONDS)
							:serviceLocator.getScheduledPool().scheduleWithFixedDelay(this, 30, refreshRate, TimeUnit.SECONDS);
		}
		this.started = true;
		LOG.info("...Service Registry successfully started.");
	}

	public void stop() throws Exception {
		LOG.info("Stopping the Service Registry...");
		// remove services from the bus
		if (this.future != null) {
			if (!this.future.cancel(false)) {
				LOG.warn("Failed to cancel the RemotedServiceRegistry.");
			}
			this.future = null;
		}
		List<ServiceInfo> fetchedServices = this.getServiceRegistry().findLocallyPublishedServices(RiceUtilities.getIpNumber(), ConfigContext.getCurrentContextConfig().getServiceNamespace());
		this.getServiceRegistry().markServicesDead(fetchedServices);
		this.publishedServices.clear();
		this.getPublishedTempServices().clear();
		this.serviceInfoCopies.clear();
		this.started = false;
		LOG.info("...Service Registry successfully stopped.");
	}

	public String getContents(String indent, boolean servicePerLine) {
		String content = indent + "RemotedServiceRegistryImpl services=";

		for (ServiceHolder serviceHolder : this.publishedServices.values()) {
			if (servicePerLine) {
				content += indent + "+++" + serviceHolder.getServiceInfo().toString() + "\n";
			} else {
				content += serviceHolder.getServiceInfo().toString() + ", ";
			}
		}
		return content;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry == null ? KSBServiceLocator.getServiceRegistry() : serviceRegistry;
	}

	public Map<QName, ServerSideRemotedServiceHolder> getPublishedServices() {
		if (!isStarted()) {
			try {
				start();
			} catch (Exception e) {
				throw new ConfigurationException(e);
			}
		}
		return this.publishedServices;
	}

	public Map<QName, ServerSideRemotedServiceHolder> getPublishedTempServices() {
		return this.publishedTempServices;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	/**
	 * @return the serviceLocator
	 */
	public KSBContextServiceLocator getServiceLocator() {
		return this.serviceLocator;
	}

	/**
	 * @param serviceLocator the serviceLocator to set
	 */
	public void setServiceLocator(KSBContextServiceLocator serviceLocator) {
		this.serviceLocator = serviceLocator;
	}
	
	
	/**
	 * Looks up service QNameS based on URL StringS.  API is Map-like, but non-service specific portions of the
	 * URL are trimmed prior to accessing its internal Map.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	private static class ServiceNameFinder {
	    
		/**
		 * A service path to service QName map
		 */
		private Map<String, QName> servicePathToQName = Collections.synchronizedMap(new HashMap<String, QName>());
		

		/**
		 * This method trims the endpoint url base ({@link Config#getEndPointUrl()}) base off of the full service URL, e.g.
		 * "http://kuali.edu/kr-dev/remoting/SomeService" -> "SomeService".  It makes an effort to do so even if the host
		 * and ip don't match what is in {@link Config#getEndPointUrl()} by stripping host/port info.
		 * 
		 * If the service URL contains the configured subpath for RESTful service, additional trimming is done to
		 * isolate the service name from the rest of the url.
		 * 
		 * @param url
		 * @return the service specific suffix.  If fullServiceUrl doesn't contain the endpoint url base,
		 * fullServiceUrl is returned unmodified.  
		 */
		private String trimServiceUrlBase(String url) {
			String trimmedUrl = 
				StringUtils.removeStart(url, ConfigContext.getCurrentContextConfig().getEndPointUrl());
			
			if (trimmedUrl.length() == url.length()) { // it didn't contain the endpoint url base.
				// Perhaps the incoming url has a different host (or the ip) or a different port.
				// Trim off the host & port, then trim off the common base.
				URI serviceUri = URI.create(url);
				URI endpointUrlBase = URI.create(ConfigContext.getCurrentContextConfig().getEndPointUrl());
				
				String reqPath = serviceUri.getPath();
				String basePath = endpointUrlBase.getPath();
				
				trimmedUrl = StringUtils.removeStart(reqPath, basePath);
			}
			
			return trimmedUrl;
		}
		
		/**
		 * adds a mapping from the service specific portion of the service URL to the service name.
		 * 
		 * @param serviceUrl
		 * @param serviceName
		 */
		public void register(ServiceInfo entry, QName serviceName) {
			String serviceUrlBase = trimServiceUrlBase(entry.getEndpointUrl());

			if (serviceUrlBase.endsWith("/"))
				serviceUrlBase = StringUtils.chop(serviceUrlBase);

			servicePathToQName.put(serviceUrlBase, serviceName);
		}
		
		/**
		 * removes the mapping (if one exists) for the service specific portion of this url.
		 * 
		 * @param serviceUrl
		 */
		public void remove(String serviceUrl) {
			servicePathToQName.remove(trimServiceUrlBase(serviceUrl));
		}
		
		/**
		 * gets the QName for the service
		 * 
		 * @param serviceUrl
		 * @return
		 */
		public QName lookup(String serviceUrl) {
			String serviceUrlBase = trimServiceUrlBase(serviceUrl);

			// First, make sure we don't have any query params
			if (serviceUrlBase.length() > 0 && serviceUrlBase.lastIndexOf('?') != -1) {
				serviceUrlBase = serviceUrlBase.substring(0, serviceUrlBase.lastIndexOf('?'));
			}

			QName qname = null;
			// Now, iterate backwards through the url, stripping off pieces until you match -- this should work for rest too
			while (qname == null) {
				qname = servicePathToQName.get(serviceUrlBase);

				int lastSeparatorIndex = serviceUrlBase.lastIndexOf('/');
				if (lastSeparatorIndex == -1)
					break;
				serviceUrlBase = serviceUrlBase.substring(0, lastSeparatorIndex);
			}

			return qname;
		}

	}

}
