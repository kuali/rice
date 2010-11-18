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
package org.kuali.rice.ksb.messaging.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.ModuleConfigurer;
import org.kuali.rice.core.config.event.AfterStartEvent;
import org.kuali.rice.core.config.event.RiceConfigEvent;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.lifecycle.ServiceDelegatingLifecycle;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.SpringLoader;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.ksb.messaging.AlternateEndpoint;
import org.kuali.rice.ksb.messaging.AlternateEndpointLocation;
import org.kuali.rice.ksb.messaging.ServiceDefinition;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.messaging.serviceconnectors.HttpInvokerConnector;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.util.KSBConstants;
import org.quartz.Scheduler;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Used to configure the embedded workflow. This could be used to configure
 * embedded workflow programmatically but mostly this is a base class by which
 * to hang specific configuration behavior off of through subclassing
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class KSBConfigurer extends ModuleConfigurer {

	private List<ServiceDefinition> services = new ArrayList<ServiceDefinition>();
	
    private List<AlternateEndpointLocation> alternateEndpointLocations = new ArrayList<AlternateEndpointLocation>();

	private List<AlternateEndpoint> alternateEndpoints = new ArrayList<AlternateEndpoint>();

	private DataSource registryDataSource;

	private DataSource messageDataSource;
	
	private DataSource nonTransactionalMessageDataSource;

	private Scheduler exceptionMessagingScheduler;

	private PlatformTransactionManager platformTransactionManager;

	public KSBConfigurer() {
        super();
        setModuleName( "KSB" );
        setHasWebInterface(true);
        VALID_RUN_MODES.remove(EMBEDDED_RUN_MODE);
        VALID_RUN_MODES.remove( REMOTE_RUN_MODE );
        VALID_RUN_MODES.remove( THIN_RUN_MODE );
    }
	
	@Override
	public Config loadConfig(Config parentConfig) throws Exception {
		Config currentConfig = super.loadConfig(parentConfig);
		configureDataSource(currentConfig);
		configureBus(currentConfig);
		configureScheduler(currentConfig);
		configurePlatformTransactionManager(currentConfig);
		configureAlternateEndpoints(currentConfig);
		return currentConfig;
	}

	@Override
	public String getSpringFileLocations(){
	    String files = "classpath:org/kuali/rice/ksb/config/KSBSpringBeans.xml" + SpringLoader.SPRING_SEPARATOR_CHARACTER;
        
        if (OrmUtils.isJpaEnabled("rice.ksb")) {
            files += "classpath:org/kuali/rice/ksb/config/KSBJPASpringBeans.xml";
        }
        else {
            files += "classpath:org/kuali/rice/ksb/config/KSBOJBSpringBeans.xml";
        }
        
        if (Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.LOAD_KNS_MODULE_CONFIGURATION)).booleanValue()) {
        	files += SpringLoader.SPRING_SEPARATOR_CHARACTER + "classpath:org/kuali/rice/ksb/config/KSBModuleConfigurationSpringBeans.xml";
        }
        
        return files;
	}
	
	/**
	 * Returns true - KSB UI should always be included.
	 * 
	 * @see org.kuali.rice.core.config.ModuleConfigurer#shouldRenderWebInterface()
	 */
	@Override
	public boolean shouldRenderWebInterface() {
		return true;
	}
	
	@Override
	public ResourceLoader getResourceLoaderToRegister() throws Exception{
		ResourceLoader ksbRemoteResourceLoader = KSBResourceLoaderFactory.createRootKSBRemoteResourceLoader();
		ksbRemoteResourceLoader.start();
		return ksbRemoteResourceLoader;
	}
	
	@Override
	protected List<Lifecycle> loadLifecycles() throws Exception {
		List<Lifecycle> lifecycles = new LinkedList<Lifecycle>();

		// this validation of our service list needs to happen after we've
		// loaded our configs so it's a lifecycle
		lifecycles.add(new BaseLifecycle() {

			@Override
			public void start() throws Exception {
				// first check if we want to allow self-signed certificates for SSL communication
				if (Boolean.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.KSB_ALLOW_SELF_SIGNED_SSL)).booleanValue()) {
				    Protocol.registerProtocol("https", new Protocol("https",
					    (ProtocolSocketFactory) new EasySSLProtocolSocketFactory(), 443));
				}
	
				for (final ServiceDefinition serviceDef : KSBConfigurer.this.services) {
					serviceDef.validate();
				}
				super.start();
			}
		});
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.THREAD_POOL_SERVICE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.SCHEDULED_THREAD_POOL_SERVICE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.REPEAT_TOPIC_INVOKING_QUEUE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.OBJECT_REMOTER));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.BUS_ADMIN_SERVICE));
		lifecycles.add(new ServiceDelegatingLifecycle(KSBServiceLocator.REMOTED_SERVICE_REGISTRY));
		return lifecycles;
	}
	
	

	/**
     * Used to refresh the service registry after the Application Context is initialized.  This way any services that were exported on startup
     * will be available in the service registry once startup is complete.
     */
    @Override
    public void onEvent(RiceConfigEvent event) {
        if (event instanceof AfterStartEvent) {
            LOG.info("Refreshing Service Registry to export services to the bus.");
            KSBServiceLocator.getServiceDeployer().refresh();
        }
    }

    protected String getServiceNamespace(Config config) {
		if (StringUtils.isBlank(config.getServiceNamespace())) {
			throw new ConfigurationException("The 'service.namespace' property was not properly configured.");
		}
		return config.getServiceNamespace();
	}

	protected void configureBus(Config config) throws Exception {
		LOG.debug("Configuring services for Service Namespace " + ConfigContext.getCurrentContextConfig().getServiceNamespace() + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		configureServiceList(config, Config.BUS_DEPLOYED_SERVICES, getServices());
	}

	@SuppressWarnings("unchecked")
	protected void configureServiceList(Config config, String key, List services) throws Exception {
		LOG.debug("Configuring services for Service Namespace " + ConfigContext.getCurrentContextConfig().getServiceNamespace() + " using config for classloader " + ClassLoaderUtils.getDefaultClassLoader());
		List<ServiceDefinition> serviceDefinitions = (List<ServiceDefinition>) config.getObject(key);
		if (serviceDefinitions == null) {
			config.putObject(key, services);
		} else if (services != null) {
			LOG.debug("Services already exist.  Adding additional services");
			serviceDefinitions.addAll(services);
		}
	}

	protected void configureScheduler(Config config) {
		if (this.getExceptionMessagingScheduler() != null) {
			LOG.info("Configuring injected exception messaging Scheduler");
			config.putObject(KSBConstants.INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY, this.getExceptionMessagingScheduler());
		}
	}

	protected void configureDataSource(Config config) {
        if (getMessageDataSource() != null && getRegistryDataSource() == null) {
            throw new ConfigurationException("A message data source was defined but a registry data source was not defined.  Both must be specified.");
        }
        if (getMessageDataSource() == null && getRegistryDataSource() != null) {
            throw new ConfigurationException("A registry data source was defined but a message data source was not defined.  Both must be specified.");
        }

        if (getMessageDataSource() != null) {
            config.putObject(KSBConstants.KSB_MESSAGE_DATASOURCE, getMessageDataSource());
        }
        if (getNonTransactionalMessageDataSource() != null) {
            config.putObject(KSBConstants.KSB_MESSAGE_NON_TRANSACTIONAL_DATASOURCE, getNonTransactionalMessageDataSource());
        }
        if (getRegistryDataSource() != null) {
            config.putObject(KSBConstants.KSB_REGISTRY_DATASOURCE, getRegistryDataSource());
        }
    }

	protected void configurePlatformTransactionManager(Config config) {
		if (getPlatformTransactionManager() == null) {
			return;
		}
		config.putObject(RiceConstants.SPRING_TRANSACTION_MANAGER, getPlatformTransactionManager());
	}
	
	protected void configureAlternateEndpoints(Config config) {
		config.putObject(KSBConstants.KSB_ALTERNATE_ENDPOINT_LOCATIONS, getAlternateEndpointLocations());
		config.putObject(KSBConstants.KSB_ALTERNATE_ENDPOINTS, getAlternateEndpoints());
	}
	
	@Override
	public void stop() throws Exception {
		try {
			HttpInvokerConnector.shutdownIdleConnectionTimeout();
		} catch (Exception e) {
			LOG.error("Failed to shutdown idle connection timeout evictor thread.", e);
		}
	    super.stop();
	    cleanUpConfiguration();
	}
	
	/**
     * Because our configuration is global, shutting down Rice does not get rid of objects stored there.  For that reason
     * we need to manually clean these up.  This is most important in the case of the service bus because the configuration
     * is used to store services to be exported.  If we don't clean this up then a shutdown/startup within the same
     * class loading context causes the service list to be doubled and results in "multiple endpoint" error messages.
     *
     */
    protected void cleanUpConfiguration() {
        ConfigContext.getCurrentContextConfig().removeObject(Config.BUS_DEPLOYED_SERVICES);
        ConfigContext.getCurrentContextConfig().removeObject(KSBConstants.KSB_ALTERNATE_ENDPOINTS);
    }

	public List<ServiceDefinition> getServices() {
		return this.services;
	}

	public void setServices(List<ServiceDefinition> javaServices) {
		this.services = javaServices;
	}

	public DataSource getMessageDataSource() {
		return this.messageDataSource;
	}

	public void setMessageDataSource(DataSource messageDataSource) {
		this.messageDataSource = messageDataSource;
	}

    public DataSource getNonTransactionalMessageDataSource() {
        return this.nonTransactionalMessageDataSource;
    }

    public void setNonTransactionalMessageDataSource(DataSource nonTransactionalMessageDataSource) {
        this.nonTransactionalMessageDataSource = nonTransactionalMessageDataSource;
    }

    public DataSource getRegistryDataSource() {
		return this.registryDataSource;
	}

	public void setRegistryDataSource(DataSource registryDataSource) {
		this.registryDataSource = registryDataSource;
	}

	public Scheduler getExceptionMessagingScheduler() {
		return this.exceptionMessagingScheduler;
	}

	public void setExceptionMessagingScheduler(Scheduler exceptionMessagingScheduler) {
		this.exceptionMessagingScheduler = exceptionMessagingScheduler;
	}

	public PlatformTransactionManager getPlatformTransactionManager() {
		return platformTransactionManager;
	}

	public void setPlatformTransactionManager(PlatformTransactionManager springTransactionManager) {
		this.platformTransactionManager = springTransactionManager;
	}

    public List<AlternateEndpointLocation> getAlternateEndpointLocations() {
	return this.alternateEndpointLocations;
    }

    public void setAlternateEndpointLocations(List<AlternateEndpointLocation> alternateEndpointLocations) {
	this.alternateEndpointLocations = alternateEndpointLocations;
	}

    public List<AlternateEndpoint> getAlternateEndpoints() {
        return this.alternateEndpoints;
    }

    public void setAlternateEndpoints(List<AlternateEndpoint> alternateEndpoints) {
        this.alternateEndpoints = alternateEndpoints;
    }
    
}
