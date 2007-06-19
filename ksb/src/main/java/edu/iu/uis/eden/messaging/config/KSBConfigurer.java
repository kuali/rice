/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.messaging.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.bus.ojb.OjbConfigurer;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.ModuleConfigurer;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.lifecycle.ServiceDelegatingLifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.cache.RiceCacheAdministrator;
import edu.iu.uis.eden.messaging.ServiceDefinition;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Used to configure the embedded workflow.  This could be used to configure embedded workflow programmatically
 * but mostly this is a base class by which to hang specific configuration behavior off of through
 * subclassing
 *
 * @author rkirkend
 *
 */
public class KSBConfigurer extends ModuleConfigurer {

	private static final Logger LOG = Logger.getLogger(KSBConfigurer.class);
	
	private List<ServiceHolder> overrideServices;
	private List<ServiceDefinition> services = new ArrayList<ServiceDefinition>();

	private String serviceServletUrl;
	private String keystoreAlias;
	private String keystorePassword;
	private String keystoreFile;
	private String webservicesUrl;
	private String webserviceRetry;
	private RiceCacheAdministrator cache;

	private boolean isStarted = false;

	
	public Config loadConfig(Config parentConfig) throws Exception {
		LOG.info("Starting configuration of KEW for message entity " + getMessageEntity(parentConfig));
		Config currentConfig = Core.getCurrentContextConfig();
		configureBus(currentConfig);
		configureKeystore(currentConfig);
		if (getServiceServletUrl() != null) {
			currentConfig.overrideProperty("http.service.url", getServiceServletUrl());
		}
		return currentConfig;
	}
	
	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();
		
		
		//this validation of our service list needs to happen after we've loaded our configs so it's a lifecycle
		lifecycles.add(new Lifecycle() {
			boolean started = false;
			public boolean isStarted() {
				return this.started;
			}
			public void start() throws Exception {
			    for (final ServiceDefinition serviceDef : KSBConfigurer.this.services) {
			        serviceDef.validate();
			    }
				this.started = true;
			}
			public void stop() throws Exception {
			    this.started = false;
			}
		});
		lifecycles.add(new OjbConfigurer());
		lifecycles.add(KSBResourceLoaderFactory.createRootKSBResourceLoader());
		lifecycles.add(new ServicesOverrideLifecycle(this.getOverrideServices()));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.THREAD_POOL_SERVICE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.REPEAT_TOPIC_INVOKING_QUEUE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.OBJECT_REMOTER));
		if (getCache() != null) {
			lifecycles.add(getCache());
		}
    	lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.REMOTED_SERVICE_REGISTRY));
    	return lifecycles;
	}
	
	protected String getMessageEntity(Config config) {
		if (StringUtils.isBlank(config.getMessageEntity())) {
			throw new ConfigurationException("The 'message.entity' property was not properly configured.");
		}
		return config.getMessageEntity();
	}
	
	@SuppressWarnings("unchecked")
	protected void configureBus(Config config) throws Exception {
		LOG.debug("Configuring services for Message Entity " + Core.getCurrentContextConfig().getMessageEntity() + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		configureServiceList(config, Config.BUS_DEPLOYED_SERVICES, getServices());
	}

	@SuppressWarnings("unchecked")
	private void configureServiceList(Config config, String key, List services) throws Exception {
		LOG.debug("Configuring services for Message Entity " + Core.getCurrentContextConfig().getMessageEntity() + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		List<ServiceDefinition> serviceDefinitions = (List<ServiceDefinition>) config.getObject(key);
		if (serviceDefinitions == null) {
			config.getObjects().put(key, services);
		} else if (services != null) {
			LOG.debug("Services already exist.  Adding additional services");
			serviceDefinitions.addAll(services);
		}

		// if it's empty, then we want to be able to inherit it from the parent configuration
		if (!StringUtils.isEmpty(this.serviceServletUrl)) {
			config.getObjects().put(Config.SERVICE_SERVLET_URL, this.serviceServletUrl);
			config.overrideProperty(Config.SERVICE_SERVLET_URL, this.serviceServletUrl);
		}

	}
	
	protected void configureKeystore(Config config) {
		if (!StringUtils.isEmpty(this.keystoreAlias)) {
			config.getProperties().put(Config.KEYSTORE_ALIAS, this.keystoreAlias);
		}
		if (!StringUtils.isEmpty(this.keystorePassword)) {
			config.getProperties().put(Config.KEYSTORE_PASSWORD, this.keystorePassword);
		}
		if (!StringUtils.isEmpty(this.keystoreFile)) {
			config.getProperties().put(Config.KEYSTORE_FILE, this.keystoreFile);
		}
	}

	public void stop() throws Exception {
		try {
			GlobalResourceLoader.stop();
		} finally {
		    this.isStarted = false;
		}
	}

	public boolean isStarted() {
		return this.isStarted;
	}

	protected void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public List<ServiceDefinition> getServices() {
		return this.services;
	}

	public void setServices(List<ServiceDefinition> javaServices) {
		this.services = javaServices;
	}

	public String getKeystoreAlias() {
		return this.keystoreAlias;
	}

	public void setKeystoreAlias(String keystoreAlias) {
		this.keystoreAlias = keystoreAlias;
	}

	public String getKeystoreFile() {
		return this.keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	public String getKeystorePassword() {
		return this.keystorePassword;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public String getWebserviceRetry() {
		return this.webserviceRetry;
	}

	public void setWebserviceRetry(String webserviceRetry) {
		this.webserviceRetry = webserviceRetry;
	}

	public String getWebservicesUrl() {
		return this.webservicesUrl;
	}

	public void setWebservicesUrl(String webservicesUrl) {
		this.webservicesUrl = webservicesUrl;
	}

	public String getServiceServletUrl() {
		return this.serviceServletUrl;
	}

	public void setServiceServletUrl(String serviceServletUrl) {
		if (!StringUtils.isEmpty(serviceServletUrl) && !serviceServletUrl.endsWith("/")) {
			serviceServletUrl += "/";
		}
		this.serviceServletUrl = serviceServletUrl;
	}
	
	public List<ServiceHolder> getOverrideServices() {
		return this.overrideServices;
	}

	public void setOverrideServices(List<ServiceHolder> overrideServices) {
		this.overrideServices = overrideServices;
	}

	public RiceCacheAdministrator getCache() {
		return this.cache;
	}

	public void setCache(RiceCacheAdministrator cache) {
		this.cache = cache;
	}
}